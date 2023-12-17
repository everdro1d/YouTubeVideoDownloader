package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.Objects;

import static main.java.MainWindow.frame;
import static main.java.MainWorker.*;

public class WorkingPane extends JFrame {
    protected static JFrame workingFrame;
    protected static JPanel panel;
    protected static JPanel panelRow2;
    protected static JLabel label1;
    protected static JLabel labelMessage;
    protected static JProgressBar progressBar;
    protected static JButton cancelButton;

    protected static JDialog cancelledDialog;
    protected static JDialog confirmDialog;
    protected static JDialog saveProgressDialog;
    protected static JDialog[] cancelDialogArray = new JDialog[3];

    public WorkingPane() {
        initializeWindowProperties();

        initializeGUIComponents();

        workingFrame.setVisible(true);
        MainWindow.downloadButton.setEnabled(false); // disables so that the user cannot start another download while in progress
        MainWindow.setHandCursorToClickableComponents(this);
    }

    private void initializeGUIComponents() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        workingFrame.add(panel);
        {
            panel.add(Box.createVerticalStrut(5));

            label1 = new JLabel(" Preparing Download...");
            label1.setFont(new Font(MainWindow.fontName, Font.PLAIN, 18));
            label1.setAlignmentX(Component.LEFT_ALIGNMENT);
            label1.setHorizontalTextPosition(JLabel.LEFT);
            panel.add(label1);

            panel.add(Box.createVerticalStrut(10));

            labelMessage = new JLabel(" Please wait...");
            labelMessage.setFont(new Font(MainWindow.fontName, Font.PLAIN, 14));
            labelMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
            labelMessage.setHorizontalTextPosition(JLabel.LEFT);
            panel.add(labelMessage);

            panel.add(Box.createVerticalStrut(10));

            progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
            progressBar.setFont(new Font(MainWindow.fontName, Font.PLAIN, 14));
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
                cancelButton = new JButton("Cancel");
                cancelButton.setFont(new Font(MainWindow.fontName, Font.PLAIN, 14));
                panelRow2.add(cancelButton);

                cancelButton.addActionListener(e -> {
                    if (debug) System.out.println("Cancel button pressed.");
                    cancelDownload();
                });
            }
            panel.add(Box.createVerticalStrut(5));
        }
    }

    private void initializeWindowProperties() {
        workingFrame = new JFrame("Working...");

        workingFrame.setMinimumSize(new Dimension(355, 170));
        workingFrame.setPreferredSize(new Dimension(355, 170));
        workingFrame.setLocationRelativeTo(frame);
        workingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        workingFrame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeWorkingPane();
            }
        });

        workingFrame.setResizable(false);
    }

    private void cancelDownload() {
        JOptionPane confirmPane = new JOptionPane("Are you sure you want to cancel the download?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        confirmDialog = confirmPane.createDialog(workingFrame, "Cancel Download");

        JOptionPane cancelledPane = new JOptionPane("Download was cancelled.", JOptionPane.INFORMATION_MESSAGE);
        cancelledDialog = cancelledPane.createDialog(null, "Cancelled");

        JOptionPane saveProgressPane = new JOptionPane("Save download progress to resume later?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        saveProgressDialog = saveProgressPane.createDialog(null, "Save Progress");

        cancelDialogArray[0] = confirmDialog;
        cancelDialogArray[1] = cancelledDialog;
        cancelDialogArray[2] = saveProgressDialog;


        confirmDialog.setVisible(true);
        Object confirm = confirmPane.getValue();
        if (confirm != null) {
            int result = (int) confirm;
            if (result != JOptionPane.YES_OPTION) {
                return; // !=
            } else {
                if (debug) System.out.println("Cancel confirmed.");
                downloadCanceled = true;
                downloadStatus = "cancelledTemp";
            }
        } else {
            System.err.println("Confirm dialog returned null.");
            return;
        }

        for (String binaryFile : binaryFiles) {
            closeProcess(null, binaryFile);
        }

        cancelledDialog.setVisible(true);
        closeWorkingPane(true);

        if (debug) {
            System.out.println("Download was cancelled.");
            System.out.println("Download started: " + downloadStarted);
        }

        if (downloadStarted) {
            saveProgressDialog.setVisible(true);
            Object saveProgress = saveProgressPane.getValue();
            if (saveProgress != null) {
                int result = (int) saveProgress;
                if (result == JOptionPane.NO_OPTION) {
                    if (debug) System.out.println("Save progress denied. Deleting files.");
                    deleteRelatedFiles();
                    downloadStatus = validDownloadStatus[3];
                } else {
                    if (debug) System.out.println("Save progress confirmed.");
                    downloadStatus = validDownloadStatus[4];
                }
                if (!Objects.equals(downloadStatus, "cancelledTemp")) {
                    logDownloadHistory();
                }
            } else {
                System.err.println("Save progress dialog returned null.");
                // add return here if something else happens underneath
            }
        }
    }

    public void closeWorkingPane() {
        closeWorkingPane(false);
    }

    public void closeWorkingPane(boolean cancelValid) {
        if (debug) System.out.println("Closing working pane.");
        if (!downloadCanceled && cancelValid) {
            cancelDownload();
        }
        for (JDialog dialog : cancelDialogArray) {
            // if the dialog is not null -> continue
            if ( dialog != null ) {
                // the first two are checks to make sure that the save progress dialog is not disposed after the download is cancelled

                // if the download status is not cancelled and the dialog is the save progress dialog -> dispose
                if ( !Objects.equals(downloadStatus, "cancelledTemp") ) { //&& Objects.equals(dialog, saveProgressDialog)
                    dialog.dispose();
                // if download status is cancelled and the dialog is not the save progress dialog -> dispose
                } else if (Objects.equals(downloadStatus, "cancelledTemp") && !Objects.equals(dialog, saveProgressDialog) ) {
                    dialog.dispose();

                } else if (dialog != saveProgressDialog) {
                    System.err.println("[ERROR] Dialog not disposed: " + dialog.getTitle());
                }
            }
        }
        MainWindow.downloadButton.setEnabled(true);
        workingFrame.dispose();
    }

    public void setCTitle(String title) {
        label1.setText(title);
    }

    public void setMessage(String message) {
        labelMessage.setText(String.format("<html><body style='width: 300px'>%s</body></html>", message));
    }

    public void setProgress(int i) {
        if (i < 0 || i > 100) {
            if (progressBar.isIndeterminate()) return;
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
