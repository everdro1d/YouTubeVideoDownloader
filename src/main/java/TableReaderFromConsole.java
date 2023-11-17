package main.java;

import java.util.*;

import static java.util.Arrays.stream;

public class TableReaderFromConsole {

    public static void scannerTableMap(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            System.out.println("No output from process");
            return;
        }
        while (scanner.hasNextLine()) {
            //Skip lines until the line containing "ID" is found
            String s = scanner.nextLine();
            if (s.contains("[info] Available formats for")) {
                break;
            }
        }

        if (!scanner.hasNextLine()) {
            System.out.println("No output from process");
            return;
        }
        //Get the table from console
        String[][] table = parseTable(scanner);

        scanner.close();

        //Get the table data in a HashMap
        AdvancedSettings.tableMap = parseTableToMap(table);

        //Print the table
        //printTableMap(advancedSettings.tableMap);
    }

    public static Map<String, Map<String, String>> parseTableToMap(String[][] table) {
        //Make a HashMap to store the table data
        Map<String, Map<String, String>> tableMap = new HashMap<>();

        //Iterate over rows starting from second row (ignore header row)
        for (int row = 1; row < table.length; row++) {
            String idValue = table[row][0];

            //Make a HashMap to store the row data
            Map<String, String> rowMap = getRowMap(table, row);

            //Put the rowMap in the tableMap with the key being the value of the ID column
            tableMap.put(idValue, rowMap);
        }

        return tableMap;
    }

    public static Map<String, String> getRowMap(String[][] table, int row) {
        Map<String, String> rowMap = new HashMap<>();

        //Iterate over columns
        for (int column = 0; column < table[row].length; column++) {
            //Put the column data in the rowMap
            rowMap.put(table[0][column], table[row][column]);

            //Get the value from the table and put it into the rowMap
            String value = table[row][column]; // value is the value of the current cell
            String key = table[0][column];     // key is the value of the first row of the current column
            rowMap.put(key, value);            // example: rowMap.put("ID", "sb2");
        }
        return rowMap;
    }

    public static String[][] parseTable(Scanner scanner) {

        // Read the table header
        String headerString = scanner.nextLine();
        String[] headersIN = headerString.replaceAll("[│─|-]", "").split("\\s+");
        String[] headersOUT = stream(headersIN).filter(s -> !s.isEmpty()).toArray(String[]::new);

        //concat the 14th and 15th column headers
        headersOUT[13] = headersOUT[13] + " " + headersOUT[14];
        String[] headers = Arrays.copyOf(headersOUT, headersOUT.length - 1);

        int numColumns = Math.min(headers.length, 14);

        // Create a list to store rows
        List<String[]> rows = new ArrayList<>();

        // Read the rows
        while (scanner.hasNextLine()) {
            // Read the row and remove unwanted characters
            String rowString = scanner.nextLine().replaceAll("[─-]", "").trim();
            if (rowString.equals("EOI")) { // EOI = End Of Input
                break;
            }
            if (rowString.isEmpty()) { // Skip empty lines
                continue;
            }


            // Split the row into cells by counting characters from the start of the headerString
            // and using the length of the headerString as the end index
            String[] row = new String[numColumns];
            for (int i = 0; i < numColumns; i++) {
                int start = headerString.indexOf(headers[i]);

                int end = start + headers[i].length();
                if ( i == numColumns - 1) {
                    end = rowString.length();
                }

                // Adjust the start and end indexes for some columns
                {
                    if (i == 0) {                               // ID
                        end += 1;
                    } else if (i == 1) {                        // EXT
                        end += 2;
                    } else if (i == 5 || i == 6 || i == 9) {    // FILESIZE, TBR, VBR
                        start -= 2;
                    } else if (i == 8) {                        // VCODEC
                        end += 7;
                    } else if (i == 10) {                       // ACODEC
                        end += 4;
                    } else if (i == 11) {                       // ABR
                        start -= 1;
                    }

                    if ( end > rowString.length() ) {
                        end = rowString.length();
                    }

                    if ( start > rowString.length()) {
                        break;
                    }
                }

                row[i] = rowString.substring(start, end).trim();
            }

            // Replace empty cells with "ec"
            for (int i = 0; i < numColumns; i++) {
                if (row[i] == null || row[i].isEmpty()) {
                    row[i] = "ec";
                }
            }

            // Add the row to the list
            rows.add(row);
        }
        // Create the 2D array (+1 for the header row)
        String[][] table = new String[rows.size() + 1][numColumns];
        table[0] = Arrays.copyOf(headers, numColumns);

        // Copy the rows to the table
        for (int i = 0; i < rows.size(); i++) {
            table[i + 1] = rows.get(i);
        }
        return table;
    }

//    public static void printTableMap(Map<String, Map<String, String>> tableMap) {
//        // Print the tableMap
//        for (Map.Entry<String, Map<String, String>> entry : tableMap.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
//    }

}
