package main.java;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static main.java.advancedOptions.*;

public class mainWindow extends JFrame {
    protected final JFrame frame;
    private final int windowWidth = 751, windowHeight = 228;
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
        textField_URL.setText("https://www.youtube.com/watch?v=jNQXAC9IVRw");
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
                            videoAudio = comboBoxType.getSelectedIndex();
                            checkType();
                            // 0 = video and audio,  1 = only video,  2 = only audio
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
                                System.out.println(frame.getWidth());
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
                //centerVerticalPanelRow2.setSize(new Dimension(600, 200)); FIXME idk whats wrong here, I wrote this hours ago
                    centerVerticalPanel.add(centerVerticalPanelRow2);

                //dependent on the layout of the centerVerticalPanelRow2
                {
                    //add a new JPanel in the center of the centerVerticalPanelRow2
                    advancedSettingsPanel = new JPanel();
                    advancedSettingsPanel.setLayout(new BoxLayout(advancedSettingsPanel, BoxLayout.LINE_AXIS));
                    advancedSettingsPanel.setVisible(false);
                        centerVerticalPanelRow2.add(advancedSettingsPanel);

                    //dependent on the layout of the advancedSettingsPanel
                    {

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(20, 0)));

                        // Video ----------------------------------------------------------
                        comboBoxVideoExt = new JComboBox<>(arrayVideoExtensions);
                        comboBoxMaker(comboBoxVideoExt, videoExt);
                            advancedSettingsPanel.add(comboBoxVideoExt);
                            comboBoxVideoExt.addActionListener((e) -> { //TODO video format combobox
                                videoExt = comboBoxVideoExt.getSelectedIndex();
                                if (videoAudio == 0) {
                                    comboBoxAudioExt.setSelectedIndex(videoExt);
                                }
                                doCascadeFilter(comboBoxVideoExt);
                            });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxVideoResolution = new JComboBox<>(arrayVideoResolution);
                        comboBoxMaker(comboBoxVideoResolution, videoResolution);
                        advancedSettingsPanel.add(comboBoxVideoResolution);
                            comboBoxVideoResolution.addActionListener((e) -> { //TODO video resolution combobox
                                videoResolution = comboBoxVideoResolution.getSelectedIndex();
                                doCascadeFilter(comboBoxVideoResolution);
                            });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxVideoFPS = new JComboBox<>(arrayVideoFPS);
                        comboBoxMaker(comboBoxVideoFPS, videoFPS);
                        advancedSettingsPanel.add(comboBoxVideoFPS);
                        comboBoxVideoFPS.addActionListener((e) -> { //TODO video FPS combobox
                            videoFPS = comboBoxVideoFPS.getSelectedIndex();
                            doCascadeFilter(comboBoxVideoFPS);
                        });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxVideoCodec = new JComboBox<>(arrayVideoCodec);
                        comboBoxMaker(comboBoxVideoCodec, videoCodec);
                        advancedSettingsPanel.add(comboBoxVideoCodec);
                            comboBoxVideoCodec.addActionListener((e) -> { //TODO video codec combobox
                                videoCodec = comboBoxVideoCodec.getSelectedIndex();
                                doCascadeFilter(comboBoxVideoCodec);
                            });


                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(20, 0)));


                        // Audio ----------------------------------------------------------
                        comboBoxAudioExt = new JComboBox<>(arrayAudioExtensions);
                        comboBoxMaker(comboBoxAudioExt, audioExt);
                        advancedSettingsPanel.add(comboBoxAudioExt);
                        comboBoxAudioExt.addActionListener((e) -> { //TODO audio format combobox
                            audioExt = comboBoxAudioExt.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioExt);
                        });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxAudioChannels = new JComboBox<>(arrayAudioChannels);
                        comboBoxMaker(comboBoxAudioChannels, audioChannels);
                        advancedSettingsPanel.add(comboBoxAudioChannels);
                        comboBoxAudioChannels.addActionListener((e) -> { //TODO audio channels combobox
                            audioChannels = comboBoxAudioChannels.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioChannels);
                        });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxAudioASR = new JComboBox<>(arrayAudioASR);
                        comboBoxMaker(comboBoxAudioASR, audioASR);
                        advancedSettingsPanel.add(comboBoxAudioASR);
                        comboBoxAudioASR.addActionListener((e) -> { //TODO audio ASR combobox
                            audioASR = comboBoxAudioASR.getSelectedIndex();
                            doCascadeFilter(comboBoxAudioASR);
                        });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                        comboBoxAudioCodec = new JComboBox<>(arrayAudioCodec);
                        comboBoxMaker(comboBoxAudioCodec, audioCodec);
                        advancedSettingsPanel.add(comboBoxAudioCodec);
                            comboBoxAudioCodec.addActionListener((e) -> { //TODO audio codec combobox
                                audioCodec = comboBoxAudioCodec.getSelectedIndex();
                                doCascadeFilter(comboBoxAudioCodec);
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

    private void comboBoxMaker(JComboBox<String> comboBox, int selectedIndex) {
        comboBox.setFont(new Font("Tahoma", Font.PLAIN, 18));
        comboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
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
    public static void doCascadeFilter(JComboBox<String> comboBox) {
        // Video
        if (comboBox.equals(comboBoxVideoExt)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();
            filterPropertiesMap.put("EXT", comboBox.getItemAt(videoExt));
            updateComboBoxByProperty("RESOLUTION", filterPropertiesMap, comboBoxVideoResolution, "video");
            doCascadeFilter(comboBoxVideoResolution);
        } else if (comboBox.equals(comboBoxVideoResolution)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();
            filterPropertiesMap.put("EXT", comboBoxVideoExt.getItemAt(videoExt));
            filterPropertiesMap.put("RESOLUTION", comboBox.getItemAt(videoResolution));
            updateComboBoxByProperty("FPS", filterPropertiesMap, comboBoxVideoFPS, "video");
            doCascadeFilter(comboBoxVideoFPS);
        } else if (comboBox.equals(comboBoxVideoFPS)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();
            filterPropertiesMap.put("EXT", comboBoxVideoExt.getItemAt(videoExt));
            filterPropertiesMap.put("RESOLUTION", comboBoxVideoResolution.getItemAt(videoResolution));
            filterPropertiesMap.put("FPS", comboBox.getItemAt(videoFPS));
            updateComboBoxByProperty("VCODEC", filterPropertiesMap, comboBoxVideoCodec, "video");

            // Audio
        } else if (comboBox.equals(comboBoxAudioExt)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();
            filterPropertiesMap.put("EXT", comboBox.getItemAt(audioExt));
            updateComboBoxByProperty("CH", filterPropertiesMap, comboBoxAudioChannels, "audio");
            doCascadeFilter(comboBoxAudioChannels);
        } else if (comboBox.equals(comboBoxAudioChannels)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();
            filterPropertiesMap.put("EXT", comboBoxAudioExt.getItemAt(audioExt));
            filterPropertiesMap.put("CH", comboBox.getItemAt(audioChannels));
            updateComboBoxByProperty("ASR", filterPropertiesMap, comboBoxAudioASR, "audio");
            doCascadeFilter(comboBoxAudioASR);
        } else if (comboBox.equals(comboBoxAudioASR)) {
            Map<String, String> filterPropertiesMap = new HashMap<>();
            filterPropertiesMap.put("EXT", comboBoxAudioExt.getItemAt(audioExt));
            filterPropertiesMap.put("CH", comboBoxAudioChannels.getItemAt(audioChannels));
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

//            System.out.println("--------------------");
//            System.out.println("Property: " + property);
//            System.out.println("Filter Properties: " + filterPropertiesMap);
//            System.out.println("Values: " + values);
//            System.out.println("Array Values: " + Arrays.toString(arrayValues));
//            System.out.println("--------------------");
            updateComboBox(arrayValues, comboBox);
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
