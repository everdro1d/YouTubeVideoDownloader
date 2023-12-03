package main.java;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
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
                protected String[] columnNames = {"URL", "Status", "Date"};
                protected int selectedRow;
        protected JPanel sidePanelLeft;
            protected JLabel labelLeft;
        protected JPanel sidePanelRight;
            protected JLabel labelRight;
        protected JPanel verticalPanelBottom;
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

        this.setSize(770, 500);
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
            sidePanelLeft.setPreferredSize(new Dimension(50, 0));
            mainPanel.add(sidePanelLeft, BorderLayout.WEST);
            {
                // create a label in the side panel
                labelLeft = new JLabel("Left");
                labelLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
                labelLeft.setHorizontalTextPosition(JLabel.CENTER);
                sidePanelLeft.add(labelLeft);
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
                historyTable.getColumnModel().getColumn(2).setPreferredWidth(150 - scrollPane.getVerticalScrollBar().getWidth());

                historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                historyTable.setShowGrid(true);
                scrollPane.setViewportView(historyTable);

                final boolean[] ascending = {true};
                historyTable.getTableHeader().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        int col = historyTable.columnAtPoint(e.getPoint());
                        String name = historyTable.getColumnName(col);
                        if (MainWorker.debug) {
                            System.out.println("Column index selected: [" + col + ", " + name + "]");
                        }

                        sortTable(col, ascending);
                    }
                });

                // row selection listener
                historyTable.getSelectionModel().addListSelectionListener(e -> {
                    // only selected event
                    if (!e.getValueIsAdjusting()) return;

                    selectedRow = historyTable.getSelectedRow();
                    if (MainWorker.debug) {
                        System.out.println("Row selected: " + selectedRow);
                    }
                });
            }

            // create a side panel on the right of the border panel
            sidePanelRight = new JPanel();
            sidePanelRight.setLayout(new BoxLayout(sidePanelRight, BoxLayout.Y_AXIS));
            sidePanelRight.setAlignmentY(Component.TOP_ALIGNMENT);
            sidePanelRight.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidePanelRight.setPreferredSize(new Dimension(50, 0));
            mainPanel.add(sidePanelRight, BorderLayout.EAST);
            {
                // create a label in the side panel
                labelRight = new JLabel("Right");
                labelRight.setAlignmentX(Component.CENTER_ALIGNMENT);
                labelRight.setHorizontalTextPosition(JLabel.CENTER);
                sidePanelRight.add(labelRight);
            }

            // create a vertical panel at the bottom of the border panel
            verticalPanelBottom = new JPanel();
            verticalPanelBottom.setLayout(new BoxLayout(verticalPanelBottom, BoxLayout.Y_AXIS));
            verticalPanelBottom.setPreferredSize(new Dimension(0, 50));
            mainPanel.add(verticalPanelBottom, BorderLayout.SOUTH);
            {
                // create Y spacing
                verticalPanelBottom.add(Box.createRigidArea(new Dimension(0, 15)));

                // create a button panel int the verticalPanel of the border panel
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                verticalPanelBottom.add(buttonPanel, BorderLayout.SOUTH);
                {
                    // create a clear history button in the button panel
                    clearButton = new JButton("Clear History");
                    buttonPanel.add(clearButton);
                    clearButton.addActionListener(e -> {
                        HistoryLogger historyLogger = new HistoryLogger();
                        historyLogger.clearHistory();
                        setHistoryTable();
                    });

                    // create a remove selection button in the button panel
                    removeButton = new JButton("Remove Selection");
                    buttonPanel.add(removeButton);
                    removeButton.addActionListener(e -> {
                        HistoryLogger historyLogger = new HistoryLogger();
                        // remove the selected row from the table
                        if (selectedRow != -1) {
                            tableModel.removeRow(selectedRow);
                            historyLogger.setHistoryFile(getTableAsList(tableModel));
                        }
                        scrollPane.getVerticalScrollBar().setEnabled(historyTable.getRowCount() > 13);
                    });

                    // create X spacing
                    buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

                    // create a close button in the button panel
                    closeButton = new JButton("Close");
                    buttonPanel.add(closeButton);
                    closeButton.addActionListener(e -> this.dispose());
                }
            }

        }

        setHistoryTable();
    }

    private void sortTable(int col, boolean[] ascending) {
        // change the column header to show the sort type and remove the sort type from the other columns
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            String columnName = historyTable.getColumnName(i);
            if (i == col) {
                if (ascending[0]) {
                    columnName += " ▲";
                } else {
                    columnName += " ▼";
                }
            } else {
                columnName = columnName.replace(" ▼", "").replace(" ▲", "");
            }
            historyTable.getColumnModel().getColumn(i).setHeaderValue(columnName);
        }

        HistoryLogger historyLogger = new HistoryLogger();
        ArrayList<String[]> tableList = getTableAsList(tableModel);
        historyLogger.sortHistoryList(tableList, col, ascending[0]);
        ascending[0] = !ascending[0];

        // clear the table
        tableModel.setRowCount(0);
        // add the sorted list to the table
        for (String[] data : tableList) {
            if (data != null) {
                tableModel.addRow(data);
            } else {
                System.err.println("[ERROR] Data is null.");
            }
        }

        // Repaint the table headers
        historyTable.getTableHeader().repaint();
        scrollPane.getVerticalScrollBar().setEnabled(historyTable.getRowCount() > 13);
    }

    private ArrayList<String[]> getTableAsList(TableModel tableModel) {
        ArrayList<String[]> tableList = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String[] data = new String[3];
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                data[j] = (String) tableModel.getValueAt(i, j);
            }
            tableList.add(data);
        }
        return tableList;
    }

    public void setHistoryTable() {
        HistoryLogger historyLogger = new HistoryLogger();
        ArrayList<String[]> historyList = historyLogger.getHistory();

        if (historyList.isEmpty()) {
            tableModel.setRowCount(0);
            if (MainWorker.debug) {
                System.out.println("List is empty. No data to display.");
            }
        } else {
            // sort the history list by date
            int date = 2;
            sortTable(date, new boolean[]{false});

            if (MainWorker.debug) {
                System.out.println("History List:");
            }
        }
        for (String[] data : historyList) {
            if (MainWorker.debug) {
                System.out.println(columnNames[0] + ": " + data[0] + " | " + columnNames[1] + ": " + data[1] + " | " + columnNames[2] + ": " + data[2]);
            }
            if (data != null) {
                tableModel.addRow(data);
            } else {
                System.err.println("[ERROR] Data is null.");
            }
        }

        scrollPane.getVerticalScrollBar().setEnabled(historyTable.getRowCount() > 13);
    }
}
