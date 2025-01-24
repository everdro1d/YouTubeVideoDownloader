package main.com.everdro1d.ytvd.ui;

import com.everdro1d.libs.core.Utils;
import com.everdro1d.libs.swing.RequestFocusListener;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.DoNotAskAgainConfirmDialog;
import main.com.everdro1d.ytvd.core.HistoryLogger;
import main.com.everdro1d.ytvd.core.MainWorker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static main.com.everdro1d.ytvd.core.HistoryLogger.*;
import static main.com.everdro1d.ytvd.core.MainWorker.darkMode;
import static main.com.everdro1d.ytvd.core.MainWorker.localeManager;
import static main.com.everdro1d.ytvd.ui.MainWindow.fontName;
import static main.com.everdro1d.ytvd.ui.MainWindow.frame;

public class HistoryWindow extends JFrame {
    private static final int historyWindowWidth = 1300;
    private static final int historyWindowHeight = 550;

    public static JFrame historyFrame;
        private JPanel mainPanel;
            private JPanel topPanel;
                private JLabel labelTitle;
                    private String titleText = "Download History";
                protected static CustomSeparator separatorHistoryTitle;
            private JScrollPane scrollPane;
                private DefaultTableModel tableModel;
                private DefaultTableCellRenderer cellRenderer;
                private JTable historyTable;
                    private String[] columnNames = {"Title", "URL", "Status", "Type", "Date"};
                    private JPopupMenu tablePopupMenu;
                    private String[] tablePopupMenuItems = {"Open Link", "Remove Entry", "Insert URL"};
                        private String tablePopupMenuCopyItem = "Copy";
                        private String[] copySubMenuItems = {"All", "Title", "URL", "Status", "Type", "Date"};
                public static ArrayList<String[]> historyList;
                private int sortModeCol = colDate;
                private int selectedRow;
            private JPanel sidePanelLeft;
            private JPanel sidePanelRight;
                private JLabel labelRight;
                    private String optionsLabelText = "Options";
                private JPanel buttonPanel;
                    private JButton openLinkButton;
                        private String openLinkButtonText = "Open Link";
                    private JButton clearButton;
                        private String clearButtonText = "Clear History";
                        private String clearHistoryDialogMessageText = "Are you sure you want to clear the history?";
                        private String clearHistoryDialogTitleText = "Clear History";
                    private JButton removeButton;
                        private String removeButtonText = "Remove Entry";
                        private String removeRowErrorDialogTitleText = "Remove Entry Error";
                        private String removeRowConfirmDialogMessageText = "Are you sure you want to remove the selected entry?";
                        private String removeRowConfirmDialogTitleText = "Remove Entry";
                    private JButton insertButton;
                        private String insertButtonText = "Insert URL";
                    private JCheckBox closeAfterInsert;
                        private String closeAfterInsertCheckBoxText = "Close History Window After Inserting URL";
                    public static String noRowSelectedErrorDialogMessageText = "No row selected. Please select a row and try again.";
                    public static String noRowSelectedErrorDialogTitleText = "Error!";
            private JPanel verticalPanelBottom;
                private JPanel pagePanel;
                    private JButton firstButton;
                    private JButton previousButton;
                    private JLabel labelPage;
                        private String pageLabelText = "Page";
                    private JLabel labelPageNumber;
                    private JLabel labelPageTotal;
                        private String ofText = "of";
                    private JButton nextButton;
                    private JButton lastButton;
                    protected static JButton closeButton;
                        private String closeButtonText = "Close";

    public HistoryWindow() {
        super();

        // if the locale does not contain the class, add it and it's components
        if (!localeManager.getClassesInLocaleMap().contains("HistoryWindow")) {
            addClassToLocale();
        }
        useLocale();

        initializeTableModel();
        initializeCellRenderer();

        historyList = new HistoryLogger().getHistory();

        initializeWindowProperties();
        initializeHistoryWindowGUI();

        SwingGUI.setHandCursorToClickableComponents(this);
    }

    private void addClassToLocale() {
        Map<String, Map<String, String>> classMap = new TreeMap<>();
            classMap.put("Main", new TreeMap<>());
            Map<String, String> mainMap = classMap.get("Main");
                mainMap.put("titleText", titleText);
                mainMap.put("optionsLabelText", optionsLabelText);
                mainMap.put("openLinkButtonText", openLinkButtonText);
                mainMap.put("clearButtonText", clearButtonText);
                mainMap.put("clearHistoryDialogMessageText", clearHistoryDialogMessageText);
                mainMap.put("clearHistoryDialogTitleText", clearHistoryDialogTitleText);
                mainMap.put("removeButtonText", removeButtonText);
                mainMap.put("removeRowErrorDialogTitleText", removeRowErrorDialogTitleText);
                mainMap.put("removeRowConfirmDialogMessageText", removeRowConfirmDialogMessageText);
                mainMap.put("removeRowConfirmDialogTitleText", removeRowConfirmDialogTitleText);
                mainMap.put("insertButtonText", insertButtonText);
                mainMap.put("closeAfterInsertCheckBoxText", closeAfterInsertCheckBoxText);
                mainMap.put("pageLabelText", pageLabelText);
                mainMap.put("ofText", ofText);
                mainMap.put("closeButtonText", closeButtonText);
                mainMap.put("noRowSelectedErrorDialogMessageText", noRowSelectedErrorDialogMessageText);
                mainMap.put("noRowSelectedErrorDialogTitleText", noRowSelectedErrorDialogTitleText);

            classMap.put("ColumnHeaderNames", new TreeMap<>());
            Map<String, String> columnHeaderNamesMap = classMap.get("ColumnHeaderNames");
                columnHeaderNamesMap.put("Title", columnNames[0]);
                columnHeaderNamesMap.put("URL", columnNames[1]);
                columnHeaderNamesMap.put("Status", columnNames[2]);
                columnHeaderNamesMap.put("Type", columnNames[3]);
                columnHeaderNamesMap.put("Date", columnNames[4]);

            classMap.put("TablePopupMenu", new TreeMap<>());
            Map<String, String> tablePopupMenuMap = classMap.get("TablePopupMenu");
                tablePopupMenuMap.put("tablePopupMenuOpenItem", tablePopupMenuItems[0]);
                tablePopupMenuMap.put("tablePopupMenuRemoveItem", tablePopupMenuItems[1]);
                tablePopupMenuMap.put("tablePopupMenuInsertItem", tablePopupMenuItems[2]);
                tablePopupMenuMap.put("tablePopupMenuCopyItem", tablePopupMenuCopyItem);

            classMap.put("CopySubMenuMap", new TreeMap<>());
                Map<String, String> copySubMenuMap = classMap.get("CopySubMenuMap");
                    copySubMenuMap.put("All", copySubMenuItems[0]);
                    copySubMenuMap.put("Title", copySubMenuItems[1]);
                    copySubMenuMap.put("URL", copySubMenuItems[2]);
                    copySubMenuMap.put("Status", copySubMenuItems[3]);
                    copySubMenuMap.put("Type", copySubMenuItems[4]);
                    copySubMenuMap.put("Date", copySubMenuItems[5]);

        localeManager.addClassSpecificMap("HistoryWindow", classMap);
    }

    private void useLocale() {
        Map<String, Map<String, String>> classMap = localeManager.getClassSpecificMap("HistoryWindow");
        Map<String, String> mainMap = classMap.get("Main");
            titleText = mainMap.getOrDefault("titleText", titleText);
            optionsLabelText = mainMap.getOrDefault("optionsLabelText", optionsLabelText);
            openLinkButtonText = mainMap.getOrDefault("openLinkButtonText", openLinkButtonText);
            clearButtonText = mainMap.getOrDefault("clearButtonText", clearButtonText);
            clearHistoryDialogMessageText = mainMap.getOrDefault("clearHistoryDialogMessageText", clearHistoryDialogMessageText);
            clearHistoryDialogTitleText = mainMap.getOrDefault("clearHistoryDialogTitleText", clearHistoryDialogTitleText);
            removeButtonText = mainMap.getOrDefault("removeButtonText", removeButtonText);
            removeRowErrorDialogTitleText = mainMap.getOrDefault("removeRowErrorDialogTitleText", removeRowErrorDialogTitleText);
            removeRowConfirmDialogMessageText = mainMap.getOrDefault("removeRowConfirmDialogMessageText", removeRowConfirmDialogMessageText);
            removeRowConfirmDialogTitleText = mainMap.getOrDefault("removeRowConfirmDialogTitleText", removeRowConfirmDialogTitleText);
            insertButtonText = mainMap.getOrDefault("insertButtonText", insertButtonText);
            closeAfterInsertCheckBoxText = mainMap.getOrDefault("closeAfterInsertCheckBoxText", closeAfterInsertCheckBoxText);
            pageLabelText = mainMap.getOrDefault("pageLabelText", pageLabelText);
            ofText = mainMap.getOrDefault("ofText", ofText);
            closeButtonText = mainMap.getOrDefault("closeButtonText", closeButtonText);
            noRowSelectedErrorDialogMessageText = mainMap.getOrDefault("noRowSelectedErrorDialogMessageText", noRowSelectedErrorDialogMessageText);
            noRowSelectedErrorDialogTitleText = mainMap.getOrDefault("noRowSelectedErrorDialogTitleText", noRowSelectedErrorDialogTitleText);

        Map<String, String> columnHeaderNamesMap = classMap.get("ColumnHeaderNames");
            columnNames[0] = columnHeaderNamesMap.getOrDefault("Title", columnNames[0]);
            columnNames[1] = columnHeaderNamesMap.getOrDefault("URL", columnNames[1]);
            columnNames[2] = columnHeaderNamesMap.getOrDefault("Status", columnNames[2]);
            columnNames[3] = columnHeaderNamesMap.getOrDefault("Type", columnNames[3]);
            columnNames[4] = columnHeaderNamesMap.getOrDefault("Date", columnNames[4]);

        Map<String, String> tablePopupMenuMap = classMap.get("TablePopupMenu");
            tablePopupMenuItems[0] = tablePopupMenuMap.getOrDefault("tablePopupMenuOpenItem", tablePopupMenuItems[0]);
            tablePopupMenuItems[1] = tablePopupMenuMap.getOrDefault("tablePopupMenuRemoveItem", tablePopupMenuItems[1]);
            tablePopupMenuItems[2] = tablePopupMenuMap.getOrDefault("tablePopupMenuInsertItem", tablePopupMenuItems[2]);
            tablePopupMenuCopyItem = tablePopupMenuMap.getOrDefault("tablePopupMenuCopyItem", tablePopupMenuCopyItem);

        Map<String, String> copySubMenuMap = classMap.get("CopySubMenuMap");
            copySubMenuItems[0] = copySubMenuMap.getOrDefault("All", copySubMenuItems[0]);
            copySubMenuItems[1] = copySubMenuMap.getOrDefault("Title", copySubMenuItems[1]);
            copySubMenuItems[2] = copySubMenuMap.getOrDefault("URL", copySubMenuItems[2]);
            copySubMenuItems[3] = copySubMenuMap.getOrDefault("Status", copySubMenuItems[3]);
            copySubMenuItems[4] = copySubMenuMap.getOrDefault("Type", copySubMenuItems[4]);
            copySubMenuItems[5] = copySubMenuMap.getOrDefault("Date", copySubMenuItems[5]);
    }

    private void initializeWindowProperties() {
        historyFrame = this;
        this.setSize(historyWindowWidth, historyWindowHeight);
        this.setResizable(false);
        this.setTitle(titleText);
        this.setLocationRelativeTo(frame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initializeCellRenderer() {
        cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.LEFT);
    }

    private void initializeTableModel() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // All cells are un-editable
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);
    }

    private void initializeHistoryWindowGUI() {
        // create a border panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.add(mainPanel);
        {
            // create a top panel in the border panel
            topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setPreferredSize(new Dimension(0, 55));
            mainPanel.add(topPanel, BorderLayout.NORTH);
            {
                // create a label at the top of the border panel
                labelTitle = new JLabel(titleText);
                labelTitle.setFont(new Font(fontName, Font.BOLD, 24));
                labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                labelTitle.setHorizontalAlignment(JLabel.CENTER);
                topPanel.add(labelTitle, BorderLayout.NORTH);

                // create Y spacing
                topPanel.add(Box.createRigidArea(new Dimension(0, 5)));

                // create a separator at the top of the border panel
                separatorHistoryTitle = new CustomSeparator(true, 4, 3);
                separatorHistoryTitle.setBackground(new Color(darkMode ? 0x595959 : 0xc2c2c2));
                topPanel.add(separatorHistoryTitle);

            }

            // create a side panel on the left of the border panel
            sidePanelLeft = new JPanel();
            sidePanelLeft.setLayout(new BoxLayout(sidePanelLeft, BoxLayout.Y_AXIS));
            sidePanelLeft.setAlignmentY(Component.TOP_ALIGNMENT);
            sidePanelLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidePanelLeft.setPreferredSize(new Dimension(25, 0));
            mainPanel.add(sidePanelLeft, BorderLayout.WEST);
            {
                //nada
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

                historyTable.getColumnModel().getColumn(colTitle).setPreferredWidth(345);
                historyTable.getColumnModel().getColumn(colUrl).setPreferredWidth(325);
                historyTable.getColumnModel().getColumn(colStatus).setPreferredWidth(160);
                historyTable.getColumnModel().getColumn(colType).setPreferredWidth(120);
                historyTable.getColumnModel().getColumn(colDate).setPreferredWidth(150 - scrollPane.getVerticalScrollBar().getWidth());

                historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                historyTable.setRowSelectionAllowed(true);
                historyTable.setColumnSelectionAllowed(false);

                historyTable.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                //disable component focus border
                historyTable.setFocusable(false);
                historyTable.setShowGrid(true);
                scrollPane.setViewportView(historyTable);

                final boolean[] ascending = {true};
                historyTable.getTableHeader().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        sortModeCol = historyTable.columnAtPoint(e.getPoint());
                        String name = historyTable.getColumnName(sortModeCol);
                        if (MainWorker.debug) {
                            System.out.println("Column index selected: [" + sortModeCol + ", " + name + "]");
                        }

                        // sort and set pages
                        sortHistoryList(sortModeCol, ascending);
                    }
                });

                // row selection listener include both clicks
                historyTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int row = historyTable.rowAtPoint(e.getPoint());
                        System.out.println("Row at point: " + row);
                        if (row >= 0) {
                            historyTable.setRowSelectionInterval(row, row);
                            selectedRow = historyTable.getSelectedRow();
                            // check if double click
                            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                                if (MainWorker.debug) System.out.println("Double click on row: " + row);
                                MainWorker.openLinkFromTable(HistoryWindow.this, historyTable, selectedRow);

                            } else if (SwingUtilities.isRightMouseButton(e)) {
                                if (MainWorker.debug) System.out.println("Right click on row: " + row);
                                tablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
                            }
                        } else {
                            historyTable.clearSelection();
                        }
                    }
                });

                historyTable.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int row = historyTable.rowAtPoint(e.getPoint());
                        if (row > -1) {
                            historyTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        } else {
                            historyTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                });

                tablePopupMenu = new JPopupMenu();
                {
                    JMenu copySubMenu = getCopySubMenu();
                    tablePopupMenu.add(copySubMenu);

                    ActionListener[] actions = {
                            e -> {
                                System.out.println("POPUPMENU: Open Link selected");
                                selectedRow = historyTable.getSelectedRow();
                                MainWorker.openLinkFromTable(HistoryWindow.this, historyTable, selectedRow);
                            },
                            e -> {
                                System.out.println("POPUPMENU: Remove Entry selected");
                                selectedRow = historyTable.getSelectedRow();
                                removeHistoryRow(selectedRow);
                            },
                            e -> {
                                System.out.println("POPUPMENU: Insert URL selected");
                                selectedRow = historyTable.getSelectedRow();
                                MainWorker.insertURL(HistoryWindow.this, historyTable, selectedRow);
                            }
                    };

                    for (int i = 0; i < tablePopupMenuItems.length; i++) {
                        JMenuItem menuItem = new JMenuItem(tablePopupMenuItems[i]);
                        menuItem.addActionListener(actions[i]);
                        tablePopupMenu.add(menuItem);
                        menuItem.setFont(new Font(fontName, Font.PLAIN, 14));
                    }
                }

            }

            // create a side panel on the right of the border panel
            sidePanelRight = new JPanel();
            sidePanelRight.setLayout(new BoxLayout(sidePanelRight, BoxLayout.Y_AXIS));
            sidePanelRight.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            sidePanelRight.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(sidePanelRight, BorderLayout.EAST);
            {
                // create a label in the side panel
                labelRight = new JLabel(optionsLabelText);
                labelRight.setAlignmentX(Component.CENTER_ALIGNMENT);
                labelRight.setHorizontalTextPosition(JLabel.CENTER);
                sidePanelRight.add(labelRight);

                // create Y spacing
                sidePanelRight.add(Box.createRigidArea(new Dimension(0, 5)));

                // create a button panel int the verticalPanel of the border panel
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
                buttonPanel.setMinimumSize(new Dimension(135, 376));
                buttonPanel.setPreferredSize(new Dimension(135, 376));
                sidePanelRight.add(buttonPanel);
                {
                    // create a open link button in the button panel
                    openLinkButton = new JButton(openLinkButtonText);
                    openLinkButton.setFont(new Font(fontName, Font.PLAIN, 14));
                    openLinkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    openLinkButton.setMinimumSize(new Dimension(125, 25));
                    openLinkButton.setMaximumSize(new Dimension(125, 25));
                    buttonPanel.add(openLinkButton);

                    openLinkButton.addActionListener(e -> {
                        if (MainWorker.debug) System.out.println("Open Link button pressed.");
                        selectedRow = historyTable.getSelectedRow();
                        MainWorker.openLinkFromTable(this, historyTable, selectedRow);
                    });

                    // create a Y spacing
                    buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                    // create a clear history button in the button panel
                    clearButton = new JButton(clearButtonText);
                    clearButton.setFont(new Font(fontName, Font.PLAIN, 14));
                    clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    clearButton.setMinimumSize(new Dimension(125, 25));
                    clearButton.setMaximumSize(new Dimension(125, 25));
                    buttonPanel.add(clearButton);

                    clearButton.addActionListener(e -> {
                        if (MainWorker.debug) System.out.println("Clear button pressed.");

                        int confirm = DoNotAskAgainConfirmDialog.showConfirmDialog(this,
                                clearHistoryDialogMessageText,
                                clearHistoryDialogTitleText, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                                MainWorker.prefs, "doNotAskAgainClearButton"); //TODO - update for localeManager

                        if (confirm == JOptionPane.YES_OPTION) {
                            HistoryLogger historyLogger = new HistoryLogger();
                            historyLogger.clearHistory();
                            setHistoryTable();
                        } else {
                            if (MainWorker.debug) System.out.println("Clear history was cancelled.");
                        }
                    });

                    // create Y spacing
                    buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                    // create a remove selection button in the button panel
                    removeButton = new JButton(removeButtonText);
                    removeButton.setFont(new Font(fontName, Font.PLAIN, 14));
                    removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    removeButton.setMinimumSize(new Dimension(125, 25));
                    removeButton.setMaximumSize(new Dimension(125, 25));
                    buttonPanel.add(removeButton);

                    removeButton.addActionListener(e -> {
                        if (MainWorker.debug) System.out.println("Remove button pressed.");
                        selectedRow = historyTable.getSelectedRow();
                        removeHistoryRow(selectedRow);
                    });

                    // create Y spacing
                    buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                    // create an insert url button in the button panel
                    insertButton = new JButton(insertButtonText);
                    insertButton.setFont(new Font(fontName, Font.PLAIN, 14));
                    insertButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    insertButton.setMinimumSize(new Dimension(125, 25));
                    insertButton.setMaximumSize(new Dimension(125, 25));
                    buttonPanel.add(insertButton);

                    insertButton.addActionListener(e -> {
                        if (MainWorker.debug) System.out.println("Insert button pressed.");
                        selectedRow = historyTable.getSelectedRow();
                        MainWorker.insertURL(this, historyTable, selectedRow);
                    });

                    // create Y spacing
                    buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                    // create a closeAfterInsert checkbox in the button panel
                    closeAfterInsert = new JCheckBox("<html>" + closeAfterInsertCheckBoxText + "</html>");
                    closeAfterInsert.setFont(new Font(fontName, Font.PLAIN, 14));
                    closeAfterInsert.setAlignmentX(Component.CENTER_ALIGNMENT);
                    closeAfterInsert.setMinimumSize(new Dimension(125, 60));
                    closeAfterInsert.setMaximumSize(new Dimension(125, 60));
                    closeAfterInsert.setSelected(MainWorker.closeAfterInsert);
                    buttonPanel.add(closeAfterInsert);

                    closeAfterInsert.addActionListener(e -> {
                        if (MainWorker.debug) System.out.println("Close After Insert checkbox pressed.");
                        MainWorker.closeAfterInsert = closeAfterInsert.isSelected();
                    });
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
                pagePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                pagePanel.setAlignmentY(Component.TOP_ALIGNMENT);
                pagePanel.setMaximumSize(new Dimension(historyWindowWidth, 35));
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
                    labelPage = new JLabel(pageLabelText);
                    labelPage.setFont(new Font(fontName, Font.PLAIN, 16));
                    pagePanel.add(labelPage);

                    // create a page number label in the page panel
                    labelPageNumber = new JLabel("1");
                    labelPageNumber.setFont(new Font(fontName, Font.PLAIN, 16));
                    pagePanel.add(labelPageNumber);

                    // create a page total label in the page panel
                    labelPageTotal = new JLabel(ofText + " 1");
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

                    // create X spacing to center the page buttons on the table
                    // account for the width of the buttons because of right alignment
                    int sidePanelsWidth = 160; // L = 25, R = 135
                    int buttonsWidth = 125;
                    int x = ((historyWindowWidth - sidePanelsWidth) / 2) - buttonsWidth;
                    pagePanel.add(Box.createRigidArea(new Dimension(x, 0)));

                    // create a close button in the button panel
                    closeButton = new JButton(closeButtonText);
                    closeButton.setFont(new Font(fontName, Font.PLAIN, 14));
                    closeButton.setPreferredSize(new Dimension(125, 25));
                    closeButton.setBackground(new Color(darkMode ? 0x375a81 : 0xffffff));
                    closeButton.addAncestorListener(new RequestFocusListener());
                    pagePanel.add(closeButton);

                    closeButton.addActionListener(e -> this.dispose());
                }
            }

            setHistoryTable();
        }
    }

    private JMenu getCopySubMenu() {
        JMenu copySubMenu = new JMenu(tablePopupMenuCopyItem);
        copySubMenu.setFont(new Font(fontName, Font.PLAIN, 14));
        int[] indices = {-1, colTitle, colUrl, colStatus, colType, colDate};

        for (int i = 0; i < copySubMenuItems.length; i++) {
            JMenuItem menuItem = new JMenuItem(copySubMenuItems[i]);
            final int index = indices[i];
            final String option = copySubMenuItems[i];

            menuItem.addActionListener(e -> {
                System.out.println("POPUPMENU: Copy " + option + " selected");
                selectedRow = historyTable.getSelectedRow();
                MainWorker.copyRowToClipboard(HistoryWindow.this, historyTable, selectedRow, index);
            });

            copySubMenu.add(menuItem);
            menuItem.setFont(copySubMenu.getFont());
        }
        return copySubMenu;
    }

    private void removeHistoryRow(int selectedRow) {
        if (selectedRow == -1) {
            // show an error dialog if no row is selected
            JOptionPane.showMessageDialog(this,
                    noRowSelectedErrorDialogMessageText,
                    removeRowErrorDialogTitleText, JOptionPane.ERROR_MESSAGE);

        } else {
            int confirm = DoNotAskAgainConfirmDialog.showConfirmDialog(this,
                    removeRowConfirmDialogMessageText,
                    removeRowConfirmDialogTitleText, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    MainWorker.prefs, "doNotAskAgainRemoveButton"); //TODO - update for localeManager

            if (confirm == JOptionPane.YES_OPTION) {
                HistoryLogger historyLogger = new HistoryLogger();

                // calculate the actual index of the selected row in the full history list
                int pageNum = Integer.parseInt(labelPageNumber.getText());
                int maxRows = 25; // Maximum number of rows per page
                int actualIndex = selectedRow + (pageNum - 1) * maxRows;

                // remove the selected row from the full history list
                if (actualIndex < historyList.size()) {
                    historyList.remove(actualIndex);

                    // copy historyList to tempList
                    ArrayList<String[]> tempList = new ArrayList<>(historyList);
                    historyLogger.setHistoryFile(tempList);
                }

                // update the table view
                setTablePage(pageNum);

                scrollPane.getVerticalScrollBar().setEnabled(historyTable.getRowCount() > 13);
            }
        }
    }

    private void printHistoryList() {
        int totalLineLength = 125;
        int[] charAtColumnDivVertical = {53, 82, 100};
        String tableFormat = "| %-50s | %-26s | %-15s | %-21s |%n| %-50s | %-26s | %-15s | %-21s |%n";
        int historyListSize = historyList.size();

        StringBuilder sb = new StringBuilder();

        String fullLengthDivider = "+" + "-".repeat(totalLineLength - 2) + "+";
        String fullLengthDividerPlus = fullLengthDivider;
        for (int i : charAtColumnDivVertical) {
            fullLengthDividerPlus = Utils.replaceCharAt(fullLengthDividerPlus, i, "+");
        }
        String fullLengthDividerColumn = fullLengthDivider;
        for (int i : charAtColumnDivVertical) {
            fullLengthDividerColumn = Utils.replaceCharAt(fullLengthDividerColumn, i, "|");
        }

/*
+-------------------------------------------------------------------------------------------------------------------------------------+
| History List Size: 2                                                                                                                |
|                                                                                                                                     |
+--------------------------------------------------------------+----------------------------+-----------------+-----------------------+
*/
        sb.append(fullLengthDivider);
        sb.append(String.format("%n| %-18s %,-102d |%n", "History List Size:", historyListSize));
        sb.append("|").append(fullLengthDivider.replace("-", " "),
                1, fullLengthDivider.length() - 1).append("|").append("\n");
        sb.append("|").append(fullLengthDividerPlus, 1, fullLengthDividerPlus.length() - 1).append("|").append("\n");


/*
|--------------------------------------------------------------+----------------------------+-----------------+-----------------------|
| Title                                                        | Status                     | Type            | Date (Recent First)   |
| URL                                                          |                            |                 |                       |
|--------------------------------------------------------------|----------------------------|-----------------|-----------------------|
*/
        sb.append(String.format(
                tableFormat, columnNames[colTitle], columnNames[colStatus], columnNames[colType],
                columnNames[colDate] + " (Recent First)", columnNames[colUrl], "", "", "")
        );
        sb.append("|").append(fullLengthDividerColumn, 1, fullLengthDividerColumn.length() - 1).append("|").append("\n");


/*
|--------------------------------------------------------------|----------------------------|-----------------|-----------------------|
| Me at the zoo                                                | Completed - Success        | Video + Audio   | 2023-12-11 22:44:28   |
| https://www.youtube.com/watch?v=jNQXAC9IVRw                  |                            |                 |                       |
+--------------------------------------------------------------+----------------------------+-----------------+-----------------------+
*/
        for (String[] data : historyList) {
            String titleTrimmed = data[colTitle].length() > 50 ? data[colTitle].substring(0, 47) + "..." : data[colTitle];
            sb.append(String.format(
                    tableFormat, titleTrimmed, data[colStatus], data[colType], data[colDate],
                    data[colUrl], "", "", "")
            );

            // if not the last row, add a divider
            if (historyList.indexOf(data) != historyListSize - 1) {
                sb.append("|").append(fullLengthDividerColumn, 1, fullLengthDividerColumn.length() - 1).append("|").append("\n");
            }
        }
        sb.append("+").append(fullLengthDividerPlus, 1, fullLengthDividerPlus.length() - 1).append("+").append("\n");


        System.out.print(sb);
    }

    private void sortHistoryList(int col, boolean[] ascending) {
        HistoryLogger historyLogger = new HistoryLogger();

        updateColumnHeaders(ascending[0]);

        // sort the history list by the selected column
        int secondarySortType = colDate;
        if (col == colDate) secondarySortType = colType;
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

    private void setTablePage(int pageNum) {
        int maxRowsPerPage = 25;

        // get the total number of pages in the list
        int totalPages = (int) Math.ceil((double) historyList.size() / maxRowsPerPage);

        // set the page number labels
        labelPageNumber.setText(String.valueOf(pageNum));
        labelPageTotal.setText(ofText + " " + (totalPages == 0 ? (totalPages = 1) : totalPages));
        if (MainWorker.debug) System.out.println("Page number: " + pageNum + " Total Pages: " + totalPages);

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

        // repaint the table
        historyTable.repaint();

        // enable/disable the scroll bar
        scrollPane.getVerticalScrollBar().setEnabled(historyTable.getRowCount() > 13);
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    public void setHistoryTable() {
        if (MainWorker.debug) System.out.println("Setting History Table.");

        if (historyList.isEmpty()) {
            tableModel.setRowCount(0);
            if (MainWorker.debug) System.out.println("List is empty. No data to display.");
        } else {
            // sort the history list by date
            sortHistoryList(colDate, new boolean[]{false});
            if (MainWorker.debug) printHistoryList();

            for (String[] data : historyList) {
                if (data != null) {
                    tableModel.addRow(data);
                } else {
                    System.err.println("[ERROR] Failed to set History Table. Data is null.");
                }
            }
        }

        // set the table to the first page
        setTablePage(1);
    }
}