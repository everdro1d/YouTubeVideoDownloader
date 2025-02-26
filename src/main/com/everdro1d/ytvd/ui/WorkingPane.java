package main.com.everdro1d.ytvd.ui;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static main.com.everdro1d.ytvd.core.MainWorker.*;
import static main.com.everdro1d.ytvd.ui.MainWindow.frame;

public class WorkingPane extends JFrame {
    public static JFrame workingFrame;
    protected static JPanel panel;
    private static JPanel panelRow2;
    protected static JLabel labelTitle;
        public static String workingFrameTitleText = "Working...";
    private String prepareDownloadTitleText = "Preparing Download...";
        public static String gettingDownloadInfoTitleText = "Getting Download Info...";
        public static String recodingVideoTitleText = "Recoding Video...";
        public static String recodingAudioTitleText = "Recoding Audio...";
        public static String mergingVideoTitleText = "Merging...";
        public static String finishingTitleText = "Finishing...";
    protected static JLabel labelMessage;
        private String pleaseWaitLabelText = "Please wait...";
        public static String gettingVideoInfoMessageText = "Getting video info...";
        public static String gettingVideoFilenameMessageText = "Getting file info...";
        public static String recodingInfoMessageText = "Recoding to";
        public static String recodingInfoNoteText = "Note: Recoding can take a while.";
        public static String mergingVideoMessageText = "Merging audio and video...";
        public static String finishingMessageText = "Finishing up...";
    public static JProgressBar progressBar;
    private static JButton cancelButton;
        private String cancelButtonText = "Cancel";
    private static JDialog confirmDialog;
        private final boolean showConfirmDialog;
        private String cancelDownloadConfirmDialogMessageText = "Are you sure you want to cancel the download?";
        private String cancelDownloadConfirmDialogTitleText = "Cancel Download";
    private static JDialog cancelledDialog;
        private String cancelledDownloadDialogNoticeMessageText = "Download was cancelled.";
        private String cancelledDownloadDialogNoticeTitleText = "Cancelled";
    private static JDialog saveProgressDialog;
        private String saveProgressDialogQuestionMessageText = "Save download progress to resume later?";
        private String saveProgressDialogQuestionTitleText = "Save Progress";
    private static JDialog[] cancelDialogArray = new JDialog[3];

    // dialog texts
    public static String downloadErrorDialogMessageText = "An error occurred while downloading the video:";
    public static String downloadErrorDialogTitleText = "An error occurred while downloading the video:";
    public static String videoAlreadyDownloadedDialogMessageText = "This video has already been downloaded.";
    public static String videoAlreadyDownloadedDialogTitleText = "Aborted!";
    public static String downloadCompletedDialogMessageText = "Download completed!";
    public static String downloadCompletedDialogTitleText = "Finished!";

    public WorkingPane() {
        this(true);
    }

    public WorkingPane(boolean showConfirmDialog) {
        this.showConfirmDialog = showConfirmDialog;

        // if the locale does not contain the class, add it and it's components
        if (!localeManager.getClassesInLocaleMap().contains("WorkingPane")) {
            addClassToLocale();
        }
        useLocale();

        initializeWindowProperties();

        initializeGUIComponents();

        workingFrame.setVisible(true);
        MainWindow.downloadButton.setEnabled(false); // disables so that the user cannot start another download while in progress
        SwingGUI.setHandCursorToClickableComponents(this);
    }

    private void addClassToLocale() {
        Map<String, Map<String, String>> classMap = new TreeMap<>();
            classMap.put("Main", new TreeMap<>());
            Map<String, String> mainMap = classMap.get("Main");
                mainMap.put("pleaseWaitLabelText", pleaseWaitLabelText);
                mainMap.put("cancelButtonText", cancelButtonText);
                mainMap.put("cancelDownloadConfirmDialogMessageText", cancelDownloadConfirmDialogMessageText);
                mainMap.put("cancelDownloadConfirmDialogTitleText", cancelDownloadConfirmDialogTitleText);
                mainMap.put("cancelledDownloadDialogNoticeMessageText", cancelledDownloadDialogNoticeMessageText);
                mainMap.put("cancelledDownloadDialogNoticeTitleText", cancelledDownloadDialogNoticeTitleText);
                mainMap.put("saveProgressDialogQuestionMessageText", saveProgressDialogQuestionMessageText);
                mainMap.put("saveProgressDialogQuestionTitleText", saveProgressDialogQuestionTitleText);

            classMap.put("TitleLabel", new TreeMap<>());
            Map<String, String> titleLabelMap = classMap.get("TitleLabel");
                titleLabelMap.put("workingFrameTitleText", workingFrameTitleText);
                titleLabelMap.put("prepareDownloadTitleText", prepareDownloadTitleText);
                titleLabelMap.put("gettingDownloadInfoTitleText", gettingDownloadInfoTitleText);
                titleLabelMap.put("recodingVideoTitleText", recodingVideoTitleText);
                titleLabelMap.put("recodingAudioTitleText", recodingAudioTitleText);
                titleLabelMap.put("mergingVideoTitleText", mergingVideoTitleText);
                titleLabelMap.put("finishingTitleText", finishingTitleText);

            classMap.put("MessageLabel", new TreeMap<>());
            Map<String, String> messageLabelMap = classMap.get("MessageLabel");
                messageLabelMap.put("pleaseWaitLabelText", pleaseWaitLabelText);
                messageLabelMap.put("gettingVideoInfoMessageText", gettingVideoInfoMessageText);
                messageLabelMap.put("gettingVideoFilenameMessageText", gettingVideoFilenameMessageText);
                messageLabelMap.put("recodingInfoMessageText", recodingInfoMessageText);
                messageLabelMap.put("recodingInfoNoteText", recodingInfoNoteText);
                messageLabelMap.put("mergingVideoMessageText", mergingVideoMessageText);
                messageLabelMap.put("finishingMessageText", finishingMessageText);

            classMap.put("StatusDialogs", new TreeMap<>());
            Map<String, String> statusDialogsMap = classMap.get("StatusDialogs");
                statusDialogsMap.put("downloadErrorDialogMessageText", downloadErrorDialogMessageText);
                statusDialogsMap.put("downloadErrorDialogTitleText", downloadErrorDialogTitleText);
                statusDialogsMap.put("videoAlreadyDownloadedDialogMessageText", videoAlreadyDownloadedDialogMessageText);
                statusDialogsMap.put("videoAlreadyDownloadedDialogTitleText", videoAlreadyDownloadedDialogTitleText);
                statusDialogsMap.put("downloadCompletedDialogMessageText", downloadCompletedDialogMessageText);
                statusDialogsMap.put("downloadCompletedDialogTitleText", downloadCompletedDialogTitleText);

        localeManager.addClassSpecificMap("WorkingPane", classMap);
    }

    private void useLocale() {
        Map<String, Map<String, String>> classMap = localeManager.getClassSpecificMap("WorkingPane");
        Map<String, String> mainMap = classMap.get("Main");
            workingFrameTitleText = mainMap.getOrDefault("workingFrameTitleText", workingFrameTitleText);
            prepareDownloadTitleText = mainMap.getOrDefault("prepareDownloadLabelText", prepareDownloadTitleText);
            pleaseWaitLabelText = mainMap.getOrDefault("pleaseWaitLabelText", pleaseWaitLabelText);
            cancelButtonText = mainMap.getOrDefault("cancelButtonText", cancelButtonText);
            cancelDownloadConfirmDialogMessageText = mainMap.getOrDefault("cancelDownloadConfirmDialogMessageText", cancelDownloadConfirmDialogMessageText);
            cancelDownloadConfirmDialogTitleText = mainMap.getOrDefault("cancelDownloadConfirmDialogTitleText", cancelDownloadConfirmDialogTitleText);
            cancelledDownloadDialogNoticeMessageText = mainMap.getOrDefault("cancelledDownloadDialogNoticeMessageText", cancelledDownloadDialogNoticeMessageText);
            cancelledDownloadDialogNoticeTitleText = mainMap.getOrDefault("cancelledDownloadDialogNoticeTitleText", cancelledDownloadDialogNoticeTitleText);
            saveProgressDialogQuestionMessageText = mainMap.getOrDefault("saveProgressDialogQuestionMessageText", saveProgressDialogQuestionMessageText);
            saveProgressDialogQuestionTitleText = mainMap.getOrDefault("saveProgressDialogQuestionTitleText", saveProgressDialogQuestionTitleText);

        Map<String, String> titleLabelMap = classMap.get("TitleLabel");
            workingFrameTitleText = titleLabelMap.getOrDefault("workingFrameTitleText", workingFrameTitleText);
            prepareDownloadTitleText = titleLabelMap.getOrDefault("prepareDownloadLabelText", prepareDownloadTitleText);
            gettingDownloadInfoTitleText = titleLabelMap.getOrDefault("gettingDownloadInfoTitleText", gettingDownloadInfoTitleText);
            recodingVideoTitleText = titleLabelMap.getOrDefault("recodingVideoTitleText", recodingVideoTitleText);
            recodingAudioTitleText = titleLabelMap.getOrDefault("recodingAudioTitleText", recodingAudioTitleText);
            mergingVideoTitleText = titleLabelMap.getOrDefault("mergingVideoTitleText", mergingVideoTitleText);
            finishingTitleText = titleLabelMap.getOrDefault("finishingTitleText", finishingTitleText);

        Map<String, String> messageLabelMap = classMap.get("MessageLabel");
            pleaseWaitLabelText = messageLabelMap.getOrDefault("pleaseWaitLabelText", pleaseWaitLabelText);
            gettingVideoInfoMessageText = messageLabelMap.getOrDefault("gettingVideoInfoMessageText", gettingVideoInfoMessageText);
            gettingVideoFilenameMessageText = messageLabelMap.getOrDefault("gettingVideoFilenameMessageText", gettingVideoFilenameMessageText);
            recodingInfoMessageText = messageLabelMap.getOrDefault("recodingInfoMessageText", recodingInfoMessageText);
            recodingInfoNoteText = messageLabelMap.getOrDefault("recodingInfoNoteText", recodingInfoNoteText);
            mergingVideoMessageText = messageLabelMap.getOrDefault("mergingVideoMessageText", mergingVideoMessageText);
            finishingMessageText = messageLabelMap.getOrDefault("finishingMessageText", finishingMessageText);

        Map<String, String> statusDialogsMap = classMap.get("StatusDialogs");
            downloadErrorDialogMessageText = statusDialogsMap.getOrDefault("downloadErrorDialogMessageText", downloadErrorDialogMessageText);
            downloadErrorDialogTitleText = statusDialogsMap.getOrDefault("downloadErrorDialogTitleText", downloadErrorDialogTitleText);
            videoAlreadyDownloadedDialogMessageText = statusDialogsMap.getOrDefault("videoAlreadyDownloadedDialogMessageText", videoAlreadyDownloadedDialogMessageText);
            videoAlreadyDownloadedDialogTitleText = statusDialogsMap.getOrDefault("videoAlreadyDownloadedDialogTitleText", videoAlreadyDownloadedDialogTitleText);
            downloadCompletedDialogMessageText = statusDialogsMap.getOrDefault("downloadCompletedDialogMessageText", downloadCompletedDialogMessageText);
            downloadCompletedDialogTitleText = statusDialogsMap.getOrDefault("downloadCompletedDialogTitleText", downloadCompletedDialogTitleText);
    }

    private void initializeWindowProperties() {
        workingFrame = new JFrame(workingFrameTitleText);

        workingFrame.setMinimumSize(new Dimension(355, 170));
        workingFrame.setPreferredSize(new Dimension(355, 170));
        workingFrame.setLocationRelativeTo(frame);
        workingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        workingFrame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                closeWorkingPane();
            }
        });

        workingFrame.setResizable(false);
    }

    private void initializeGUIComponents() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        workingFrame.add(panel);
        {
            panel.add(Box.createVerticalStrut(5));

            labelTitle = new JLabel(" " + prepareDownloadTitleText);
            labelTitle.setFont(new Font(MainWindow.fontName, Font.PLAIN, 18));
            labelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            labelTitle.setHorizontalTextPosition(JLabel.LEFT);
            panel.add(labelTitle);

            panel.add(Box.createVerticalStrut(10));

            labelMessage = new JLabel(" " + pleaseWaitLabelText);
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
                cancelButton = new JButton(cancelButtonText);
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

    private void cancelDownload() {
        if (!showConfirmDialog) { //TODO fails to stop retrieval process after cancel
            for (String binaryFile : binaryFiles) {
                closeProcess(null, binaryFile);
            }
            closeWorkingPane(false);
            return;
        }

        JOptionPane confirmPane = new JOptionPane(cancelDownloadConfirmDialogMessageText, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        confirmDialog = confirmPane.createDialog(workingFrame, cancelDownloadConfirmDialogTitleText);

        JOptionPane cancelledPane = new JOptionPane(cancelledDownloadDialogNoticeMessageText, JOptionPane.INFORMATION_MESSAGE);
        cancelledDialog = cancelledPane.createDialog(frame, cancelledDownloadDialogNoticeTitleText);

        JOptionPane saveProgressPane = new JOptionPane(saveProgressDialogQuestionMessageText, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        saveProgressDialog = saveProgressPane.createDialog(frame, saveProgressDialogQuestionTitleText);

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

    public void setTempTitle(String title) {
        labelTitle.setText(title);
    }

    public void setMessage(String message) {
        labelMessage.setText(String.format("<html><body style='width: 300px'>%s</body></html>", message));
    }
}
