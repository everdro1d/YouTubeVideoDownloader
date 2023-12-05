package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;

public class HistoryLogger {
    public static final String historyFileName = "history.txt";
    public static String historyFilePath;
    private final ArrayList<String[]> historyList;


    public HistoryLogger() {
        historyFilePath = getHistoryFilePath();
        if (MainWorker.debug) System.out.println("History File Path: " + historyFilePath);
        this.historyList = new ArrayList<>();
        loadHistoryFromFile();
    }

    public void logHistory(String[] data) {
        try (FileWriter writer = new FileWriter(historyFilePath, true)) {
            // Append data to the file
            writer.write(data[0] + "," + data[1] + "," + data[2] + "," + data[3] + System.lineSeparator());
        } catch (IOException e) {
            if (MainWorker.debug) e.printStackTrace(System.err);
        }

        // Update the in-memory history list
        historyList.add(data);
        HistoryWindow.historyList = getHistory();
    }

    public ArrayList<String[]> getHistory() {
        return new ArrayList<>(historyList);
    }

    public void clearHistory() {
        if (MainWorker.debug) System.out.println("Clearing history file: " + historyFilePath);

        // Check if the history file is in use or locked
        if (isFileInUse(Paths.get(historyFilePath))) {
            System.err.println("[ERROR] History file is in use.");
            return;
        } else {
            if (MainWorker.debug) System.out.println("History file is not in use.");
        }

        // Clear the history file
        try {
            Files.write(Paths.get(historyFilePath), new byte[0]);
            if (MainWorker.debug) System.out.println("History file cleared successfully.");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to clear history file:");
            if (MainWorker.debug) e.printStackTrace(System.err);
        }

        // Clear the in-memory history list
        historyList.clear();
        HistoryWindow.historyList.clear();
        if (MainWorker.debug) System.out.println("Cleared in-memory history lists.");
    }

    public void setHistoryFile(ArrayList<String[]> historyList) {
        if (MainWorker.debug) System.out.println("Setting history file: " + historyFilePath);

        clearHistory();

        int date = 3, type = 2;
        sortHistoryList(historyList, date, type, false);
        for (String[] data : historyList) {
            logHistory(data);
        }
        if (MainWorker.debug) System.out.println("Set history file.");
    }

    private void loadHistoryFromFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(historyFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into a String array
                String[] data = line.split(",");
                historyList.add(data);
            }
        } catch (IOException e) {
            if (MainWorker.debug) e.printStackTrace(System.err);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("[ERROR] Failed to close history file reader.");
                    if (MainWorker.debug) e.printStackTrace(System.err);
                }
            }
        }
        int date = 3, type = 2;
        sortHistoryList(historyList, date, type, false);
    }

    public static String getHistoryFilePath() {
        String jarPath;
        String historyFilePath;
        try {
            jarPath = Paths.get(HistoryLogger.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();
        } catch (URISyntaxException e) {
            if (MainWorker.debug) e.printStackTrace(System.err);
            System.err.println("[ERROR] Failed to get jar path.");
            return "";
        }

        historyFilePath = jarPath + "\\" + historyFileName;
        if (MainWorker.debug) System.out.println("History File Default Path: " + historyFilePath);

        Path filePath = Paths.get(historyFilePath);
        // check if history file exists
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
                Files.setAttribute(filePath, "dos:hidden", true);
                if (MainWorker.debug) System.out.println("Created history file at: " + historyFilePath);
            } catch (IOException e) {
                if (MainWorker.debug) e.printStackTrace(System.err);
                System.err.println("[ERROR] Failed to create history file.");
            }
        }

        return historyFilePath;
    }

    /**
     * Sorts the history list by the specified sort type. If there are multiple entries with the same sort type, they will be sorted by date using ascending.
     * @param historyList the history list to sort
     * @param primarySortType sort by:<p> <tab> 0 = url, 1 = status, 2 = type, 3 = date
     * @param secondarySortType sort by:<p> <tab> 0 = url, 1 = status, 2 = type, 3 = date
     * @param ascending whether to sort in ascending or descending order
     */
    public void sortHistoryList(ArrayList<String[]> historyList, int primarySortType, int secondarySortType, boolean ascending) {
        if (MainWorker.debug) {
            System.out.println("Sorting history list by column at index: " + primarySortType + " in " + (ascending ? "ascending" : "descending") + " order.");
        }
        historyList.sort((o1, o2) -> {
            int result, same = 0;
            result = o1[primarySortType].compareTo(o2[primarySortType]);

            if (result == same) {
                // sort by secondary sort column if the primary sort column is the same
                result = o1[secondarySortType].compareTo(o2[secondarySortType]);
            }
            return result;
        });

        if (primarySortType == 2) {
            // sort in ascending order if the primary sort column is type
            ascending = !ascending;
        }
        if (!ascending) { // ascending is default
            // reverse the list
            Collections.reverse(historyList);
        }
    }

    public static boolean isFileInUse(Path filePath) {
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.WRITE);
             FileLock lock = channel.tryLock()) {

            // If the lock is null, then the file is already locked
            return lock == null;

        } catch (IOException e) {
            // An exception occurred, which means the file is likely in use
            return true;
        }
    }
}