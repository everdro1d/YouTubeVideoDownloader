package main.java;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static main.java.AdvancedSettings.*;
import static main.java.MainWorker.darkMode;
import static main.java.MainWorker.prefs;

public class MainWindow extends JFrame {
    protected static JFrame frame;

        protected JPanel mainPanel;
            protected JPanel northPanel;
                protected JPanel northPanelBorder1;
                    protected JPanel northPanelWestBorder;
                    protected JPanel northPanelEastBorder;
                        protected JToggleButton lightDarkModeButton;
                    protected JPanel northPanelRow1;
                        protected JLabel labelTitle;
                        protected CustomSeparator separatorNP1;
                        protected JLabel labelURL;
                        protected JTextField textField_URL;
                        protected boolean validURL;
                        protected JComboBox<String> comboBoxType;
            protected JPanel centerVerticalPanel;
                protected JPanel centerVerticalPanelRow1;
                    protected JLabel validURLLabel;
                    protected JCheckBox checkBoxAdvancedSettings;
                protected JPanel centerVerticalPanelRow2;
                    protected JPanel advancedSettingsPanelRow1;
                        // Video
                        protected JLabel labelVideoExt;
                        protected JLabel labelVideoResolution;
                        protected JLabel labelVideoCodec;
                        protected JLabel labelVideoFPS;

                        // Audio
                        protected JLabel labelAudioExt;
                        protected JLabel labelAudioChannels;
                        protected JLabel labelAudioCodec;
                        protected JLabel labelAudioASR;
                    protected JPanel advancedSettingsPanelRow2;
                        // Video
                        protected static JComboBox<String> comboBoxVideoExt;
                        protected static JComboBox<String> comboBoxVideoResolution;
                        protected static JComboBox<String> comboBoxVideoCodec;
                        protected static JComboBox<String> comboBoxVideoFPS;

                        // Audio
                        protected static JComboBox<String> comboBoxAudioChannels;
                        protected static JComboBox<String> comboBoxAudioExt;
                        protected static JComboBox<String> comboBoxAudioCodec;
                        protected static JComboBox<String> comboBoxAudioASR;
            protected JPanel southPanel;
                protected CustomSeparator separatorSP;
                protected JPanel southPanelRow1;
                    protected JButton buttonFileChooser;
                    protected JButton buttonDownload;
            protected JPanel eastPanel;
            protected JPanel westPanel;
    private final String titleText = "YouTube Video Downloader V2.0";
    private final String[] typeComboBoxOptions = {"Video + Audio", "Only Video", "Only Audio"};
    protected static final int windowWidth = 820;
    protected final int windowHeight = 245;
    protected final int windowHeightExpanded = 360;
    protected final int fontSize = 18;
    protected static final String fontName = "Tahoma";


    public MainWindow() {
        frame = new JFrame();
        frame.setTitle(titleText);
        frame.setMinimumSize(new Dimension(windowWidth, windowHeight));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);

        initializeGUIComponents();


        frame.setVisible(true);
        textField_URL.setText("https://www.youtube.com/watch?v=jNQXAC9IVRw"); //TODO remove this
    }

    private void initializeGUIComponents() {

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
                    //this is just to make the title look centered
                    northPanelWestBorder.add(Box.createRigidArea(new Dimension(70, 0)));
                }
                // add a new JPanel in the center of the northPanelBorder1
                northPanelRow1 = new JPanel();
                northPanelRow1.setLayout(new BoxLayout(northPanelRow1, BoxLayout.Y_AXIS));
                northPanelBorder1.add(northPanelRow1, BorderLayout.CENTER);

                // dependent on the layout of the northPanelRow1
                {
                    northPanelRow1.add(Box.createRigidArea(new Dimension(0, 5)));

                    // add a new title label in the north of the northPanelRow1
                    labelTitle = new JLabel(titleText);
                    labelTitle.setFont(new Font(fontName, Font.BOLD, 24));
                    labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                    northPanelRow1.add(labelTitle);

                    northPanelRow1.add(Box.createRigidArea(new Dimension(0, 10)));

                    // add a new separator in the north of the northPanelRow1
                    separatorNP1 = new CustomSeparator(2, 3);
                    northPanelRow1.add(separatorNP1);

                    northPanelRow1.add(Box.createRigidArea(new Dimension(0, 30)));

                    // add a new JPanel in the center of the northPanel
                    northPanelRow1 = new JPanel();
                    northPanelRow1.setLayout(new BoxLayout(northPanelRow1, BoxLayout.LINE_AXIS));
                    northPanel.add(northPanelRow1, BorderLayout.CENTER);

                    // dependent on the layout of the northPanelRow1
                    {

                        northPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));

                        //add a new label on the left side of the north panel underneath the title
                        labelURL = new JLabel("URL: ");
                        labelURL.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelURL.setHorizontalAlignment(SwingConstants.LEFT);
                        labelURL.setAlignmentX(Component.LEFT_ALIGNMENT);
                        northPanelRow1.add(labelURL);

                        //add a new text field in the center of the north panel underneath the title
                        textField_URL = new JTextField();
                        textField_URL.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        textField_URL.setHorizontalAlignment(SwingConstants.LEFT);
                        textField_URL.setAlignmentX(Component.CENTER_ALIGNMENT);
                        textField_URL.setColumns(28);
                        northPanelRow1.add(textField_URL);

                        textField_URL.addKeyListener(new java.awt.event.KeyAdapter() {
                            public void keyReleased(java.awt.event.KeyEvent e) {
                                MainWorker.rawURL = textField_URL.getText();
                                if (MainWorker.rawURL == null || MainWorker.rawURL.isEmpty()) {
                                    validURL = false;
                                } else {
                                    validURL = MainWorker.validURL(MainWorker.rawURL);
                                }
                                validURLLabel.setText(validURL ? "URL is valid" : "URL is invalid");

                                if (validURL) {
                                    checkBoxAdvancedSettings.setEnabled(true);
                                } else {
                                    checkBoxAdvancedSettings.setSelected(false);
                                    checkBoxAdvancedSettings.setEnabled(false);
                                    advancedSettingsPanelRow1.setVisible(false);
                                    advancedSettingsPanelRow2.setVisible(false);
                                }
                            }
                        });

                        northPanelRow1.add(Box.createRigidArea(new Dimension(10, 0)));

                        //add a new comboBox on the right side of the north panel underneath the title
                        comboBoxType = new JComboBox<>();
                        comboBoxType.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        comboBoxType.setModel(new DefaultComboBoxModel<>(typeComboBoxOptions));
                        comboBoxType.setSelectedIndex(videoAudio);
                        comboBoxType.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        northPanelRow1.add(comboBoxType);

                        comboBoxType.addActionListener((e) -> {
                            videoAudio = comboBoxType.getSelectedIndex();
                            checkType();
                            // 0 = video and audio,  1 = only video,  2 = only audio
                        });

                        northPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));
                    }
                }

                // add a new JPanel in the east of the northPanelBorder1
                northPanelEastBorder = new JPanel();
                northPanelEastBorder.setLayout(new BoxLayout(northPanelEastBorder, BoxLayout.X_AXIS));
                northPanelBorder1.add(northPanelEastBorder, BorderLayout.EAST);
                {
                    lightDarkModeButton = new JToggleButton();
                    lightDarkModeButton.setBorderPainted(false);
                    lightDarkModeButton.setContentAreaFilled(false);
                    lightDarkModeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    Icon sunIcon = new ImageIcon("src/main/resources/sunIcon.png");
                    Icon moonIcon = new ImageIcon("src/main/resources/moonIcon.png");
                    if (prefs.getBoolean("darkMode", false)) {
                        lightDarkModeButton.setSelectedIcon(sunIcon);
                        lightDarkModeButton.setIcon(moonIcon);
                    } else {
                        lightDarkModeButton.setSelectedIcon(moonIcon);
                        lightDarkModeButton.setIcon(sunIcon);
                    }
                    northPanelEastBorder.add(lightDarkModeButton, BorderLayout.EAST);

                    lightDarkModeButton.addActionListener((e) -> {
                        darkMode = !darkMode;
                        MainWorker.lightDarkMode();
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
                    //add a jlabel on the left side of the centerVerticalPanelRow1
                    centerVerticalPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));

                    validURLLabel = new JLabel(validURL ? "URL is valid  " : "URL is invalid");
                    validURLLabel.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    validURLLabel.setHorizontalTextPosition(SwingConstants.LEFT);
                    validURLLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        centerVerticalPanelRow1.add(validURLLabel);

                    centerVerticalPanelRow1.add(Box.createRigidArea(new Dimension(10, 0)));

                    //add a new checkbox in the center-right of the centerVerticalPanelRow1
                    checkBoxAdvancedSettings = new JCheckBox("Advanced Settings");
                    checkBoxAdvancedSettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    checkBoxAdvancedSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
                    checkBoxAdvancedSettings.setEnabled(false);
                        centerVerticalPanelRow1.add(checkBoxAdvancedSettings);

                        checkBoxAdvancedSettings.addActionListener((e) -> {
                            checkType();

                            if (checkBoxAdvancedSettings.isSelected()) { // there are two identical if statements because of the way the code is structured
                                AdvancedSettings.readVideoOptionsFromYT();

                            } else {
                                getVideoOptions = false;
                                AdvancedSettings.advancedSettingsEnabled = false;
                            }

                            while (advancedSettingsEnabled) {
                                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                                if (getVideoOptions) {
                                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.getDefaultCursor().getType()));
                                    break;
                                }
                            }
                            advancedSettingsPanelRow1.setVisible(checkBoxAdvancedSettings.isSelected());
                            advancedSettingsPanelRow2.setVisible(checkBoxAdvancedSettings.isSelected());
                            frame.pack();

                            if (checkBoxAdvancedSettings.isSelected()) { // there are two identical if statements because of the way the code is structured
                                frame.setMinimumSize(new Dimension(windowWidth, windowHeightExpanded));
                                frame.setSize(new Dimension(windowWidth, windowHeightExpanded));

                                advancedSettingsPanelRow2.setSize(advancedSettingsPanelRow1.getSize());
                            } else {
                                frame.setMinimumSize(new Dimension(windowWidth, windowHeight));
                                frame.setSize(new Dimension(windowWidth, windowHeight));
                            }

                            setadvancedSettings();
                        });
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
                        labelVideoExt.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelVideoExt.setAlignmentX(Component.LEFT_ALIGNMENT);
                            advancedSettingsPanelRow1.add(labelVideoExt);

                        comboBoxVideoExt = new JComboBox<>(arrayVideoExtensions);
                        comboBoxMaker(comboBoxVideoExt, videoExt);
                        advancedSettingsPanelRow1.add(comboBoxVideoExt);
                        comboBoxVideoExt.addActionListener((e) -> {
                            videoExt = comboBoxVideoExt.getSelectedIndex();
                            if (videoAudio == 0) {
                                comboBoxAudioExt.setSelectedIndex(videoExt);
                            }
                            doCascadeFilter(comboBoxVideoExt);
                        });


                        advancedSettingsPanelRow1.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelVideoResolution = new JLabel("Resolution: ");
                        labelVideoResolution.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelVideoResolution.setAlignmentX(Component.LEFT_ALIGNMENT);
                            advancedSettingsPanelRow1.add(labelVideoResolution);

                        comboBoxVideoResolution = new JComboBox<>(arrayVideoResolution);
                        comboBoxMaker(comboBoxVideoResolution, videoResolution);
                        advancedSettingsPanelRow1.add(comboBoxVideoResolution);
                        comboBoxVideoResolution.addActionListener((e) -> {
                            videoResolution = comboBoxVideoResolution.getSelectedIndex();
                            doCascadeFilter(comboBoxVideoResolution);
                        });


                        advancedSettingsPanelRow1.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelVideoFPS = new JLabel("FPS: ");
                        labelVideoFPS.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelVideoFPS.setAlignmentX(Component.LEFT_ALIGNMENT);
                            advancedSettingsPanelRow1.add(labelVideoFPS);

                        comboBoxVideoFPS = new JComboBox<>(arrayVideoFPS);
                        comboBoxMaker(comboBoxVideoFPS, videoFPS);
                        advancedSettingsPanelRow1.add(comboBoxVideoFPS);
                        comboBoxVideoFPS.addActionListener((e) -> {
                            videoFPS = comboBoxVideoFPS.getSelectedIndex();
                            doCascadeFilter(comboBoxVideoFPS);
                        });


                        advancedSettingsPanelRow1.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelVideoCodec = new JLabel("Codec: ");
                        labelVideoCodec.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelVideoCodec.setAlignmentX(Component.LEFT_ALIGNMENT);
                            advancedSettingsPanelRow1.add(labelVideoCodec);

                        comboBoxVideoCodec = new JComboBox<>(arrayVideoCodec);
                        comboBoxMaker(comboBoxVideoCodec, videoCodec);
                        advancedSettingsPanelRow1.add(comboBoxVideoCodec);
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
                        labelAudioExt.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelAudioExt.setAlignmentX(Component.LEFT_ALIGNMENT);
                        advancedSettingsPanelRow2.add(labelAudioExt);

                        comboBoxAudioExt = new JComboBox<>(arrayAudioExtensions);
                        comboBoxMaker(comboBoxAudioExt, audioExt);
                        advancedSettingsPanelRow2.add(comboBoxAudioExt);
                        comboBoxAudioExt.addActionListener((e) -> {
                            audioExt = comboBoxAudioExt.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioExt);
                        });


                        advancedSettingsPanelRow2.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelAudioChannels = new JLabel("Channels: ");
                        labelAudioChannels.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelAudioChannels.setAlignmentX(Component.LEFT_ALIGNMENT);
                        advancedSettingsPanelRow2.add(labelAudioChannels);

                        comboBoxAudioChannels = new JComboBox<>(arrayAudioChannels);
                        comboBoxMaker(comboBoxAudioChannels, audioChannels);
                        advancedSettingsPanelRow2.add(comboBoxAudioChannels);
                        comboBoxAudioChannels.addActionListener((e) -> {
                            audioChannels = comboBoxAudioChannels.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioChannels);
                        });


                        advancedSettingsPanelRow2.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelAudioASR = new JLabel("ASR: ");
                        labelAudioASR.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelAudioASR.setAlignmentX(Component.LEFT_ALIGNMENT);
                        advancedSettingsPanelRow2.add(labelAudioASR);

                        comboBoxAudioASR = new JComboBox<>(arrayAudioASR);
                        comboBoxMaker(comboBoxAudioASR, audioASR);
                        advancedSettingsPanelRow2.add(comboBoxAudioASR);
                        comboBoxAudioASR.addActionListener((e) -> {
                            audioASR = comboBoxAudioASR.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioASR);
                        });


                        advancedSettingsPanelRow2.add(Box.createRigidArea(new Dimension(5, 0)));


                        labelAudioCodec = new JLabel("Codec: ");
                        labelAudioCodec.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        labelAudioCodec.setAlignmentX(Component.LEFT_ALIGNMENT);
                        advancedSettingsPanelRow2.add(labelAudioCodec);

                        comboBoxAudioCodec = new JComboBox<>(arrayAudioCodec);
                        comboBoxMaker(comboBoxAudioCodec, audioCodec);
                        advancedSettingsPanelRow2.add(comboBoxAudioCodec);
                        comboBoxAudioCodec.addActionListener((e) -> {
                            audioCodec = comboBoxAudioCodec.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioCodec);
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

            separatorSP = new CustomSeparator(6, 4);
            southPanel.add(separatorSP);

            southPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            // add a new JPanel in the south of the mainPanel
            southPanelRow1 = new JPanel();
            southPanelRow1.setLayout(new FlowLayout());
                southPanel.add(southPanelRow1, BorderLayout.SOUTH);
            {
                //add a new FileChooser button in the center of the southPanel
                buttonFileChooser = new JButton("Choose Folder");
                buttonFileChooser.setFont(new Font(fontName, Font.PLAIN, fontSize+2));
                buttonFileChooser.setPreferredSize(new Dimension(185, 40));
                buttonFileChooser.setAlignmentX(Component.CENTER_ALIGNMENT);
                Icon folderIcon = new ImageIcon("src/main/resources/folderIcon.png");
                buttonFileChooser.setIcon(folderIcon);
                southPanelRow1.add(buttonFileChooser);

                buttonFileChooser.addActionListener((e) -> {
                    MainWorker.filePath = MainWorker.openFileChooser();
                });


                southPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));


                // add a new button in the center of the southPanel
                buttonDownload = new JButton("Download");
                buttonDownload.setFont(new Font(fontName, Font.PLAIN, fontSize+2));
                buttonDownload.setPreferredSize(new Dimension(160, 40));
                buttonDownload.setAlignmentX(Component.CENTER_ALIGNMENT);
                Icon saveIcon = new ImageIcon("src/main/resources/saveIcon.png");
                buttonDownload.setIcon(saveIcon);
                southPanelRow1.add(buttonDownload);

                buttonDownload.addActionListener((e) -> MainWorker.downloadButtonClicked());


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
    }

    protected void coloringModeChange() {
        Color backgroundColor = new Color(darkMode ? 0x2B2B2B : 0xE7E7E7);

        Color separatorNP1Color = new Color(darkMode ? 0x46494b : 0xdcdcdc);
        Color separatorSPColor = new Color(darkMode ? 0x595959 : 0xc2c2c2);

        Color advSettingsPanelColor = new Color(darkMode ? 0x303234 : 0xe0e0e0);


        frame.getContentPane().setBackground(backgroundColor);

        separatorNP1.setBackground(separatorNP1Color);
        separatorSP.setBackground(separatorSPColor);

        centerVerticalPanelRow2.setBackground(advSettingsPanelColor);
        advancedSettingsPanelRow1.setBackground(advSettingsPanelColor);
        advancedSettingsPanelRow2.setBackground(advSettingsPanelColor);
    }

    private void comboBoxMaker(JComboBox<String> comboBox, int selectedIndex) {
        comboBox.setFont(new Font(fontName, Font.PLAIN, fontSize));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setSelectedIndex(selectedIndex);
    }

    private void checkType() {
        switch (comboBoxType.getSelectedIndex()) {
            case 0: // video + audio (default)
                comboBoxVideoExt.setEnabled(true);
                comboBoxVideoResolution.setEnabled(true);
                comboBoxVideoFPS.setEnabled(true);
                comboBoxVideoCodec.setEnabled(true);

                comboBoxAudioExt.setEnabled(true);
                comboBoxAudioChannels.setEnabled(true);
                comboBoxAudioASR.setEnabled(true);
                comboBoxAudioCodec.setEnabled(true);
                break;

            case 1: // video only
                comboBoxVideoExt.setEnabled(true);
                comboBoxVideoResolution.setEnabled(true);
                comboBoxVideoFPS.setEnabled(true);
                comboBoxVideoCodec.setEnabled(true);

                comboBoxAudioExt.setEnabled(false);
                comboBoxAudioChannels.setEnabled(false);
                comboBoxAudioASR.setEnabled(false);
                comboBoxAudioCodec.setEnabled(false);
                break;

            case 2: // audio only
                comboBoxVideoExt.setEnabled(false);
                comboBoxVideoResolution.setEnabled(false);
                comboBoxVideoFPS.setEnabled(false);
                comboBoxVideoCodec.setEnabled(false);

                comboBoxAudioExt.setEnabled(true);
                comboBoxAudioChannels.setEnabled(true);
                comboBoxAudioASR.setEnabled(true);
                comboBoxAudioCodec.setEnabled(true);
                break;
        }
    }

    //Additive filters for the combo boxes
    //Example: if the user selects "mp4" in the video format combobox, the video resolution combobox should only show
    //resolutions that are available for mp4 files
    //Then, if the user selects "1920x1080" in the video resolution combobox, the video FPS combobox should only show
    //FPS values that are available for 1920x1080 mp4 files
    //Then, if the user selects "30" in the video FPS combobox, the video codec combobox should only show codecs that
    //are available for 1920x1080 30fps mp4 files
    // I dislike that this works the way i want it to, or rather, that I didn't find a cleaner way to do it
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
            if (comboBox.getItemAt(videoFPS) == null) { videoFPS = 0; }
            filterPropertiesMap.put("FPS", comboBox.getItemAt(videoFPS));

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

            updateComboBoxByProperty("ASR", filterPropertiesMap, comboBoxAudioASR, "audio");
            doCascadeFilter(comboBoxAudioASR);

        } else if (comboBox.equals(comboBoxAudioASR)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();

            filterPropertiesMap.put("EXT", comboBoxAudioExt.getItemAt(audioExt));
            filterPropertiesMap.put("CH", comboBoxAudioChannels.getItemAt(audioChannels));
            if (comboBox.getItemAt(audioASR) == null) { audioASR = 0; }
            filterPropertiesMap.put("ASR", comboBox.getItemAt(audioASR));

            updateComboBoxByProperty("ACODEC", filterPropertiesMap, comboBoxAudioCodec, "audio");
        }
    }

    private static void updateComboBoxByProperty(String property, Map<String, String> filterPropertiesMap, JComboBox<String> comboBox, String context) {
        if (property != null && filterPropertiesMap != null && !filterPropertiesMap.isEmpty()) {
            Set<String> values;

            switch (context) {
                case "video":
                    values = getUniqueValuesForVideo(property, filterPropertiesMap);
                    break;
                case "audio":
                    values = getUniqueValuesForAudio(property, filterPropertiesMap);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid context: " + context);
            }

            values.removeIf(value -> value.contains("only") || value.contains("ec"));
            String[] arrayValues = values.toArray(new String[0]);
            sortArrayValues(property, arrayValues);

            updateComboBox(arrayValues, comboBox);
            comboBox.setSelectedIndex(0);
        }
    }

    public static void updateComboBox(String[] array, JComboBox<String> comboBox) {
        comboBox.setModel(new DefaultComboBoxModel<>(array));

        // Determine the maximum width among all items
        int maxWidth = 0;
        FontMetrics fontMetrics = comboBox.getFontMetrics(comboBox.getFont());

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            int textWidth = fontMetrics.stringWidth(comboBox.getItemAt(i));
            maxWidth = Math.max(maxWidth, textWidth);
        }

        // Add some padding to the maxWidth to accommodate borders and padding
        int padding = 40; // Adjust this value as needed
        int newMaxWidth = maxWidth + padding;

        // Set the width of the JComboBox to the maximum width
        Dimension dimension = new Dimension(newMaxWidth, comboBox.getPreferredSize().height);
        comboBox.setMinimumSize(dimension);
        comboBox.setPreferredSize(dimension);
        comboBox.setMaximumSize(dimension);
    }

    private static Set<String> getUniqueValuesForVideo(String property, Map<String, String> filterProperties) {
        return getUniqueValues(property, option -> {
            boolean match = option.get("ACODEC").equals("video only");
            for (Map.Entry<String, String> entry : filterProperties.entrySet()) {
                match = match && option.get(entry.getKey()).equals(entry.getValue());
            }
            return match;
        });
    }

    private static Set<String> getUniqueValuesForAudio(String property, Map<String, String> filterProperties) {
        return getUniqueValues(property, option -> {
            boolean match = option.get("VCODEC").equals("audio only");
            for (Map.Entry<String, String> entry : filterProperties.entrySet()) {
                match = match && option.get(entry.getKey()).equals(entry.getValue());
            }
            return match;
        });
    }
}
