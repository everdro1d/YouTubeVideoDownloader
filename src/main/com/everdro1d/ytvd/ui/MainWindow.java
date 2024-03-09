package main.com.everdro1d.ytvd.ui;

import com.everdro1d.libs.core.Utils;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.DebugConsoleWindow;
import com.formdev.flatlaf.FlatClientProperties;
import main.com.everdro1d.ytvd.core.AdvancedSettings;
import main.com.everdro1d.ytvd.core.MainWorker;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static main.com.everdro1d.ytvd.core.AdvancedSettings.*;
import static main.com.everdro1d.ytvd.core.MainWorker.*;
import static main.com.everdro1d.ytvd.ui.WorkingPane.workingFrame;

public class MainWindow extends JFrame {
    public static HistoryWindow historyWindow;
    public static JFrame frame;
        protected JPanel mainPanel;
            protected JPanel northPanel;
                protected JPanel northPanelBorder1;
                    protected JPanel northPanelWestBorder;
                        protected JButton openWindowMenuButton;
                    protected JPanel northPanelCenter;
                        protected JPanel northPanelCenterRow1;
                            protected JPanel northPanelCenterRow1YPanel;
                                protected JLabel labelTitle;
                                protected CustomSeparator separatorTitle;
                    protected JPanel northPanelEastBorder;
                        protected JToggleButton lightDarkModeButton;
                        protected JPanel northPanelCenterRow2;
                            protected JLabel labelURL;
                            public static JTextField textField_URL;
                            protected static JPopupMenu textFieldPopupMenu;
                            protected boolean validURL;
                            protected JComboBox<String> comboBoxType;
            protected JPanel centerVerticalPanel;

                protected JPanel centerVerticalPanelRow1;
                    protected static JLabel validURLLabel;
                    public static boolean overrideValidURL;
                    public static JCheckBox checkBoxAdvancedSettings;
                    protected static JCheckBox checkBoxCompatibility;
                    protected static JCheckBox checkBoxLogHistory;

                protected JPanel centerVerticalPanelRow2;
                    protected JPanel advancedSettingsPanelRow1;
                        // Video
                        protected JLabel labelVideoExt;
                        protected JLabel labelVideoResolution;
                        protected JLabel labelVideoFPS;
                        protected JLabel labelVideoVBR;
                        protected JLabel labelVideoCodec;

                        public static JComboBox<String> comboBoxVideoExt;
                        public static JComboBox<String> comboBoxVideoResolution;
                        public static JComboBox<String> comboBoxVideoFPS;
                        public static JComboBox<String> comboBoxVideoVBR;
                        public static JComboBox<String> comboBoxVideoCodec;

                    protected JPanel advancedSettingsPanelRow2;
                        // Audio
                        protected JLabel labelAudioExt;
                        protected JLabel labelAudioChannels;
                        protected JLabel labelAudioABR;
                        protected JLabel labelAudioASR;
                        protected JLabel labelAudioCodec;

                        public static JComboBox<String> comboBoxAudioChannels;
                        public static JComboBox<String> comboBoxAudioExt;
                        public static JComboBox<String> comboBoxAudioABR;
                        public static JComboBox<String> comboBoxAudioASR;
                        public static JComboBox<String> comboBoxAudioCodec;

                    protected JPanel advancedSettingsPanelRow3;
                        protected static JCheckBox checkBoxRecode;
                        protected JLabel labelRecodeBox;
                        public static JComboBox<String> comboBoxRecodeExt;
                        protected static JCheckBox checkBoxWriteThumbnail;
                        protected static JComboBox<String> comboBoxWriteThumbnailExt;
                        protected static JCheckBox checkBoxEmbedThumbnail;
                        protected static JCheckBox checkBoxMetadata;

            protected JPanel southPanel;
                protected CustomSeparator separatorButtonPanel;
                protected JPanel southPanelRow1;
                    protected JButton fileChooserButton;
                    protected static JButton downloadButton;
            protected JPanel eastPanel;
            protected JPanel westPanel;
    private final String[] typeComboBoxOptions = {"Video + Audio", "Only Video", "Only Audio"};
    protected static final int windowWidth = 840;
    protected final int windowHeight = 250;
    protected final int windowWidthExpanded = 980;
    protected int windowHeightExpanded = 360;
    public static final String fontName = "Tahoma";
    public static final int fontSize = 18;


    public MainWindow() {
        initializeWindowProperties();

        initializeGUIComponents();

        frame.setVisible(true);

        SwingGUI.setHandCursorToClickableComponents(frame);
    }

    private void initializeWindowProperties() {
        frame = this;
        frame.setTitle(titleText);
        frame.setMinimumSize(new Dimension(windowWidth, windowHeight));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                windowPosition = SwingGUI.getFramePositionOnScreen(frame);
            }
        });
    }

    private void initializeGUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);
        // dependent on the layout of the mainPanel
        {
            // add a new JPanel in the north of the mainPanel
            northPanel = new JPanel();
            northPanel.setLayout(new BorderLayout());
            mainPanel.add(northPanel, BorderLayout.NORTH);
            {
                northPanelBorder1 = new JPanel();
                northPanelBorder1.setLayout(new BorderLayout());
                northPanel.add(northPanelBorder1, BorderLayout.NORTH);
                {
                    // add a new JPanel in the west of the northPanelBorder1
                    northPanelWestBorder = new JPanel();
                    northPanelWestBorder.setLayout(new BoxLayout(northPanelWestBorder, BoxLayout.Y_AXIS));
                    northPanelBorder1.add(northPanelWestBorder, BorderLayout.WEST);
                    {
                        //adds a new label in the west of the northPanelBorder1
                        //this is just to make the title look centered           TotalWidthOfPanel = 70
                        northPanelWestBorder.add(Box.createRigidArea(new Dimension(20, 0)));

                        //create Y spacing
                        northPanelWestBorder.add(Box.createRigidArea(new Dimension(0, 10)));

                        // add a button in the west of the northPanelWestBorder
                        openWindowMenuButton = new JButton();
                        openWindowMenuButton.setBorderPainted(false);
                        openWindowMenuButton.setContentAreaFilled(false);
                        openWindowMenuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                        openWindowMenuButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                        openWindowMenuButton.setIcon(
                                SwingGUI.getApplicationIcon(darkMode ? "images/historyIconDark.png" : "images/historyIcon.png", this.getClass())
                        );
                        northPanelWestBorder.add(openWindowMenuButton);

                        openWindowMenuButton.addActionListener((e) -> {
                            JPopupMenu popupMenu = new JPopupMenu();
                            {
                                String[] menuItems = {"Open History Window","Open Debug Console","Toggle debug mode On/Off","Open dro1dDev Website"};
                                ActionListener[] actions = {
                                        (e1) -> showHistoryWindow(),
                                        (e1) -> showDebugConsole(),
                                        (e1) -> debug = !debug,
                                        (e1) -> Utils.openLink(dro1dDevWebsite)
                                };

                                for (int i = 0; i < menuItems.length; i++) {
                                    JMenuItem menuItem = new JMenuItem(menuItems[i]);
                                    menuItem.addActionListener(actions[i]);
                                    popupMenu.add(menuItem);
                                    menuItem.setEnabled(true);
                                    menuItem.setFont(new Font(fontName, Font.PLAIN, 14));
                                }

                                popupMenu.addPopupMenuListener(new PopupMenuListener() {
                                    @Override
                                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                                        popupMenu.getComponent(0).setEnabled(logHistory);
                                        popupMenu.getComponent(1).setEnabled(debug);
                                    }

                                    @Override
                                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
                                    @Override
                                    public void popupMenuCanceled(PopupMenuEvent e) {}
                                });
                            }
                            popupMenu.show(openWindowMenuButton, openWindowMenuButton.getWidth(), 0);
                        });
                    }
                    // add a new JPanel in the center of the northPanelBorder1
                    northPanelCenter = new JPanel();
                    northPanelCenter.setLayout(new BoxLayout(northPanelCenter, BoxLayout.Y_AXIS));
                    northPanelBorder1.add(northPanelCenter, BorderLayout.CENTER);

                    // dependent on the layout of the northPanelRow1
                    {
                        northPanelCenter.add(Box.createRigidArea(new Dimension(0, 5)));

                        // add a new JPanel in the first row of the northPanelCenter for the title
                        northPanelCenterRow1 = new JPanel();
                        northPanelCenterRow1.setLayout(new BoxLayout(northPanelCenterRow1, BoxLayout.X_AXIS));
                        northPanelCenter.add(northPanelCenterRow1);
                        {
                            int spacerWidth = 15;
                            northPanelCenterRow1.add(Box.createRigidArea(new Dimension(spacerWidth, 0)));


                            northPanelCenterRow1YPanel = new JPanel();
                            northPanelCenterRow1YPanel.setLayout(new BoxLayout(northPanelCenterRow1YPanel, BoxLayout.Y_AXIS));
                            northPanelCenterRow1.add(northPanelCenterRow1YPanel);
                            {
                                // add a new title label in the north of the northPanelRow1
                                labelTitle = new JLabel(titleText);
                                labelTitle.setFont(new Font(fontName, Font.BOLD, 32));
                                labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                                northPanelCenterRow1YPanel.add(labelTitle);

                                northPanelCenterRow1YPanel.add(Box.createRigidArea(new Dimension(0, 5)));

                                // add a new separator in the north of the northPanelRow1
                                separatorTitle = new CustomSeparator(true, 3, 3);
                                northPanelCenterRow1YPanel.add(separatorTitle);
                            }


                            northPanelCenterRow1.add(Box.createRigidArea(new Dimension(spacerWidth, 0)));
                        }

                        northPanelCenter.add(Box.createRigidArea(new Dimension(0, 20)));

                        // add a new JPanel in the second row of the northPanelCenter
                        northPanelCenterRow2 = new JPanel();
                        northPanelCenterRow2.setLayout(new BoxLayout(northPanelCenterRow2, BoxLayout.LINE_AXIS));
                        northPanel.add(northPanelCenterRow2, BorderLayout.CENTER);

                        // dependent on the layout of the northPanelRow1
                        {

                            northPanelCenterRow2.add(Box.createRigidArea(new Dimension(20, 0)));

                            //add a new label on the left side of the north panel underneath the title
                            labelURL = new JLabel("URL: ");
                            labelURL.setFont(new Font(fontName, Font.PLAIN, fontSize));
                            labelURL.setHorizontalAlignment(SwingConstants.LEFT);
                            labelURL.setAlignmentX(Component.LEFT_ALIGNMENT);
                            northPanelCenterRow2.add(labelURL);

                            //add a new text field in the center of the north panel underneath the title
                            textField_URL = new JTextField(rawURL);
                            textField_URL.setFont(new Font(fontName, Font.PLAIN, fontSize));
                            textField_URL.setHorizontalAlignment(SwingConstants.LEFT);
                            textField_URL.setAlignmentX(Component.CENTER_ALIGNMENT);
                            textField_URL.setColumns(28);
                            northPanelCenterRow2.add(textField_URL);

                            textField_URL.addKeyListener(new KeyAdapter() {
                                public void keyReleased(KeyEvent e) {
                                    rawURL = textField_URL.getText().trim();
                                    if (rawURL.isEmpty() && !overrideValidURL) {
                                        validURL = false;
                                    } else {
                                        validURL = overrideValidURL || validURL(rawURL);
                                        if (validURL && !overrideValidURL) {
                                            //find the char index of the videoID add 11 to get the end of the video ID
                                            int videoIDStart = rawURL.indexOf(videoID);
                                            int videoIDEnd = videoIDStart + 11;
                                            //cut off the URL to the end of the video ID
                                            rawURL = rawURL.substring(0, videoIDEnd);

                                            textField_URL.setText(rawURL);

                                        }
                                        if (debug) System.out.println("RawURL: " + rawURL);
                                    }
                                    if (!overrideValidURL) validURLLabel.setText(validURL ? "URL is valid" : "URL is invalid");
                                    else validURLLabel.setText("FORCE - URL is valid");
                                    coloringModeChange();

                                    if ( validURL && !(compatibilityMode || overrideValidURL) ) {
                                        checkBoxAdvancedSettings.setEnabled(true);
                                    } else {
                                        checkBoxAdvancedSettings.setSelected(false);
                                        checkBoxAdvancedSettings.setEnabled(false);
                                        advancedSettingsPanelRow1.setVisible(false);
                                        advancedSettingsPanelRow2.setVisible(false);
                                        advancedSettingsPanelRow3.setVisible(false);
                                        advancedSettingsEvent(true);
                                    }
                                }
                            });

                            textField_URL.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    textField_URL.requestFocusInWindow();

                                    if (SwingUtilities.isRightMouseButton(e)) {
                                        if (MainWorker.debug) System.out.println("Right click on URL text field");
                                        textFieldPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                                        if (MainWorker.debug) System.out.println("Left click on URL text field");
                                        textFieldPopupMenu.setVisible(false);
                                    }
                                }
                            });


                            northPanelCenterRow2.add(Box.createRigidArea(new Dimension(10, 0)));

                            //add a new comboBox on the right side of the north panel underneath the title
                            comboBoxType = new JComboBox<>();
                            comboBoxType.setFont(new Font(fontName, Font.PLAIN, fontSize));
                            comboBoxType.setModel(new DefaultComboBoxModel<>(typeComboBoxOptions));
                            comboBoxType.setSelectedIndex(videoAudio);
                            comboBoxType.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            northPanelCenterRow2.add(comboBoxType);

                            comboBoxType.addActionListener((e) -> {
                                videoAudio = comboBoxType.getSelectedIndex();
                                checkType();
                                // 0 = video and audio,  1 = only video,  2 = only audio
                            });

                            northPanelCenterRow2.add(Box.createRigidArea(new Dimension(20, 0)));
                        }
                    }

                    // add a new JPanel in the east of the northPanelBorder1
                    northPanelEastBorder = new JPanel();
                    northPanelEastBorder.setLayout(new BoxLayout(northPanelEastBorder, BoxLayout.Y_AXIS));
                    northPanelBorder1.add(northPanelEastBorder, BorderLayout.EAST);
                    {
                        //create Y spacing
                        northPanelEastBorder.add(Box.createRigidArea(new Dimension(0, 10)));

                        lightDarkModeButton = new JToggleButton();
                        lightDarkModeButton.setBorderPainted(false);
                        lightDarkModeButton.setContentAreaFilled(false);
                        lightDarkModeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        lightDarkModeButton.setAlignmentY(Component.CENTER_ALIGNMENT);

                        Icon sunIcon = SwingGUI.getApplicationIcon("images/sunIcon.png", this.getClass());
                        Icon moonIcon = SwingGUI.getApplicationIcon("images/moonIcon.png", this.getClass());

                        lightDarkModeButton.setSelectedIcon(darkMode ? sunIcon : moonIcon);
                        lightDarkModeButton.setIcon(darkMode ? moonIcon : sunIcon);

                        northPanelEastBorder.add(lightDarkModeButton, BorderLayout.EAST);

                        lightDarkModeButton.addActionListener((e) -> {
                            darkMode = !darkMode;
                            SwingGUI.lightOrDarkMode(darkMode, new JFrame[]{frame, workingFrame, DebugConsoleWindow.debugFrame, HistoryWindow.historyFrame});
                            coloringModeChange();
                            SwingUtilities.updateComponentTreeUI(frame);
                        });

                        northPanelEastBorder.add(Box.createRigidArea(new Dimension(20, 0)));
                    }
                }
            }

            //------------------------------ Main Panel Divider ------------------------------\\


            //create a new JPanel boxlayout in the center of the mainPanel
            centerVerticalPanel = new JPanel();
            centerVerticalPanel.setLayout(new BoxLayout(centerVerticalPanel, BoxLayout.Y_AXIS));
            mainPanel.add(centerVerticalPanel, BorderLayout.CENTER);

            //dependent on the layout of the centerVerticalPanel
            {
                centerVerticalPanel.add(Box.createRigidArea(new Dimension(0, 5)));

                //create a new JPanel boxlayout in the first row of the centerVerticalPanel
                centerVerticalPanelRow1 = new JPanel();
                centerVerticalPanelRow1.setLayout(new BoxLayout(centerVerticalPanelRow1, BoxLayout.LINE_AXIS));
                centerVerticalPanel.add(centerVerticalPanelRow1);

                //dependent on the layout of the centerVerticalPanelRow1
                {
                    //add a JLabel on the left side of the centerVerticalPanelRow1
                    centerVerticalPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));

                    validURLLabel = new JLabel(validURL ? "URL is valid  " : "URL is invalid");
                    validURLLabel.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    validURLLabel.setHorizontalTextPosition(SwingConstants.LEFT);
                    validURLLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    validURLLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    centerVerticalPanelRow1.add(validURLLabel);

                    validURLLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            JPopupMenu popupMenu = new JPopupMenu();
                            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem("Force valid URL (Allow anything as URL)", overrideValidURL);
                            menuItem.addActionListener((e1) -> overrideValidURL(!overrideValidURL));
                            menuItem.setFont(new Font(fontName, Font.PLAIN, 14));
                            popupMenu.add(menuItem);

                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    });

                    centerVerticalPanelRow1.add(Box.createRigidArea(new Dimension(10, 0)));

                    //add a new checkbox in the center-right of the centerVerticalPanelRow1
                    checkBoxAdvancedSettings = new JCheckBox("Advanced Settings");
                    checkBoxAdvancedSettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    checkBoxAdvancedSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
                    checkBoxAdvancedSettings.setEnabled(false);
                    centerVerticalPanelRow1.add(checkBoxAdvancedSettings);

                    checkBoxAdvancedSettings.addActionListener((e) -> {
                        checkBoxCompatibility.setSelected(false);
                        compatibilityMode = false;
                        checkBoxCompatibility.setEnabled(!checkBoxAdvancedSettings.isSelected());
                        advancedSettingsEvent(Utils.containsAny(new String[]{
                                "https://www.youtube.com/watch?v=",
                                "https://youtu.be/",
                                "https://www.youtube.com/shorts/"
                        }, rawURL));
                    });

                    centerVerticalPanelRow1.add(Box.createRigidArea(new Dimension(10, 0)));

                    //add a compatability checkbox to the right of the advanced settings checkbox
                    checkBoxCompatibility = new JCheckBox("Compatibility Mode");
                    checkBoxCompatibility.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    checkBoxCompatibility.setAlignmentX(Component.CENTER_ALIGNMENT);
                    checkBoxCompatibility.setEnabled(true);
                    centerVerticalPanelRow1.add(checkBoxCompatibility);

                    if (prefs.getBoolean("compatibilityMode", false)) {
                        checkBoxCompatibility.setSelected(compatibilityMode);
                        checkBoxAdvancedSettings.setSelected(!compatibilityMode);
                    }
                    checkBoxCompatibility.addActionListener((e) -> {

                        compatibilityMode = checkBoxCompatibility.isSelected();
                        if (compatibilityMode) {
                            checkBoxAdvancedSettings.setSelected(false);
                            checkBoxAdvancedSettings.setEnabled(false);
                            advancedSettingsPanelRow1.setVisible(false);
                            advancedSettingsPanelRow2.setVisible(false);
                            advancedSettingsPanelRow3.setVisible(false);
                            advancedSettingsEvent(true);
                        } else {
                            checkBoxAdvancedSettings.setEnabled(validURL && !checkBoxCompatibility.isSelected());
                        }
                    });

                    centerVerticalPanelRow1.add(Box.createRigidArea(new Dimension(10, 0)));

                    checkBoxLogHistory = new JCheckBox("Log History");
                    checkBoxLogHistory.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    checkBoxLogHistory.setAlignmentX(Component.CENTER_ALIGNMENT);
                    checkBoxLogHistory.setEnabled(true);
                    centerVerticalPanelRow1.add(checkBoxLogHistory);

                    if (prefs.getBoolean("logHistory", true)) {
                        checkBoxLogHistory.setSelected(logHistory);
                        openWindowMenuButton.setEnabled(logHistory);
                    }
                    checkBoxLogHistory.addActionListener((e) -> logHistory = checkBoxLogHistory.isSelected());
                }

                centerVerticalPanel.add(Box.createRigidArea(new Dimension(0, 20)));

                //create a new JPanel boxlayout in the second row of the centerVerticalPanel
                centerVerticalPanelRow2 = new JPanel();
                centerVerticalPanelRow2.setLayout(new BoxLayout(centerVerticalPanelRow2, BoxLayout.Y_AXIS));
                centerVerticalPanelRow2.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
                centerVerticalPanel.add(centerVerticalPanelRow2);

                //dependent on the layout of the centerVerticalPanelRow2
                {
                    centerVerticalPanelRow2.add(Box.createRigidArea(new Dimension(0, 8)));

                    advancedSettingsPanelRow1 = new JPanel();
                    advancedSettingsPanelRow1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                    advancedSettingsPanelRow1.setVisible(false);
                    centerVerticalPanelRow2.add(advancedSettingsPanelRow1);

                    // dependent on the layout of the advancedSettingsPanelRow1
                    {
                        advancedSettingsPanelRow1.add(Box.createRigidArea(new Dimension(10, 0)));

                        // Video ----------------------------------------------------------
                        labelVideoExt = new JLabel("Video Format: ");
                        comboBoxVideoExt = setupAdvancedSettingsComboBoxes(labelVideoExt, advancedSettingsPanelRow1, arrayVideoExtensions, videoExt);

                        comboBoxVideoExt.addActionListener((e) -> {
                            videoExt = comboBoxVideoExt.getSelectedIndex();

                            if (videoAudio == 0) { // sets the audio extension to the same as the video extension
                                comboBoxAudioExt.setSelectedIndex(videoExt);
                            }

                            doCascadeFilter(comboBoxVideoExt);
                            checkEmbedThumbnailSupported();
                        });


                        advancedSettingsPanelRow1.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelVideoResolution = new JLabel("Resolution: ");
                        comboBoxVideoResolution = setupAdvancedSettingsComboBoxes(labelVideoResolution, advancedSettingsPanelRow1, arrayVideoResolution, videoResolution);
                        comboBoxVideoResolution.addActionListener((e) -> {
                            videoResolution = comboBoxVideoResolution.getSelectedIndex();
                            doCascadeFilter(comboBoxVideoResolution);
                        });


                        advancedSettingsPanelRow1.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelVideoFPS = new JLabel("FPS: ");
                        comboBoxVideoFPS = setupAdvancedSettingsComboBoxes(labelVideoFPS, advancedSettingsPanelRow1, arrayVideoFPS, videoFPS);
                        comboBoxVideoFPS.addActionListener((e) -> {
                            videoFPS = comboBoxVideoFPS.getSelectedIndex();
                            doCascadeFilter(comboBoxVideoFPS);
                        });


                        advancedSettingsPanelRow1.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelVideoVBR = new JLabel("VBR: ");
                        comboBoxVideoVBR = setupAdvancedSettingsComboBoxes(labelVideoVBR, advancedSettingsPanelRow1, arrayVideoVBR, videoVBR);
                        comboBoxVideoVBR.addActionListener((e) -> {
                            videoVBR = comboBoxVideoVBR.getSelectedIndex();
                            doCascadeFilter(comboBoxVideoVBR);
                        });


                        advancedSettingsPanelRow1.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelVideoCodec = new JLabel("Codec: ");
                        comboBoxVideoCodec = setupAdvancedSettingsComboBoxes(labelVideoCodec, advancedSettingsPanelRow1, arrayVideoCodec, videoCodec);
                        comboBoxVideoCodec.addActionListener((e) -> {
                            videoCodec = comboBoxVideoCodec.getSelectedIndex();
                            doCascadeFilter(comboBoxVideoCodec);
                        });


                    }


                    centerVerticalPanelRow2.add(Box.createRigidArea(new Dimension(0, 20)));


                    //add a new JPanel in the second row of the centerVerticalPanelRow2
                    advancedSettingsPanelRow2 = new JPanel();
                    advancedSettingsPanelRow2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                    advancedSettingsPanelRow2.setVisible(false);
                    centerVerticalPanelRow2.add(advancedSettingsPanelRow2);

                    //dependent on the layout of the advancedSettingsPanelRow2
                    {
                        advancedSettingsPanelRow2.add(Box.createRigidArea(new Dimension(10, 0)));

                        // Audio ----------------------------------------------------------
                        labelAudioExt = new JLabel("Audio Format: ");
                        comboBoxAudioExt = setupAdvancedSettingsComboBoxes(labelAudioExt, advancedSettingsPanelRow2, arrayAudioExtensions, audioExt);
                        comboBoxAudioExt.addActionListener((e) -> {
                            audioExt = comboBoxAudioExt.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioExt);
                            checkEmbedThumbnailSupported();
                        });


                        advancedSettingsPanelRow2.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelAudioChannels = new JLabel("Channels: ");
                        comboBoxAudioChannels = setupAdvancedSettingsComboBoxes(labelAudioChannels, advancedSettingsPanelRow2, arrayAudioChannels, audioChannels);
                        comboBoxAudioChannels.addActionListener((e) -> {
                            audioChannels = comboBoxAudioChannels.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioChannels);
                        });


                        advancedSettingsPanelRow2.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelAudioABR = new JLabel("ABR: ");
                        comboBoxAudioABR = setupAdvancedSettingsComboBoxes(labelAudioABR, advancedSettingsPanelRow2, arrayAudioABR, audioABR);
                        comboBoxAudioABR.addActionListener((e) -> {
                            audioABR = comboBoxAudioABR.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioABR);
                        });


                        advancedSettingsPanelRow2.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelAudioASR = new JLabel("ASR: ");
                        comboBoxAudioASR = setupAdvancedSettingsComboBoxes(labelAudioASR, advancedSettingsPanelRow2, arrayAudioASR, audioASR);
                        comboBoxAudioASR.addActionListener((e) -> {
                            audioASR = comboBoxAudioASR.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioASR);
                        });


                        labelAudioCodec = new JLabel("Codec: ");
                        comboBoxAudioCodec = setupAdvancedSettingsComboBoxes(labelAudioCodec, advancedSettingsPanelRow2, arrayAudioCodec, audioCodec);
                        comboBoxAudioCodec.addActionListener((e) -> {
                            audioCodec = comboBoxAudioCodec.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioCodec);
                        });


                    }

                    centerVerticalPanelRow2.add(Box.createRigidArea(new Dimension(0, 20)));

                    //add another row to the centerVerticalPanelRow2
                    advancedSettingsPanelRow3 = new JPanel();
                    advancedSettingsPanelRow3.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                    advancedSettingsPanelRow3.setVisible(false);
                    centerVerticalPanelRow2.add(advancedSettingsPanelRow3);

                    //dependent on the layout of the advancedSettingsPanelRow3
                    {
                        advancedSettingsPanelRow3.add(Box.createRigidArea(new Dimension(10, 0)));

                        //re-code checkbox and combobox
                        //add a new checkbox
                        checkBoxRecode = new JCheckBox("Recode Video");
                        checkBoxRecode.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        checkBoxRecode.setAlignmentX(Component.LEFT_ALIGNMENT);
                        advancedSettingsPanelRow3.add(checkBoxRecode);

                        checkBoxRecode.addActionListener((e) -> {
                            recode = checkBoxRecode.isSelected();
                            labelRecodeBox.setEnabled(recode);
                            comboBoxRecodeExt.setEnabled(recode);

                            checkEmbedThumbnailSupported();

                            if (videoAudio != 2) {
                                checkBoxMetadata.setSelected(false);
                                checkBoxMetadata.setEnabled(!recode);
                            } else {
                                checkBoxMetadata.setEnabled(true);
                            }
                        });


                        advancedSettingsPanelRow3.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelRecodeBox = new JLabel("to: ");
                        labelRecodeBox.setEnabled(false);


                        advancedSettingsPanelRow3.add(Box.createRigidArea(new Dimension(4, 0)));


                        //add a new combobox
                        comboBoxRecodeExt = setupAdvancedSettingsComboBoxes(labelRecodeBox, advancedSettingsPanelRow3, arrayRecodeExt, recodeExt);
                        comboBoxRecodeExt.setEnabled(false);
                        comboBoxRecodeExt.addActionListener((e) -> {
                            recodeExt = comboBoxRecodeExt.getSelectedIndex();
                            checkEmbedThumbnailSupported();
                        });


                        advancedSettingsPanelRow3.add(Box.createRigidArea(new Dimension(25, 0)));


                        //add a new checkbox for thumbnail writing
                        checkBoxWriteThumbnail = new JCheckBox("Write Thumbnail");
                        checkBoxWriteThumbnail.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        checkBoxWriteThumbnail.setAlignmentX(Component.LEFT_ALIGNMENT);
                        advancedSettingsPanelRow3.add(checkBoxWriteThumbnail);

                        checkBoxWriteThumbnail.addActionListener((e) -> {
                            writeThumbnail = checkBoxWriteThumbnail.isSelected();
                            comboBoxWriteThumbnailExt.setEnabled(writeThumbnail);
                            checkEmbedThumbnailSupported();

                        });


                        advancedSettingsPanelRow3.add(Box.createRigidArea(new Dimension(4, 0)));


                        //add a new combobox to the right of the write thumbnail checkbox
                        comboBoxWriteThumbnailExt = setupAdvancedSettingsComboBoxes(new JLabel("as: "), advancedSettingsPanelRow3, arrayWriteThumbnailExt, writeThumbnailExt);
                        comboBoxWriteThumbnailExt.setEnabled(false);
                        comboBoxWriteThumbnailExt.addActionListener(
                                (e) -> writeThumbnailExt = comboBoxWriteThumbnailExt.getSelectedIndex()
                        );


                        advancedSettingsPanelRow3.add(Box.createRigidArea(new Dimension(10, 0)));


                        //add a new checkbox for thumbnail embedding (needs writeThumbnail to be enabled)
                        checkBoxEmbedThumbnail = new JCheckBox("Embed Thumbnail");
                        checkBoxEmbedThumbnail.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        checkBoxEmbedThumbnail.setAlignmentX(Component.LEFT_ALIGNMENT);
                        checkBoxEmbedThumbnail.setEnabled(false);
                        advancedSettingsPanelRow3.add(checkBoxEmbedThumbnail);

                        checkBoxEmbedThumbnail.addActionListener((e) -> embedThumbnail = checkBoxEmbedThumbnail.isSelected());


                        advancedSettingsPanelRow3.add(Box.createRigidArea(new Dimension(10, 0)));


                        checkBoxMetadata = new JCheckBox("Embed Metadata");
                        checkBoxMetadata.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        checkBoxMetadata.setAlignmentX(Component.LEFT_ALIGNMENT);
                        advancedSettingsPanelRow3.add(checkBoxMetadata);

                        checkBoxMetadata.addActionListener((e) -> {
                            addMetadata = checkBoxMetadata.isSelected();

                            if (videoAudio != 2) {
                                checkBoxRecode.setSelected(false);
                                checkBoxRecode.setEnabled(!addMetadata);
                            } else {
                                checkBoxRecode.setEnabled(true);
                            }
                        });
                    }

                    centerVerticalPanelRow2.add(Box.createRigidArea(new Dimension(0, 10)));
                }
                centerVerticalPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            }

            //------------------------------ Main Panel Divider ------------------------------\\


            southPanel = new JPanel();
            southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            {
                southPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                separatorButtonPanel = new CustomSeparator(true, 6, 4);
                southPanel.add(separatorButtonPanel);

                southPanel.add(Box.createRigidArea(new Dimension(0, 5)));

                // add a new JPanel in the south of the mainPanel
                southPanelRow1 = new JPanel();
                southPanelRow1.setLayout(new FlowLayout());
                southPanel.add(southPanelRow1, BorderLayout.SOUTH);
                {
                    //add a new FileChooser button in the center of the southPanel
                    fileChooserButton = new JButton("Choose Folder");
                    fileChooserButton.setFont(new Font(fontName, Font.PLAIN, fontSize + 2));
                    fileChooserButton.setPreferredSize(new Dimension(185, 40));
                    fileChooserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    fileChooserButton.setIcon(SwingGUI.getApplicationIcon("images/folderIcon.png", this.getClass()));
                    southPanelRow1.add(fileChooserButton);

                    fileChooserButton.addActionListener((e) -> MainWorker.downloadDirectoryPath = MainWorker.openFileChooser());


                    southPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));


                    // add a new button in the center of the southPanel
                    downloadButton = new JButton("Download");
                    downloadButton.setFont(new Font(fontName, Font.PLAIN, fontSize + 2));
                    downloadButton.setPreferredSize(new Dimension(160, 40));
                    downloadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    downloadButton.setIcon(SwingGUI.getApplicationIcon("images/downloadIcon.png", this.getClass()));
                    southPanelRow1.add(downloadButton);

                    downloadButton.addActionListener((e) -> MainWorker.downloadButtonClicked());


                    southPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));

                }
            }

            // add a new JPanel in the east of the mainPanel
            eastPanel = new JPanel();
            eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
            mainPanel.add(eastPanel, BorderLayout.EAST);
            {
                eastPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            }

            // add a new JPanel in the west of the mainPanel
            westPanel = new JPanel();
            westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
            mainPanel.add(westPanel, BorderLayout.WEST);
            {
                westPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            }

            textFieldPopupMenu = new JPopupMenu();
            {
                String[] menuItems = {"Cut", "Copy", "Paste", "Delete", "Select All"};
                ActionListener[] actions = {
                        (e) -> {
                            textField_URL.cut();
                            SwingGUI.requestFocusAndSimulateKeyEvent(textField_URL);
                        },
                        (e) -> {
                            textField_URL.copy();
                            SwingGUI.requestFocusAndSimulateKeyEvent(textField_URL);
                        },
                        (e) -> {
                            textField_URL.paste();
                            SwingGUI.requestFocusAndSimulateKeyEvent(textField_URL);
                        },
                        (e) -> {
                            textField_URL.setText("");
                            SwingGUI.requestFocusAndSimulateKeyEvent(textField_URL);
                        },
                        (e) -> {
                            textField_URL.selectAll();
                            textField_URL.requestFocus();
                        }
                };

                for (int i = 0; i < menuItems.length; i++) {
                    JMenuItem menuItem = new JMenuItem(menuItems[i]);
                    menuItem.addActionListener(actions[i]);
                    textFieldPopupMenu.add(menuItem);
                    menuItem.setEnabled(false);
                    menuItem.setFont(new Font(fontName, Font.PLAIN, 14));
                }

                //check if the text field is empty
                textFieldPopupMenu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        //enable/disable the select all menu item based on whether there is text in the text field
                        textFieldPopupMenu.getComponent(4).setEnabled(!textField_URL.getText().isEmpty());

                        //enable/disable the paste menu item based on whether the clipboard contains text
                        for (DataFlavor flavor : Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors()) {
                            if (flavor.equals(DataFlavor.stringFlavor)) {
                                textFieldPopupMenu.getComponent(2).setEnabled(true);
                                break;
                            } else {
                                textFieldPopupMenu.getComponent(2).setEnabled(false);
                            }
                        }

                        //enable/disable the cut, copy, delete menu items based on whether there is text selected
                        boolean isTextSelected = textField_URL.getSelectedText() != null;
                        textFieldPopupMenu.getComponent(0).setEnabled(isTextSelected);
                        textFieldPopupMenu.getComponent(1).setEnabled(isTextSelected);
                        textFieldPopupMenu.getComponent(3).setEnabled(isTextSelected);
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
            }
        }

    }

    public void overrideValidURL(boolean override) {
        overrideValidURL = override;
        validURL = override;
        if (debug) System.out.println("Override URL: " + overrideValidURL);
        //send a key released event to the text field to update the URL
        SwingGUI.simulateKeyEvent(textField_URL);
    }

    public void advancedSettingsEvent(boolean youtube) {
        checkType();

        if (checkBoxAdvancedSettings.isSelected() && youtube && !compatibilityMode) {
            // get video options
            AdvancedSettings.readVideoOptionsFromYT();
        } else {
            getVideoOptions = false;
            AdvancedSettings.advancedSettingsEnabled = false;
        }

        while (advancedSettingsEnabled) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (getVideoOptions) {
                break;
            }
        }
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.getDefaultCursor().getType()));

        if (youtube) {
            advancedSettingsPanelRow1.setVisible(checkBoxAdvancedSettings.isSelected());
            advancedSettingsPanelRow2.setVisible(checkBoxAdvancedSettings.isSelected());
        }
        advancedSettingsPanelRow3.setVisible(checkBoxAdvancedSettings.isSelected());
        frame.pack();

        windowHeightExpanded = windowHeight + centerVerticalPanelRow2.getHeight() + 20;

        if (checkBoxAdvancedSettings.isSelected()) { //GUI if
            frame.setMinimumSize(new Dimension(windowWidthExpanded, windowHeightExpanded));
            frame.setSize(new Dimension(windowWidthExpanded, windowHeightExpanded));
            SwingGUI.setLocationOnResize(frame, false);

            advancedSettingsPanelRow2.setSize(advancedSettingsPanelRow1.getSize());
            advancedSettingsPanelRow3.setSize(advancedSettingsPanelRow1.getSize());
        } else {
            frame.setMinimumSize(new Dimension(windowWidth, windowHeight));
            frame.setSize(new Dimension(windowWidth, windowHeight));
            frame.setMaximumSize(new Dimension(windowWidth, windowHeight));
        }

        if (advancedSettingsEnabled) {
            setAdvancedSettings();
        }
    }

    public void coloringModeChange() {
        // Colors
        Color backgroundColor = new Color(darkMode ? 0x2B2B2B : 0xE7E7E7);

        Color separatorTitleColor = new Color(darkMode ? 0x46494b : 0xc2c2c2);
        Color separatorButtonPanelColor = new Color(darkMode ? 0x595959 : 0xc2c2c2);

        Color advSettingsPanelColor = new Color(darkMode ? 0x303234 : 0xe0e0e0);

        Color textColor = new Color(darkMode ? 0xbbbbbb : 0x000000);

        // darkMode if() dependent colors
        if (darkMode) {
            // Valid URL Label colors
            validURLLabel.setForeground(validURL ? new Color(0x0dc47d) : new Color(0xCE3737));
        } else {
            // Valid URL Label colors
            validURLLabel.setForeground(validURL ? new Color(0x007C4D) : new Color(0xad0c0c));
        }


        // Main Panel colors
        frame.getContentPane().setBackground(backgroundColor);
        frame.setBackground(backgroundColor);

        // Separator colors
        separatorTitle.setBackground(separatorTitleColor);
        separatorButtonPanel.setBackground(separatorButtonPanelColor);

        openWindowMenuButton.setIcon(
                SwingGUI.getApplicationIcon(
                        darkMode ? "images/historyIconDark.png"
                                : "images/historyIcon.png",
                        this.getClass())
        );

        // Advanced Settings Panel colors
        centerVerticalPanelRow2.setBackground(advSettingsPanelColor);
        advancedSettingsPanelRow1.setBackground(advSettingsPanelColor);
        advancedSettingsPanelRow2.setBackground(advSettingsPanelColor);
        advancedSettingsPanelRow3.setBackground(advSettingsPanelColor);


        // Working Pane colors
        if (WorkingPane.workingFrame != null) {
            WorkingPane.workingFrame.getContentPane().setBackground(backgroundColor);
            WorkingPane.panel.setBackground(backgroundColor);
            WorkingPane.label1.setForeground(textColor);
            WorkingPane.labelMessage.setForeground(textColor);
            WorkingPane.progressBar.setForeground(textColor);
            WorkingPane.progressBar.setBackground(separatorButtonPanelColor);

        }


        // History window colors
        if (HistoryWindow.historyFrame != null) {
            HistoryWindow.separatorHistoryTitle.setBackground(separatorTitleColor);
            HistoryWindow.closeButton.setBackground(new Color(darkMode ? 0x375a81 : 0xffffff));
        }


        // Debug console colors
        DebugConsoleWindow.expandWindowButtonColorChange(textColor);
    }

    private JComboBox<String> setupAdvancedSettingsComboBoxes(
            JLabel label, JPanel advSettingPanel,
            String[] array, int selectedIndex) {
        label.setFont(new Font(fontName, Font.PLAIN, fontSize));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        advSettingPanel.add(label);

        JComboBox<String> comboBox = new JComboBox<>(array);
        SwingGUI.setupComboBox(comboBox, selectedIndex, fontName, fontSize);
        advSettingPanel.add(comboBox);
        return comboBox;
    }


    private void checkType() {
        JComponent[] videoComponents = {
                labelVideoExt, labelVideoResolution, labelVideoFPS, labelVideoVBR, labelVideoCodec,
                comboBoxVideoExt, comboBoxVideoResolution, comboBoxVideoFPS, comboBoxVideoVBR, comboBoxVideoCodec
        };
        JComponent[] audioComponents = {
                labelAudioExt, labelAudioChannels, labelAudioABR, labelAudioASR, labelAudioCodec,
                comboBoxAudioExt, comboBoxAudioChannels, comboBoxAudioABR, comboBoxAudioASR, comboBoxAudioCodec
        };
        //Type ------------------------------------------
        switch (comboBoxType.getSelectedIndex()) {
            case 0: // video + audio (default)
                setComponentsEnabled(videoComponents, true, audioComponents, true);
                break;
            case 1: // video only
                setComponentsEnabled(videoComponents, true, audioComponents, false);
                break;
            case 2: // audio only
                setComponentsEnabled(videoComponents, false, audioComponents, true);
                break;
        }


        //Recode ------------------------------------------
        setRecodeExtArray();

        SwingGUI.updateComboBox(arrayRecodeExt, comboBoxRecodeExt);


        // clear the checkboxes
        checkBoxRecode.setSelected(false);
        checkBoxWriteThumbnail.setSelected(false);
        checkBoxEmbedThumbnail.setSelected(false);
        checkBoxMetadata.setSelected(false);

        // clear the checkbox variables
        recode = checkBoxRecode.isSelected();
        writeThumbnail = checkBoxWriteThumbnail.isSelected();
        embedThumbnail = checkBoxEmbedThumbnail.isSelected();
        addMetadata = checkBoxMetadata.isSelected();

        // mutually exclusive checkboxes
        checkBoxRecode.setEnabled(!addMetadata);
        checkBoxMetadata.setEnabled(!recode);

        // dependent components
        labelRecodeBox.setEnabled(recode);
        comboBoxRecodeExt.setEnabled(recode);
        checkEmbedThumbnailSupported();
    }

    private static void setComponentsEnabled(JComponent[] videoComponents, boolean videoEnabled, JComponent[] audioComponents, boolean audioEnabled) {
        for (JComponent component : videoComponents) {
            component.setEnabled(videoEnabled);
        }
        for (JComponent component : audioComponents) {
            component.setEnabled(audioEnabled);
        }
    }

    //Additive filters for the combo boxes
    //Example: if the user selects "mp4" in the video format combobox, the video resolution combobox should only show
    //resolutions that are available for mp4 files
    //Then, if the user selects "1920x1080" in the video resolution combobox, the video FPS combobox should only show
    //FPS values that are available for 1920x1080 mp4 files
    //Then, if the user selects "30" in the video FPS combobox, the video codec combobox should only show codecs that
    //are available for 1920x1080 30fps mp4 files
    // I dislike that this code works the way I want it to, or rather, that I didn't find a cleaner way to do it
    public static void doCascadeFilter(JComboBox<String> comboBox) {
        // Video
        if (comboBox.equals(comboBoxVideoExt)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            if (comboBox.getItemAt(videoExt) == null) { videoExt = 0; }
            filterPropertiesMap.put("EXT", comboBox.getItemAt(videoExt));

            updateComboBoxByProperty("RESOLUTION", filterPropertiesMap, comboBoxVideoResolution, "video");
            doCascadeFilter(comboBoxVideoResolution);

        } else if (comboBox.equals(comboBoxVideoResolution)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            filterPropertiesMap.put("EXT", comboBoxVideoExt.getItemAt(videoExt));
            if (comboBox.getItemAt(videoResolution) == null) { videoResolution = 0; }
            filterPropertiesMap.put("RESOLUTION", comboBox.getItemAt(videoResolution));

            updateComboBoxByProperty("FPS", filterPropertiesMap, comboBoxVideoFPS, "video");
            doCascadeFilter(comboBoxVideoFPS);

        } else if (comboBox.equals(comboBoxVideoFPS)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            filterPropertiesMap.put("EXT", comboBoxVideoExt.getItemAt(videoExt));
            filterPropertiesMap.put("RESOLUTION", comboBoxVideoResolution.getItemAt(videoResolution));
            if (comboBox.getItemAt(videoFPS) == null) {
                videoFPS = 0;
            }
            filterPropertiesMap.put("FPS", comboBox.getItemAt(videoFPS));

            updateComboBoxByProperty("VBR", filterPropertiesMap, comboBoxVideoVBR, "video");
        } else if (comboBox.equals(comboBoxVideoVBR)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            filterPropertiesMap.put("EXT", comboBoxVideoExt.getItemAt(videoExt));
            filterPropertiesMap.put("RESOLUTION", comboBoxVideoResolution.getItemAt(videoResolution));
            filterPropertiesMap.put("FPS", comboBoxVideoFPS.getItemAt(videoFPS));
            if (comboBox.getItemAt(videoVBR) == null) {
                videoVBR = 0;
            }
            filterPropertiesMap.put("VBR", comboBox.getItemAt(videoVBR));

            updateComboBoxByProperty("VCODEC", filterPropertiesMap, comboBoxVideoCodec, "video");


            // Audio
        } else if (comboBox.equals(comboBoxAudioExt)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            if (comboBox.getItemAt(audioExt) == null) { audioExt = 0; }
            filterPropertiesMap.put("EXT", comboBox.getItemAt(audioExt));

            updateComboBoxByProperty("CH", filterPropertiesMap, comboBoxAudioChannels, "audio");
            doCascadeFilter(comboBoxAudioChannels);

        } else if (comboBox.equals(comboBoxAudioChannels)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            filterPropertiesMap.put("EXT", comboBoxAudioExt.getItemAt(audioExt));
            if (comboBox.getItemAt(audioChannels) == null) { audioChannels = 0; }
            filterPropertiesMap.put("CH", comboBox.getItemAt(audioChannels));

            updateComboBoxByProperty("ABR", filterPropertiesMap, comboBoxAudioABR, "audio");
            doCascadeFilter(comboBoxAudioABR);

        } else if (comboBox.equals(comboBoxAudioABR)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            filterPropertiesMap.put("EXT", comboBoxAudioExt.getItemAt(audioExt));
            filterPropertiesMap.put("CH", comboBoxAudioChannels.getItemAt(audioChannels));
            if (comboBox.getItemAt(audioABR) == null) { audioABR = 0; }
            filterPropertiesMap.put("ABR", comboBox.getItemAt(audioABR));

            updateComboBoxByProperty("ASR", filterPropertiesMap, comboBoxAudioASR, "audio");
        } else if (comboBox.equals(comboBoxAudioASR)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            filterPropertiesMap.put("EXT", comboBoxAudioExt.getItemAt(audioExt));
            filterPropertiesMap.put("CH", comboBoxAudioChannels.getItemAt(audioChannels));
            filterPropertiesMap.put("ABR", comboBoxAudioABR.getItemAt(audioABR));
            if (comboBox.getItemAt(audioASR) == null) { audioASR = 0; }
            filterPropertiesMap.put("ASR", comboBox.getItemAt(audioASR));

            updateComboBoxByProperty("ACODEC", filterPropertiesMap, comboBoxAudioCodec, "audio");
        }
    }

    private static void updateComboBoxByProperty(String property, Map<String, String> filterPropertiesMap, JComboBox<String> comboBox, String context) {
        if (property != null && filterPropertiesMap != null && !filterPropertiesMap.isEmpty()) {
            Set<String> values = switch (context) {
                case "video" -> getUniqueValuesForVideo(property, filterPropertiesMap);
                case "audio" -> getUniqueValuesForAudio(property, filterPropertiesMap);
                default -> throw new IllegalArgumentException("Invalid context: " + context);
            };

            values.removeIf(value -> value.contains("only") || value.contains("ec"));
            String[] arrayValues = values.toArray(new String[0]);
            sortArrayValues(property, arrayValues);

            SwingGUI.updateComboBox(arrayValues, comboBox);
            comboBox.setSelectedIndex(0);
        }
    }

    private static Set<String> getUniqueValuesForVideo(String property, Map<String, String> filterProperties) {
        return Utils.extractUniqueValuesByPredicate(property, option -> {
            boolean match = option.get("ACODEC").equals("video only");
            for (Map.Entry<String, String> entry : filterProperties.entrySet()) {
                match = match && option.get(entry.getKey()).equals(entry.getValue());
            }
            return match;
        }, tableMap);
    }

    private static Set<String> getUniqueValuesForAudio(String property, Map<String, String> filterProperties) {
        return Utils.extractUniqueValuesByPredicate(property, option -> {
            boolean match = option.get("VCODEC").equals("audio only");
            for (Map.Entry<String, String> entry : filterProperties.entrySet()) {
                match = match && option.get(entry.getKey()).equals(entry.getValue());
            }
            return match;
        }, tableMap);
    }

    protected static void checkEmbedThumbnailSupported() {
        String ext = "";
        if (recode) {
            ext = arrayRecodeExt[recodeExt];
        } else {
            ext = switch (videoAudio) {
                case 0, 1 -> arrayVideoExtensions[videoExt];
                case 2 -> arrayAudioExtensions[audioExt];
                default -> ext;
            };
        }

        if ( ext.isEmpty()) {
            if (advancedSettingsEnabled) {
                System.err.println("[ERROR] Failed to get extension while checking for thumbnail embedding support.");
            }
            return;
        }

        if (writeThumbnail && Utils.containsAny(arrayEmbedThumbnailSupported, ext) ) {
            checkBoxEmbedThumbnail.setEnabled(true);
        } else {
            checkBoxEmbedThumbnail.setSelected(false);
            checkBoxEmbedThumbnail.setEnabled(false);
        }
    }
}
