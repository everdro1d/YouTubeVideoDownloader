package main.java;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import static main.java.advancedOptions.*;

public class mainWindow extends JFrame {
    protected final JFrame frame;
    private final int windowWidth = 730, windowHeight = 228;
        protected JPanel mainPanel;
            protected JPanel northPanel;
                protected JPanel northPanelRow1;
                    protected JLabel labelTitle;
                    protected JLabel labelURL;
                    protected JTextField textField_URL;
                    protected JComboBox<String> comboBoxType;
            protected JPanel centerVerticalPanel;
                protected JPanel centerVerticalPanelRow1;
                    protected JButton buttonDownload;
                    protected JCheckBox checkBoxAdvancedOptions;
                protected JPanel centerVerticalPanelRow2;
                    protected JPanel advancedSettingsPanel;
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

    private final String titleText = "YouTube Video Downloader V2.0";
    private final String[] typeComboBoxOptions = {"Video + Audio", "Only Video", "Only Audio"};



    public mainWindow() {
        frame = new JFrame();
        frame.setTitle(titleText);
        frame.setMinimumSize(new Dimension(windowWidth, windowHeight));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);

        initializeGUIComponents();


        frame.setVisible(true);
        textField_URL.setText("https://www.youtube.com/watch?v=XDoytd99X-E");
    }

    private void initializeGUIComponents() {

        // add a new JPanel in the north of the mainPanel
        northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
            mainPanel.add(northPanel, BorderLayout.NORTH);

            // add a new JPanel in the north of the northPanel
            northPanelRow1 = new JPanel();
            northPanelRow1.setLayout(new BoxLayout(northPanelRow1, BoxLayout.Y_AXIS));
                northPanel.add(northPanelRow1, BorderLayout.NORTH);

            // dependent on the layout of the northPanel
            {
                northPanelRow1.add(Box.createRigidArea(new Dimension(0, 20)));

                // add a new title label in the north of the northPanelRow1
                labelTitle = new JLabel(titleText);
                labelTitle.setFont(new Font("Tahoma", Font.BOLD, 24));
                labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                    northPanelRow1.add(labelTitle);

                northPanelRow1.add(Box.createRigidArea(new Dimension(0, 20)));
                // add a new JPanel in the center of the northPanel
                northPanelRow1 = new JPanel();
                northPanelRow1.setLayout(new BoxLayout(northPanelRow1, BoxLayout.LINE_AXIS));
                    northPanel.add(northPanelRow1, BorderLayout.CENTER);

                // dependent on the layout of the northPanelRow1
                {

                    northPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));

                    //add a new label on the left side of the north panel underneath the title
                    labelURL = new JLabel("URL: ");
                    labelURL.setFont(new Font("Tahoma", Font.PLAIN, 18));
                    labelURL.setHorizontalAlignment(SwingConstants.LEFT);
                    labelURL.setAlignmentX(Component.LEFT_ALIGNMENT);
                        northPanelRow1.add(labelURL);

                    //add a new text field in the center of the north panel underneath the title
                    textField_URL = new JTextField();
                    textField_URL.setFont(new Font("Tahoma", Font.PLAIN, 18));
                    textField_URL.setHorizontalAlignment(SwingConstants.LEFT);
                    textField_URL.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    textField_URL.setAlignmentX(Component.CENTER_ALIGNMENT);
                    textField_URL.setColumns(28);
                        northPanelRow1.add(textField_URL);

                        //add key listener to the text field
                        textField_URL.addKeyListener(new java.awt.event.KeyAdapter() { //TODO URL text field
                            public void keyReleased(java.awt.event.KeyEvent e) {
                                mainWorker.rawURL = textField_URL.getText();
                                if (mainWorker.validURL(mainWorker.rawURL)) {
                                    System.out.println("URL is valid");
                                    checkBoxAdvancedOptions.setEnabled(true);
                                } else {
                                    System.out.println("URL is invalid");
                                    checkBoxAdvancedOptions.setSelected(false);
                                    checkBoxAdvancedOptions.setEnabled(false);
                                    advancedSettingsPanel.setVisible(false);
                                }
                            }
                        });

                    northPanelRow1.add(Box.createRigidArea(new Dimension(10, 0)));

                    //add a new comboBox on the right side of the north panel underneath the title
                    comboBoxType = new JComboBox<>();
                    comboBoxType.setFont(new Font("Tahoma", Font.PLAIN, 18));
                    comboBoxType.setModel(new DefaultComboBoxModel<>(typeComboBoxOptions));
                    comboBoxType.setSelectedIndex(0);
                    comboBoxType.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    comboBoxType.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        northPanelRow1.add(comboBoxType);

                        comboBoxType.addActionListener((e) -> { //TODO type selection comboBox
                            mainWorker.videoAudio = comboBoxType.getSelectedIndex();
                            checkType();
                            // 0 = video and 1 = audio
                        });

                    northPanelRow1.add(Box.createRigidArea(new Dimension(20, 0)));
                }
            }


        //------------------------------ Main Panel Divider ------------------------------\\


            //create a new JPanel boxlayout in the center of the mainPanel
            centerVerticalPanel = new JPanel();
            centerVerticalPanel.setLayout(new BoxLayout(centerVerticalPanel, BoxLayout.Y_AXIS));
                mainPanel.add(centerVerticalPanel, BorderLayout.CENTER);

            //dependent on the layout of the centerVerticalPanel
            {
                //create a new JPanel boxlayout in the first row of the centerVerticalPanel
                centerVerticalPanelRow1 = new JPanel();
                centerVerticalPanelRow1.setLayout(new BoxLayout(centerVerticalPanelRow1, BoxLayout.LINE_AXIS));
                    centerVerticalPanel.add(centerVerticalPanelRow1);

                //dependent on the layout of the centerVerticalPanelRow1
                {
                    //add a new checkbox in the center-right of the centerVerticalPanelRow1
                    checkBoxAdvancedOptions = new JCheckBox("Advanced Settings");
                    checkBoxAdvancedOptions.setFont(new Font("Tahoma", Font.PLAIN, 18));
                    checkBoxAdvancedOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
                    checkBoxAdvancedOptions.setEnabled(false);
                        centerVerticalPanelRow1.add(checkBoxAdvancedOptions);

                        checkBoxAdvancedOptions.addActionListener((e) -> { //TODO advanced options checkbox
                            checkType();
                            if (checkBoxAdvancedOptions.isSelected()) {
                                advancedOptions.readVideoOptionsFromYT();
                                System.out.println("Advanced Settings enabled");
                            } else {
                                getVideoOptions = false;
                                advancedOptions.advancedOptionsEnabled = false;
                                System.out.println("Advanced Settings disabled");
                            }
                            while (advancedOptionsEnabled) {
                                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                if (getVideoOptions) {
                                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.getDefaultCursor().getType()));
                                    break;
                                }
                            }
                            advancedSettingsPanel.setVisible(checkBoxAdvancedOptions.isSelected());
                            frame.pack();
                            if (checkBoxAdvancedOptions.isSelected()) {
                                frame.setMinimumSize(new Dimension(windowWidth, frame.getHeight()));
                            } else {
                                frame.setMinimumSize(new Dimension(windowWidth, windowHeight));
                                frame.setSize(new Dimension(windowWidth, windowHeight));
                            }
                            setAdvancedOptions();
                        });
                }

                centerVerticalPanel.add(Box.createRigidArea(new Dimension(0, 20)));

                //create a new JPanel boxlayout in the second row of the centerVerticalPanel
                centerVerticalPanelRow2 = new JPanel();
                centerVerticalPanelRow2.setLayout(new BoxLayout(centerVerticalPanelRow2, BoxLayout.LINE_AXIS));
                //centerVerticalPanelRow2.setSize(new Dimension(600, 200)); FIXME
                    centerVerticalPanel.add(centerVerticalPanelRow2);

                //dependent on the layout of the centerVerticalPanelRow2
                {
                    //add a new JPanel in the center of the centerVerticalPanelRow2
                    advancedSettingsPanel = new JPanel();
                    advancedSettingsPanel.setLayout(new BoxLayout(advancedSettingsPanel, BoxLayout.LINE_AXIS));
                    advancedSettingsPanel.setVisible(false);
                        centerVerticalPanelRow2.add(advancedSettingsPanel);

                    //dependent on the layout of the advancedSettingsPanel
                    //TODO: add advanced settings
                    {

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(20, 0)));

                        // Video ----------------------------------------------------------
                        comboBoxVideoExt = new JComboBox<>(arrayVideoExtensions);
                        comboBoxMaker(comboBoxVideoExt);
                            advancedSettingsPanel.add(comboBoxVideoExt);
                            comboBoxVideoExt.addActionListener((e) -> { //TODO video format combobox
                                videoExt = comboBoxVideoExt.getSelectedIndex();
                                comboBoxAudioExt.setSelectedIndex(videoExt);
                                updateResolutionComboBox((String) comboBoxVideoExt.getSelectedItem());
                            });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxVideoResolution = new JComboBox<>(arrayVideoResolution);
                        comboBoxMaker(comboBoxVideoResolution);
                        advancedSettingsPanel.add(comboBoxVideoResolution);
                            comboBoxVideoResolution.addActionListener((e) -> { //TODO video resolution combobox
                                advancedOptions.videoResolution = comboBoxVideoResolution.getSelectedIndex();
                            });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxVideoFPS = new JComboBox<>(arrayVideoFPS);
                        comboBoxMaker(comboBoxVideoFPS);
                        advancedSettingsPanel.add(comboBoxVideoFPS);
                        comboBoxVideoFPS.addActionListener((e) -> { //TODO video FPS combobox
                            advancedOptions.videoFPS = comboBoxVideoFPS.getSelectedIndex();
                        });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxVideoCodec = new JComboBox<>(arrayVideoCodec);
                        comboBoxMaker(comboBoxVideoCodec);
                        advancedSettingsPanel.add(comboBoxVideoCodec);
                            comboBoxVideoCodec.addActionListener((e) -> { //TODO video codec combobox
                                advancedOptions.videoCodec = comboBoxVideoCodec.getSelectedIndex();
                            });


                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(20, 0)));


                        // Audio ----------------------------------------------------------
                        comboBoxAudioExt = new JComboBox<>(arrayAudioExtensions);
                        comboBoxMaker(comboBoxAudioExt);
                        advancedSettingsPanel.add(comboBoxAudioExt);
                            comboBoxAudioExt.addActionListener((e) -> { //TODO audio format combobox
                                advancedOptions.audioExt = comboBoxAudioExt.getSelectedIndex();
                            });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxAudioChannels = new JComboBox<>(arrayAudioChannels);
                        comboBoxMaker(comboBoxAudioChannels);
                        advancedSettingsPanel.add(comboBoxAudioChannels);
                            comboBoxAudioChannels.addActionListener((e) -> { //TODO audio channels combobox
                                advancedOptions.audioChannels = comboBoxAudioChannels.getSelectedIndex();
                            });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxAudioASR = new JComboBox<>(arrayAudioASR);
                        comboBoxMaker(comboBoxAudioASR);
                        advancedSettingsPanel.add(comboBoxAudioASR);
                            comboBoxAudioASR.addActionListener((e) -> { //TODO audio ASR combobox
                                advancedOptions.audioASR = comboBoxAudioASR.getSelectedIndex();
                            });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxAudioCodec = new JComboBox<>(arrayAudioCodec);
                        comboBoxMaker(comboBoxAudioCodec);
                        advancedSettingsPanel.add(comboBoxAudioCodec);
                            comboBoxAudioCodec.addActionListener((e) -> { //TODO audio codec combobox
                                advancedOptions.audioCodec = comboBoxAudioCodec.getSelectedIndex();
                            });


                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(20, 0)));

                    }
                }
                centerVerticalPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }


        //------------------------------ Main Panel Divider ------------------------------\\


        // add a new JPanel in the south of the mainPanel
        southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            {
                // add a new button in the center of the southPanel
                buttonDownload = new JButton("Download");
                buttonDownload.setFont(new Font("Tahoma", Font.PLAIN, 18));
                buttonDownload.setAlignmentX(Component.CENTER_ALIGNMENT);
                    southPanel.add(buttonDownload);

                    buttonDownload.addActionListener((e) -> mainWorker.downloadButtonClicked());
            }

    }

    private void comboBoxMaker(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Tahoma", Font.PLAIN, 18));
        comboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboBox.setSelectedIndex(0);
    }

    private void checkType() {
        switch (comboBoxType.getSelectedIndex()) {
            case 0: // video + audio (audio is set to match video index)
            case 1: // video only
                comboBoxVideoExt.setEnabled(true);
                comboBoxAudioExt.setEnabled(false);
                break;
            case 2: // audio only
                comboBoxVideoExt.setEnabled(false);
                comboBoxAudioExt.setEnabled(true);
                break;
        }
    }

    //TODO make combobox filter
    private void updateResolutionComboBox(String videoExtString) {
        if (videoExtString != null) {
            Set<String> resolutions = getUniqueValues("RESOLUTION", option -> option.get("EXT").equals(videoExtString) && option.get("ACODEC").equals("video only"));
            String[] arrayVideoResolution = resolutions.toArray(new String[0]);
            Arrays.sort(arrayVideoResolution, Comparator.comparingInt(s -> -1 * Integer.parseInt(s.split("x")[1])));
            updateComboBox(arrayVideoResolution, comboBoxVideoResolution);
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
        int padding = 25; // Adjust this value as needed
        int newMaxWidth = maxWidth + padding;

        // Set the width of the JComboBox to the maximum width
        Dimension dimension = new Dimension(newMaxWidth, comboBox.getPreferredSize().height);
        comboBox.setMinimumSize(dimension);
        comboBox.setPreferredSize(dimension);
        comboBox.setMaximumSize(dimension);
    }
}
