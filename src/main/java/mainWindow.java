package main.java;

import javax.swing.*;
import java.awt.*;

public class mainWindow extends JFrame {
    protected final JFrame frame;
        protected JPanel mainPanel;
            protected JPanel northPanel;
                protected JPanel northPanelRow1;
                    protected JLabel labelTitle;
                    protected JLabel labelURL;
                    protected JTextField textField_URL;
            protected JPanel centerVerticalPanel;
                protected JPanel centerVerticalPanelRow1;
                    protected JButton buttonDownload;
                    protected JCheckBox checkBoxAdvanced;
                protected JPanel centerVerticalPanelRow2;
                    protected JPanel advancedSettingsPanel;
                        protected JComboBox<String> comboBoxVideoAudio;
                        protected JComboBox comboBoxVideoOptions;
                        protected JComboBox<String> comboBoxAudioOptions;
            protected JPanel southPanel;




    private final String titleText = "YouTube Video Downloader V2.0";
    private final String[] typeComboBoxOptions = {"Video", "Audio"};



    public mainWindow() {
        frame = new JFrame();
        frame.setTitle(titleText);
        frame.setMinimumSize(new Dimension(610, 228));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);

        initializeGUIComponents();

        frame.setVisible(true);
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
                        textField_URL.addKeyListener(new java.awt.event.KeyAdapter() {
                            public void keyReleased(java.awt.event.KeyEvent e) {
                                mainWorker.rawURL = textField_URL.getText();
                                if (mainWorker.validURL(mainWorker.rawURL)) {
                                    System.out.println("URL is valid");
                                    checkBoxAdvanced.setEnabled(true);
                                } else {
                                    System.out.println("URL is invalid");
                                    checkBoxAdvanced.setSelected(false);
                                    checkBoxAdvanced.setEnabled(false);
                                }
                            }
                        });

                    northPanelRow1.add(Box.createRigidArea(new Dimension(10, 0)));

                    //add a new comboBox on the right side of the north panel underneath the title
                    comboBoxVideoAudio = new JComboBox<>();
                    comboBoxVideoAudio.setFont(new Font("Tahoma", Font.PLAIN, 18));
                    comboBoxVideoAudio.setModel(new DefaultComboBoxModel<>(typeComboBoxOptions));
                    comboBoxVideoAudio.setSelectedIndex(0);
                    comboBoxVideoAudio.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    comboBoxVideoAudio.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        northPanelRow1.add(comboBoxVideoAudio);

                        comboBoxVideoAudio.addActionListener((e) -> {
                            mainWorker.videoAudio = comboBoxVideoAudio.getSelectedIndex();
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
                    checkBoxAdvanced = new JCheckBox("Advanced Settings");
                    checkBoxAdvanced.setFont(new Font("Tahoma", Font.PLAIN, 18));
                    checkBoxAdvanced.setAlignmentX(Component.CENTER_ALIGNMENT);
                    checkBoxAdvanced.setEnabled(false);
                        centerVerticalPanelRow1.add(checkBoxAdvanced);

                        checkBoxAdvanced.addActionListener((e) -> {
                            if (checkBoxAdvanced.isSelected()) {
                                mainWorker.readVideoOptionsFromYT();
                                System.out.println("Advanced Settings enabled");
                            } else {
                                System.out.println("Advanced Settings disabled");
                            }
                            advancedSettingsPanel.setVisible(checkBoxAdvanced.isSelected());
                            frame.pack();
                        });
                }

                centerVerticalPanel.add(Box.createRigidArea(new Dimension(0, 20)));

                //create a new JPanel boxlayout in the second row of the centerVerticalPanel
                centerVerticalPanelRow2 = new JPanel();
                centerVerticalPanelRow2.setLayout(new BoxLayout(centerVerticalPanelRow2, BoxLayout.LINE_AXIS));
                centerVerticalPanelRow2.setSize(new Dimension(600, 200));
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
                        //add two combo boxes in the center of the advancedSettingsPanel
                        comboBoxVideoOptions = new JComboBox<String>(mainWorker.arrayVideoFormats);
                        comboBoxVideoOptions.setFont(new Font("Tahoma", Font.PLAIN, 18));
                        comboBoxVideoOptions.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        comboBoxVideoOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
                        comboBoxVideoOptions.setMaximumSize(new Dimension(100, 30));
                            advancedSettingsPanel.add(comboBoxVideoOptions);

                            comboBoxVideoOptions.addActionListener((e) -> {
                                comboBoxVideoOptions.getItemAt(comboBoxVideoOptions.getSelectedIndex());
                            });

                        advancedSettingsPanel.add(Box.createRigidArea(new Dimension(20, 0)));

                        comboBoxAudioOptions = new JComboBox<String>(mainWorker.arrayAudioFormats);
                        comboBoxAudioOptions.setFont(new Font("Tahoma", Font.PLAIN, 18));
                        comboBoxAudioOptions.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        comboBoxAudioOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
                        comboBoxAudioOptions.setMaximumSize(new Dimension(100, 30));
                            advancedSettingsPanel.add(comboBoxAudioOptions);

                            comboBoxVideoOptions.addActionListener((e) -> {
                                comboBoxAudioOptions.getItemAt(comboBoxAudioOptions.getSelectedIndex());
                            });

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

}
