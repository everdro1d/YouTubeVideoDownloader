//TODO #1 allow user to change history file path
//TODO #2 sort by status needs to fix the order of the status
//TODO #3 add pages to the history list
//TODO #4 add opening link from history list in default browser
//TODO #5 add logHistory toggle checkBox in MainWindow

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
        if (MainWorker.debug) {
            System.out.println("History File Path: " + historyFilePath);
        }
        this.historyList = new ArrayList<>();
        loadHistoryFromFile();
    }

    public void logHistory(String[] data) {
        try (FileWriter writer = new FileWriter(historyFilePath, true)) {
            // Append data to the file
            writer.write(data[0] + "," + data[1] + "," + data[2]  + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

        // Update the in-memory history list
        historyList.add(data);
    }

    public ArrayList<String[]> getHistory() {
        return new ArrayList<>(historyList);
    }

    public void clearHistory() {
        if (MainWorker.debug) {
            System.out.println("Clearing history file: " + historyFilePath);
        }

        // Check if the history file is in use or locked
        if (isFileInUse(Paths.get(historyFilePath))) {
            System.err.println("[ERROR] History file is in use.");
            return;
        } else {
            if (MainWorker.debug) {
                System.out.println("History file is not in use.");
            }
        }

        // Clear the history file
        try {
            Files.write(Paths.get(historyFilePath), new byte[0]);
            if (MainWorker.debug) {
                System.out.println("History file cleared successfully.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to clear history file:");
            e.printStackTrace(System.err);
        }

        // Clear the in-memory history list
        historyList.clear();
        if (MainWorker.debug) {
            System.out.println("Cleared in-memory history list.");
        }
    }

    public void setHistoryFile(ArrayList<String[]> historyList) {
        clearHistory();

        int date = 2;
        sortHistoryList(historyList, date, false);
        for (String[] data : historyList) {
            logHistory(data);
        }
        System.out.println("Set history list file.");
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
                System.out.println("Added to in-memory history list: " + data[0] + " " + data[1] + " " + data[2]);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("[ERROR] Failed to close history file reader.");
                    e.printStackTrace(System.err);
                }
            }
        }
        int date = 2;
        sortHistoryList(historyList, date, false);
    }

    public static String getHistoryFilePath() {
        String jarPath;
        String historyFilePath;
        try {
            jarPath = Paths.get(HistoryLogger.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace(System.err);
            System.err.println("[ERROR] Failed to get jar path.");
            return "";
        }

        historyFilePath = jarPath + "\\" + historyFileName;
        if (MainWorker.debug) {
            System.out.println("History File Default Path: " + historyFilePath);
        }

        //TODO #1 allow user to change history file path

        Path filePath = Paths.get(historyFilePath);
        // check if history file exists
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
                Files.setAttribute(filePath, "dos:hidden", true);
                if (MainWorker.debug) {
                    System.out.println("Created history file at: " + historyFilePath);
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
                System.err.println("[ERROR] Failed to create history file.");
            }
        }

        return historyFilePath;
    }

    /**
     * Sorts the history list by the specified sort type. If there are multiple entries with the same sort type, they will be sorted by date using ascending.
     * @param historyList the history list to sort
     * @param sortType sort by:<p> <tab> 0 = url, 1 = status, 2 = date
     * @param ascending whether to sort in ascending or descending order
     */
    public void sortHistoryList(ArrayList<String[]> historyList, int sortType, boolean ascending) {
        if (MainWorker.debug) {
            System.out.println("Sorting history list by column at index: " + sortType + " in " + (ascending ? "ascending" : "descending") + " order.");
        }
        historyList.sort((o1, o2) -> {
            int result, same = 0, url = 0, date = 2;
            result = o1[sortType].compareTo(o2[sortType]); //

            if (result == same) {
                // sort by date or if the date is the same, then sort by url
                result = o1[date].compareTo(o2[date]);     // sort by date
                if (result == same) {                     // if the date is the same
                    result = o1[url].compareTo(o2[url]); // sort by url
                }
            }
            return result;
        });

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