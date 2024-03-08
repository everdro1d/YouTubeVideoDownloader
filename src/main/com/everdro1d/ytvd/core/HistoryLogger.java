package main.com.everdro1d.ytvd.core;

import com.everdro1d.libs.io.Files;
import main.com.everdro1d.ytvd.ui.HistoryWindow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import static main.com.everdro1d.ytvd.core.MainWorker.windows;

public class HistoryLogger {
    public static final String historyFileName = "history.txt";
    public static String historyFilePath;
    private final ArrayList<String[]> historyList;
    public static final String[] columnNames = {"Title", "URL", "Status", "Type", "Date"};
    public static final int colTitle = 0;
    public static final int colUrl = 1;
    public static final int colStatus = 2;
    public static final int colType = 3;
    public static final int colDate = 4;


    public HistoryLogger() {
        historyFilePath = getHistoryFilePath();
        if (MainWorker.debug) System.out.println("History File Path: " + historyFilePath);
        this.historyList = new ArrayList<>();
        loadHistoryFromFile();
    }

    public void logHistory(String[] data) {
        try (FileWriter writer = new FileWriter(historyFilePath, true)) {
            // Append data to the file
            writer.write(
                    data[colTitle] + ","
                    + data[colUrl] + ","
                    + data[colStatus] + ","
                    + data[colType] + ","
                    + data[colDate] +
                    System.lineSeparator());
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
        if (Files.isFileInUse(Paths.get(historyFilePath))) {
            System.err.println("[ERROR] History file is in use.");
            return;
        } else {
            if (MainWorker.debug) System.out.println("History file is not in use.");
        }

        // Clear the history file
        try {
            java.nio.file.Files.write(Paths.get(historyFilePath), new byte[0]);
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

        sortHistoryList(historyList, colDate, colType, false);
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
        sortHistoryList(historyList, colDate, colType, false);
    }

    public static String getHistoryFilePath() {
        String historyFilePath;

        String div = windows ? "\\" : "/";
        historyFilePath = MainWorker.jarPath + div + historyFileName;
        if (MainWorker.debug) System.out.println("History File Default Path: " + historyFilePath);

        Path filePath = Paths.get(historyFilePath);
        // check if history file exists
        if (!java.nio.file.Files.exists(filePath)) {
            try {
                java.nio.file.Files.createFile(filePath);
                if (MainWorker.windows) java.nio.file.Files.setAttribute(filePath, "dos:hidden", true);
                if (MainWorker.macOS) {
                    new ProcessBuilder("chflags", "hidden", filePath.toString()).start();
                }
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
     * @param primarySortType sort by:<p> <tab> 0 = title, 1 = url, 2 = status, 3 = type, 4 = date
     * @param secondarySortType sort by:<p> <tab> 0 = title, 1 = url, 2 = status, 3 = type, 4 = date
     * @param ascending whether to sort in ascending or descending order
     */
    public void sortHistoryList(ArrayList<String[]> historyList, int primarySortType, int secondarySortType, boolean ascending) {
        if (MainWorker.debug) {
            System.out.println("Sorting history list by column at index: " + primarySortType + " in "
                    + (ascending ? "ascending" : "descending") + " order.");
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

        if (primarySortType == colType) {
            // sort in ascending order if the primary sort column is type
            ascending = !ascending;
        }
        if (!ascending) { // ascending is default
            // reverse the list
            Collections.reverse(historyList);
        }
    }
}