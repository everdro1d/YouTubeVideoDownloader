package main.java;

import javax.swing.*;
import java.awt.*;

public class WorkingPane extends JFrame {
    protected static JFrame workingFrame;
    protected static JPanel panel;
    protected static JLabel label1;
    protected static JLabel labelMessage;
    protected String message = "";
    protected static JProgressBar progressBar;
    public WorkingPane() {
        //TODO clean up the .part file if the process is killed
        workingFrame = new JFrame("Working...");

        workingFrame.setMinimumSize(new Dimension(355, 150));
        workingFrame.setPreferredSize(new Dimension(355, 150));
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
            progressBar.setIndeterminate(true);
            panel.add(progressBar);

            panel.add(Box.createVerticalStrut(10));

            panel.add(Box.createVerticalStrut(5));
        }
        workingFrame.setVisible(true);
    }

    public void setMessage(String message) {
        this.message = message;
        labelMessage.setText(message);
    }

    public void setProgress(int i) {
        int ip = progressBar.getValue();
        if (i > ip || ip == 100) {
            progressBar.setIndeterminate(false);
            progressBar.setValue(i);
            progressBar.setStringPainted(true);
            progressBar.setString(i + "%");
        }
    }
}
