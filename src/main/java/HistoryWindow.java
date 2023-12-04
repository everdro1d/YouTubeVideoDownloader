package main.java;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

import static main.java.MainWindow.fontName;

public class HistoryWindow extends JDialog {
    protected JPanel mainPanel;
        protected JPanel topPanel;
            protected JLabel labelTitle;
            protected JScrollPane scrollPane;
                protected DefaultTableModel tableModel;
                protected DefaultTableCellRenderer cellRenderer;
                protected JTable historyTable;
                public ArrayList<String[]> historyList;
                protected String[] columnNames = {"URL", "Status", "Type", "Date"};
                protected int sortModeCol = 3;
                protected int selectedRow;
        protected JPanel sidePanelLeft;
        protected JPanel sidePanelRight;
            protected JLabel labelRight;
        protected JPanel verticalPanelBottom;
            protected JPanel pagePanel;
                protected JButton firstButton;
                protected JButton previousButton;
                protected JLabel labelPage;
                protected JLabel labelPageNumber;
                protected JLabel labelPageTotal;
                protected JButton nextButton;
                protected JButton lastButton;
            protected JPanel buttonPanel;
                protected JButton clearButton;
                protected JButton removeButton;
                protected JButton closeButton;

    public HistoryWindow(JFrame parent) {
        super(parent, "Download History", true);
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // All cells are uneditable
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);

        cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.LEFT);


        historyList = new HistoryLogger().getHistory();
        if (MainWorker.debug) {
            System.out.println("History List:");
            System.out.println(historyList);
        }

        this.setSize(940, 500);
        this.setResizable(false);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // create a border panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.add(mainPanel);
        {
            // create a top panel in the border panel
            topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setPreferredSize(new Dimension(0, 50));
            mainPanel.add(topPanel, BorderLayout.NORTH);
            {
                // create a label at the top of the border panel
                labelTitle = new JLabel("Download History");
                labelTitle.setFont(new Font(fontName, Font.BOLD, 24));
                labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                labelTitle.setHorizontalAlignment(JLabel.CENTER);
                topPanel.add(labelTitle, BorderLayout.NORTH);

                // create Y spacing
                topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }

            // create a side panel on the left of the border panel
            sidePanelLeft = new JPanel();
            sidePanelLeft.setLayout(new BoxLayout(sidePanelLeft, BoxLayout.Y_AXIS));
            sidePanelLeft.setAlignmentY(Component.TOP_ALIGNMENT);
            sidePanelLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidePanelLeft.setPreferredSize(new Dimension(25, 0));
            mainPanel.add(sidePanelLeft, BorderLayout.WEST);
            {

            }

            // create a JScrollPane in the center of the border panel
            scrollPane = new JScrollPane();
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getVerticalScrollBar().setEnabled(false);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            {
                // set the model to the table
                historyTable = new JTable(tableModel);
                historyTable.setFillsViewportHeight(true);
                historyTable.setRowHeight(25);
                historyTable.setFont(new Font(fontName, Font.PLAIN, 14));

                historyTable.setDefaultRenderer(Object.class, cellRenderer);

                historyTable.getTableHeader().setFont(new Font(fontName, Font.PLAIN, 16));
                historyTable.getTableHeader().setReorderingAllowed(false);
                historyTable.getTableHeader().setResizingAllowed(false);

                historyTable.getColumnModel().getColumn(0).setPreferredWidth(325);
                historyTable.getColumnModel().getColumn(1).setPreferredWidth(160);
                historyTable.getColumnModel().getColumn(2).setPreferredWidth(120);
                historyTable.getColumnModel().getColumn(3).setPreferredWidth(150 - scrollPane.getVerticalScrollBar().getWidth());

                historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                historyTable.setShowGrid(true);
                scrollPane.setViewportView(historyTable);

                final boolean[] ascending = {true};
                historyTable.getTableHeader().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        sortModeCol = historyTable.columnAtPoint(e.getPoint());
                        String name = historyTable.getColumnName(sortModeCol);
                        if (MainWorker.debug) {
                            System.out.println("Column index selected: [" + sortModeCol + ", " + name + "]");
                        }

                        // sort and set pages
                        sortHistoryList(sortModeCol, ascending);
                    }
                });

                // row selection listener
                historyTable.getSelectionModel().addListSelectionListener(e -> {
                    // only selected event
                    if (!e.getValueIsAdjusting()) return;

                    selectedRow = historyTable.getSelectedRow();
                    if (MainWorker.debug) {
                        System.out.println("Row selected: " + selectedRow + " Page: " + labelPageNumber.getText());
                    }
                });
            }

            // create a side panel on the right of the border panel
            sidePanelRight = new JPanel();
            sidePanelRight.setLayout(new BoxLayout(sidePanelRight, BoxLayout.Y_AXIS));
            sidePanelRight.setAlignmentY(Component.TOP_ALIGNMENT);
            sidePanelRight.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(sidePanelRight, BorderLayout.EAST);
            {
                // create a label in the side panel
                labelRight = new JLabel("Options:");
                labelRight.setAlignmentX(Component.CENTER_ALIGNMENT);
                labelRight.setHorizontalTextPosition(JLabel.CENTER);
                sidePanelRight.add(labelRight);

                // create Y spacing
                sidePanelRight.add(Box.createRigidArea(new Dimension(0, 5)));

                // create a button panel int the verticalPanel of the border panel
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
                buttonPanel.setMinimumSize(new Dimension(135, 0));
                buttonPanel.setPreferredSize(new Dimension(135, 0));
                sidePanelRight.add(buttonPanel);
                {
                    // create a clear history button in the button panel
                    clearButton = new JButton("Clear History");
                    clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    buttonPanel.add(clearButton);
                    clearButton.addActionListener(e -> {
                        HistoryLogger historyLogger = new HistoryLogger();
                        historyLogger.clearHistory();
                        setHistoryTable();
                    });

                    // create Y spacing
                    buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));

                    // create a remove selection button in the button panel
                    removeButton = new JButton("Remove Selection");
                    removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    buttonPanel.add(removeButton);
                    removeButton.addActionListener(e -> {
                        selectedRow = historyTable.getSelectedRow();
                        if (selectedRow == -1) {
                            // show an error dialog if no row is selected
                            JOptionPane.showMessageDialog(this,
                                    "No row selected. Please select a row and try again.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            HistoryLogger historyLogger = new HistoryLogger();

                            // calculate the actual index of the selected row in the full history list
                            int pageNum = Integer.parseInt(labelPageNumber.getText());
                            int maxRows = 25; // Maximum number of rows per page
                            int actualIndex = selectedRow + (pageNum - 1) * maxRows;

                            // remove the selected row from the full history list
                            if (actualIndex < historyList.size()) {
                                historyList.remove(actualIndex);
                                historyLogger.setHistoryFile(historyList);
                            }

                            // remove the selected row from the table
                            if (selectedRow != -1) {
                                tableModel.removeRow(selectedRow);
                            }

                            // update the table view
                            setTablePage(pageNum);

                            scrollPane.getVerticalScrollBar().setEnabled(historyTable.getRowCount() > 13);
                        }
                    });

                    // create Y spacing
                    buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));

                    // create a close button in the button panel
                    closeButton = new JButton("Close");
                    closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    closeButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                    buttonPanel.add(closeButton);
                    closeButton.addActionListener(e -> this.dispose());
                }
            }

            // create a vertical panel at the bottom of the border panel
            verticalPanelBottom = new JPanel();
            verticalPanelBottom.setLayout(new BoxLayout(verticalPanelBottom, BoxLayout.Y_AXIS));
            verticalPanelBottom.setPreferredSize(new Dimension(0, 35));
            mainPanel.add(verticalPanelBottom, BorderLayout.SOUTH);
            {
                // create page panel
                pagePanel = new JPanel();
                pagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                pagePanel.setAlignmentY(Component.TOP_ALIGNMENT);
                verticalPanelBottom.add(pagePanel);
                {
                    // create button to go to first page
                    firstButton = new JButton("<<");
                    firstButton.setFont(new Font(fontName, Font.PLAIN, 16));
                    firstButton.setEnabled(false);
                    firstButton.setMargin(new Insets(0,5,0,5));
                    pagePanel.add(firstButton);
                    firstButton.addActionListener(e -> setTablePage(1));

                    // create button to go to previous page
                    previousButton = new JButton("<");
                    previousButton.setFont(new Font(fontName, Font.PLAIN, 16));
                    previousButton.setEnabled(false);
                    previousButton.setMargin(new Insets(0,0,0,0));
                    pagePanel.add(previousButton);
                    previousButton.addActionListener(e -> {
                        int pageNum = Integer.parseInt(labelPageNumber.getText());
                        setTablePage(pageNum - 1);
                    });

                    // create a label in the page panel
                    labelPage = new JLabel("Page:");
                    labelPage.setFont(new Font(fontName, Font.PLAIN, 16));
                    pagePanel.add(labelPage);

                    // create a page number label in the page panel
                    labelPageNumber = new JLabel("1");
                    labelPageNumber.setFont(new Font(fontName, Font.PLAIN, 16));
                    pagePanel.add(labelPageNumber);

                    // create a page total label in the page panel
                    labelPageTotal = new JLabel("of 1");
                    labelPageTotal.setFont(new Font(fontName, Font.PLAIN, 16));
                    pagePanel.add(labelPageTotal);

                    // create next page button
                    nextButton = new JButton(">");
                    nextButton.setFont(new Font(fontName, Font.PLAIN, 16));
                    nextButton.setEnabled(false);
                    nextButton.setMargin(new Insets(0,0,0,0));
                    pagePanel.add(nextButton);
                    nextButton.addActionListener(e -> {
                        int pageNum = Integer.parseInt(labelPageNumber.getText());
                        setTablePage(pageNum + 1);
                    });

                    // create last page button
                    lastButton = new JButton(">>");
                    lastButton.setFont(new Font(fontName, Font.PLAIN, 16));
                    lastButton.setEnabled(false);
                    lastButton.setMargin(new Insets(0,5,0,5));
                    pagePanel.add(lastButton);
                    lastButton.addActionListener(e -> {
                        int pageNum = Integer.parseInt(labelPageTotal.getText().replace("of ", ""));
                        setTablePage(pageNum);
                    });

                    // create X spacing to center on the table
                    pagePanel.add(Box.createRigidArea(new Dimension(150, 0)));

                }
            }

            setHistoryTable();
        }
    }

    private void sortHistoryList(int col, boolean[] ascending) {
        HistoryLogger historyLogger = new HistoryLogger();

        updateColumnHeaders(ascending[0]);

        // sort the history list by the selected column
        int date = 3, type = 2, secondarySortType = date;
        if (col == date) secondarySortType = type;
        historyLogger.sortHistoryList(historyList, col, secondarySortType, ascending[0]);
        ascending[0] = !ascending[0];

        // Repaint the table headers
        historyTable.getTableHeader().repaint();
        scrollPane.getVerticalScrollBar().setEnabled(historyTable.getRowCount() > 13);

        // set the table to the first page
        setTablePage(1);
    }

    private void updateColumnHeaders(boolean ascending) {
        // change the column header to show the sort type and remove the sort type from the other columns
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            String columnName = historyTable.getColumnName(i);
            if (i == sortModeCol) {
                if (ascending) {
                    columnName += " ▲";
                } else {
                    columnName += " ▼";
                }
            } else {
                columnName = columnName.replace(" ▼", "").replace(" ▲", "");
            }
            historyTable.getColumnModel().getColumn(i).setHeaderValue(columnName);
        }
    }

    protected void setTablePage(int pageNum) {
        int maxRowsPerPage = 25;

        // get the total number of pages in the list
        int totalPages = (int) Math.ceil((double) historyList.size() / maxRowsPerPage);

        // set the page number labels
        labelPageNumber.setText(String.valueOf(pageNum));
        labelPageTotal.setText("of " + (totalPages == 0 ? (totalPages = 1) : totalPages));
        System.out.println("Page number: " + pageNum + " Total Pages: " + totalPages);

        // check to enable/disable the page buttons
        firstButton.setEnabled(pageNum != 1);
        previousButton.setEnabled(pageNum != 1);
        nextButton.setEnabled(pageNum != totalPages);
        lastButton.setEnabled(pageNum != totalPages);

        // clear the table
        tableModel.setRowCount(0);

        // add the rows to the table from the list based on the page number
        int start = (pageNum - 1) * maxRowsPerPage;
        int end = Math.min(pageNum * maxRowsPerPage, historyList.size());
        for (int i = start; i < end; i++) {
            tableModel.addRow(historyList.get(i));
        }

        // repaint the table headers
        historyTable.getTableHeader().repaint();

        // enable/disable the scroll bar
        scrollPane.getVerticalScrollBar().setEnabled(historyTable.getRowCount() > 13);
        scrollPane.getVerticalScrollBar().setValue(0);
    }

//    private ArrayList<String[]> getPageAsList(TableModel tableModel) {
//        ArrayList<String[]> tableList = new ArrayList<>();
//        for (int i = 0; i < tableModel.getRowCount(); i++) {
//            String[] data = new String[3];
//            for (int j = 0; j < tableModel.getColumnCount(); j++) {
//                data[j] = (String) tableModel.getValueAt(i, j);
//            }
//            tableList.add(data);
//        }
//        return tableList;
//    }

    public void setHistoryTable() {
        if (historyList.isEmpty()) {
            tableModel.setRowCount(0);
            if (MainWorker.debug) System.out.println("List is empty. No data to display.");
        } else {
            // sort the history list by date
            int date = 3;
            sortHistoryList(date, new boolean[]{false});

            if (MainWorker.debug) System.out.println("History List:");
        }
        for (String[] data : historyList) {
            if (MainWorker.debug) {
                System.out.println(
                        columnNames[0] + ": " + data[0] +
                                " | " + columnNames[1] + ": " + data[1] +
                                " | " + columnNames[2] + ": " + data[2] +
                                " | " + columnNames[2] + ": " + data[3]
                );
            }

            if (data != null) {
                tableModel.addRow(data);
            } else {
                System.err.println("[ERROR] Failed to set History Table. Data is null.");
            }
        }

        // set the table to the first page
        setTablePage(1);
    }
}
