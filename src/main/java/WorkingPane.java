package main.java;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static main.java.MainWorker.*;

public class WorkingPane extends JFrame {
    protected static JFrame workingFrame;
    protected static JPanel panel;
    protected static JPanel panelRow2;
    protected static JLabel label1;
    protected static JLabel labelMessage;
    protected String message = "";
    protected static JProgressBar progressBar;
    protected static JButton buttonCancel;
    public WorkingPane() {
        workingFrame = new JFrame("Working...");

        workingFrame.setMinimumSize(new Dimension(355, 170));
        workingFrame.setPreferredSize(new Dimension(355, 170));
        workingFrame.setLocationRelativeTo(null);
        workingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        workingFrame.setResizable(false);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        workingFrame.add(panel);
        {
            panel.add(Box.createVerticalStrut(5));

            label1 = new JLabel(" Working...");
            label1.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            label1.setAlignmentX(Component.LEFT_ALIGNMENT);
            label1.setHorizontalTextPosition(JLabel.LEFT);
            panel.add(label1);

            panel.add(Box.createVerticalStrut(10));

            labelMessage = new JLabel(" Please wait...");
            labelMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            labelMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
            labelMessage.setHorizontalTextPosition(JLabel.LEFT);
            panel.add(labelMessage);

            panel.add(Box.createVerticalStrut(10));

            progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
            progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
            progressBar.setPreferredSize(new Dimension(300, 20));
            progressBar.putClientProperty("JProgressBar.largeHeight", Boolean.TRUE);
            progressBar.setIndeterminate(true);
            panel.add(progressBar);

            panel.add(Box.createVerticalGlue());

            panelRow2 = new JPanel();
            panelRow2.setLayout(new FlowLayout(FlowLayout.RIGHT));
            panelRow2.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(panelRow2);
            {
                buttonCancel = new JButton("Cancel");
                buttonCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                panelRow2.add(buttonCancel);

                buttonCancel.addActionListener(e -> {
                    for (String binaryFile : binaryFiles) {
                        closeProcess(null, binaryFile);
                    }
                    workingFrame.dispose();
                    JOptionPane.showMessageDialog(null, "Download was cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(downloadStarted);
                    if (downloadStarted) {
                        int delFiles = JOptionPane.showConfirmDialog(null, "Save download progress to resume later?", "Cancelled", JOptionPane.YES_NO_OPTION);
                        if (delFiles == JOptionPane.NO_OPTION) {
                            // an array of all files with .part extension
                            String[] partFiles = new File(filePath).list((dir, name) -> name.endsWith(".ytdl") || name.contains(".part"));
                            if (partFiles == null) {
                                System.out.println("No files to delete.");
                                return;
                            }

                            // delete all files with .part extension
                            for (String part : partFiles) {
                                Path pathToDelete = Paths.get(filePath+ "\\" + part);
                                for (int tries = 0; tries < 5; tries++) {
                                    try {
                                        Files.delete(pathToDelete);
                                        System.out.println("Deleted file: " + pathToDelete.getFileName());
                                        break;
                                    } catch (IOException e1) {
                                        System.out.println("Failed to delete file: " + pathToDelete.getFileName());
                                        e1.printStackTrace(System.out);
                                    }
                                }
                            }
                        }
                    }
                });

            }


            panel.add(Box.createVerticalStrut(5));
        }
        workingFrame.setVisible(true);
    }


    public void setCTitle(String title) {
        label1.setText(title);
    }
    public void setMessage(String message) {
        this.message = message;
        labelMessage.setText(message);
    }

    public void setProgress(int i) {
        if (i < 0 || i > 100) {
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(false);
            return;
        }
        int ip = progressBar.getValue();
        if (i > ip || ip == 100) {
            progressBar.setIndeterminate(false);
            progressBar.setValue(i);
            progressBar.setStringPainted(true);
            progressBar.setString(i + "%");
        }
    }
}
