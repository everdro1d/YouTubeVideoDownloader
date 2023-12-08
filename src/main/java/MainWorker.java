/*
 */

package main.java;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;
import java.util.prefs.Preferences;

import static main.java.AdvancedSettings.*;
import static main.java.HistoryLogger.colUrl;
import static main.java.MainWindow.*;
import static main.java.WorkingPane.workingFrame;

public class MainWorker {
    //public static final String version = "1.2.0"; // the version of the program TODO create update checking
    protected static MainWindow window;
    private static Process globalDefaultProcess;
    public static String rawURL = ""; // raw URL String from the text field
    public static String videoID; // the video ID from the URL
    protected static String downloadBinary = ""; // the name of the binary to run
    protected static int downloadCount = 0;
    protected static int downloadMax = 1;
    public static String downloadStatus = "";

    /**
     * The valid download statuses are:
     * <ul>
     *     <li>[0] Completed - Success</li>
     *     <li>[1] Stopped - Fatal Error</li>
     *     <li>[2] Stopped - Already Exists</li>
     *     <li>[3] Canceled - Deleted Files</li>
     *     <li>[4] Canceled - Saved Files</li>
     * </ul>
     */
    public static String[] validDownloadStatus = new String[]{
            "Completed - Success", "Stopped - Fatal Error", "Stopped - Already Exists",
            "Canceled - Deleted Files", "Canceled - Saved Files"
    };
    protected static boolean downloadStarted = false;
    protected static String[] binaryFiles = {"yt-dlp", "ffmpeg", "ffprobe"};
    protected static String binaryPath = "main/libs/"; // the path to the binary to run
    protected static String downloadDirectoryPath = ""; // the path to download the video to
    public static boolean debug = false; // whether debug mode is enabled
    protected static boolean darkMode = false; // whether dark mode is enabled
    protected static boolean compatibilityMode = false; // if the compatability mode is enabled
    protected static boolean logHistory = true; // whether to log the download history
    static Preferences prefs = Preferences.userNodeForPackage(MainWorker.class);
    protected static String videoTitle = "";
    protected static String videoFileName = "";
    private static WorkingPane workingPane;
    protected static boolean downloadCanceled = false;
    private static boolean windows = false;
    private static boolean macOS = false;


    public static void main(String[] args) {
        checkCommandLineArgs(args);
        checkOSCompatability();

        // binary temp file operations
        copyBinaryTempFiles();

        // set look and feel
        FlatLightLaf.setup();
        FlatDarkLaf.setup();

        // load preferences
        prefs();

        // set UI elements
        lightDarkMode();
        uiManager();

        // start main window
        EventQueue.invokeLater(() -> {
            try {
                window = new MainWindow();
                window.coloringModeChange();
            } catch (Exception ex) {
                if (debug) ex.printStackTrace(System.err);
                System.err.println("Failed to start main window.");
            }
        });

        // check for updates on launch ( disabled in debug mode )
        if (!debug) {
            checkUpdate();
        } else {
            System.out.println("Debug enabled, update check skipped.");
        }
    }

    private static void checkCommandLineArgs(String[] args) {
        if (args.length > 0) {
            String arg = args[0];
            if (arg.equals("debug")) {
                System.out.println("Debug mode enabled.");
                debug = true;
            } else {
                System.err.println(
                        "Unknown argument: " + arg +
                                "\nValid arguments: debug" +
                                "\nContinuing without arguments."
                );
            }
        }
    }

    private static void checkOSCompatability() {
        String osName = System.getProperty("os.name").toLowerCase();
        windows = osName.contains("win");
        macOS = osName.contains("mac");
        if (!windows && !macOS) {
            System.err.println("This program is not compatible with your operating system.");
            JOptionPane.showMessageDialog(null, "This program is not compatible with your operating system.", "Error!", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        if (windows) {
            binaryPath += "win/";
            for (int i = 0; i < 3; i++) {
                binaryFiles[i] += ".exe";
            }
        } else {
            binaryPath += "mac/";
            binaryFiles[0] = "yt-dlp_macos";
        }
    }

    private static void copyBinaryTempFiles() {
        if (debug) System.out.println("Copying binary files to temp directory.");
        downloadBinary = binaryFiles[0];

        for (String binaryFile : binaryFiles) {
            if (debug) System.out.println("Copying binary file: " + binaryFile);

            try (InputStream binaryPathStream = MainWorker.class.getClassLoader().getResourceAsStream(binaryPath + binaryFile)) {
                if (binaryPathStream == null) {
                    System.err.println("Could not find binary file: " + binaryFile);
                    continue;
                }
                Path outputPath = new File(binaryFile).toPath();

                Files.copy(binaryPathStream, outputPath, StandardCopyOption.REPLACE_EXISTING);

                // set binary file to hidden on windows
                if (windows) Files.setAttribute(outputPath, "dos:hidden", true);
                // set binary file to hidden on macOS
                if (macOS) Runtime.getRuntime().exec("chflags hidden \\" + outputPath + binaryFile);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteBinaryTempFiles(binaryFile)));

            } catch (Exception e) {
                if (debug) e.printStackTrace(System.err);
            }
        }
    }

    private static void deleteBinaryTempFiles(String binaryFile) {
        if (debug) System.out.println("Deleting binary file: " + binaryFile);

        File fileToDelete = new File(binaryFile);
        if (fileToDelete.exists()) {
            int iterations = 0;
            while (!fileToDelete.delete() && iterations++ < 5) {
                System.err.println("Failed to delete temp file: " + binaryFile + "\nRetrying...");
                try {
                    closeProcess(null, binaryFile);

                    //Thread.sleep(200);

                    if (fileToDelete.delete()) {
                        System.err.println("Deleted temp file: " + binaryFile);
                    } else {
                        System.err.println("Still failed to delete temp file: " + binaryFile);
                    }
                } catch (Exception e) {
                    if (debug) e.printStackTrace(System.err);
                }
            }
        }
    }

    private static void prefs() {
        // load preferences
        darkMode = prefs.getBoolean("darkMode", false);
        downloadDirectoryPath = prefs.get("filePath", (System.getProperty("user.home") + "\\Downloads"));
        compatibilityMode = prefs.getBoolean("compatibilityMode", false);
        logHistory = prefs.getBoolean("logHistory", true);

        // save preferences on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            prefs.put("filePath", downloadDirectoryPath);
            prefs.putBoolean("darkMode", darkMode);
            prefs.putBoolean("compatibilityMode", compatibilityMode);
            prefs.putBoolean("logHistory", logHistory);
        }));
    }

    private static void uiManager() {
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Separator.stripeWidth", 10);
        UIManager.put("RootPane.background", new Color(darkMode ? 0x2B2B2B : 0xe1e1e1));
        UIManager.put("RootPane.foreground", new Color(darkMode ? 0xbbbbbb : 0x000000));

        UIManager.put("OptionPane.minimumSize",new Dimension(300, 100));
        UIManager.put("OptionPane.messageFont", new Font(fontName, Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font(fontName, Font.PLAIN, 16));

        UIManager.put("FileChooser.noPlacesBar", Boolean.TRUE);
    }

    protected static void lightDarkMode() {
        if (darkMode) {
            try {
                UIManager.setLookAndFeel( new FlatDarkLaf() );
                UIManager.put("RootPane.background", new Color(0x2B2B2B));
                UIManager.put("RootPane.foreground", new Color(0xbbbbbb));

                if (frame != null) { // because for some reason the title bar color doesn't change with the L&F
                    frame.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(0x2B2B2B));
                    frame.getRootPane().putClientProperty("JRootPane.titleBarForeground", new Color(0xbbbbbb));
                }
                if (workingFrame != null) {
                    workingFrame.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(0x2B2B2B));
                    workingFrame.getRootPane().putClientProperty("JRootPane.titleBarForeground", new Color(0xbbbbbb));
                }
            } catch (Exception ex) {
                System.err.println("Could not set look and feel of application.");
            }
        } else {
            try {
                UIManager.setLookAndFeel( new FlatLightLaf() );
                UIManager.put("RootPane.background", new Color(0xe1e1e1));
                UIManager.put("RootPane.foreground", new Color(0x000000));

                if (frame != null) { // because for some reason the title bar color doesn't change with the L&F
                    frame.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(0xe1e1e1));
                    frame.getRootPane().putClientProperty("JRootPane.titleBarForeground", Color.BLACK);
                }
                if (workingFrame != null) {
                    workingFrame.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(0xe1e1e1));
                    workingFrame.getRootPane().putClientProperty("JRootPane.titleBarForeground", Color.BLACK);
                }
            } catch (Exception ex) {
                System.err.println("Could not set look and feel of application.");
            }
        }

    }

    public static void checkUpdate() {
        new Thread(MainWorker::updateProcess).start();
    }

    private static void updateProcess() {
        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(downloadBinary, "-U"));
        Process p;
        try {
            p = pb.start();
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
            p.waitFor();
            if (debug) System.out.println(p.exitValue());

        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
    }

    public static boolean containsAny(String[] matchingArray, String testString) {
        for (String s : matchingArray) {
            if (testString.contains(s)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean validURL(String url) {
        if (url.isEmpty()) {
            return false;
        }
        if (url.length() < 11) { // too short to be a valid URL with/or a video ID
            return false;
        } else if (url.length() == 11) { // if URL contains only the video ID
            videoID = url;
        } else { // if the URL is longer than 11 characters

            // check if the URL is valid
            String[] validURLs = {
                    "www.youtube.com/watch?v=",
                    "youtu.be/",
                    "www.youtube.com/channel/",

                    "www.instagram.com/p/",
                    "www.instagram.com/reel/"
            };

            if (containsAny(validURLs, rawURL)) {
                if (!validatedURL(url, validURLs)) {
                    return false;
                }
            } else {
                return false;
            }

            //check if the url is valid
            try { new java.net.URI(url); } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private static boolean validatedURL(String url, String[] validURLs) {
        boolean valid = false;
        // check if the base URL is valid
        for (String validURL : validURLs) {
            if (url.contains(validURL)) {
                valid = true;
                break;
            }
        }

        String[] splitURL = url.split("[/=?&]");

        // check if the video ID is valid
        for (String s : splitURL) {
            if (s.length() == 11) {
                videoID = s;
                if (debug) System.out.println("Video ID: " + videoID);
                valid = true;
                break;
            } else{
                valid = false;
            }
        }

        if (videoID.isEmpty()) {
            return false;
        }

        return valid;
    }

    private static boolean checkURLDialog(boolean show) {
        if ((rawURL == null) || !validURL(rawURL) || show) {
                JOptionPane.showMessageDialog(null, "Please check the link or enter a valid URL.", "Error! Media not found.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void downloadButtonClicked() {
        downloadStarted = false;
        downloadCount = 0;
        // start download
        if ( !checkURLDialog(false) ) { return; }

        if (downloadDirectoryPath.isEmpty()) {
            downloadDirectoryPath = openFileChooser();
        }

        downloadCanceled = false;
        workingPane = new WorkingPane();
        workingPane.setCTitle(" Getting Download Info...");

        // thread for working pane (won't draw otherwise)
        new Thread(() -> {
            // get video info
            workingPane.setMessage(" Getting video info...");
            getVideoTitle();
            workingPane.setMessage(" Getting file info...");
            getVideoFileName();
            if (debug) System.out.println("Video Title: " + videoTitle);
            if (debug) System.out.println("Video Filename: " + videoFileName);
            workingPane.closeWorkingPane();

            if (!downloadCanceled) {
                // get download command
                String cmd = getCommand();
                if (debug) System.out.println(cmd);

                // start download
                download(cmd);
            } else {
                downloadCanceled = false;
                if (videoTitle.isEmpty()) {
                    videoTitle = "Canceled before title was retrieved";
                }
                downloadStatus = validDownloadStatus[3];
                logDownloadHistory();
            }
        }).start();
    }

    public static void getVideoTitle() {
        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(
                downloadBinary, "--get-title", "-o", "%(title)s", rawURL
        ));
        Process p;
        try {
            p = pb.start();
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            Scanner scanner = new Scanner(p.getInputStream());
            if (scanner.hasNextLine()) videoTitle = scanner.nextLine();
            p.waitFor();
            if (debug) System.out.println(p.exitValue());
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
    }

    private static void getVideoFileName() {
        String fileName = "";
        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(
                downloadBinary, "--restrict-filenames", "--get-filename", "-o \\%(title)s.%(ext)s\" ", rawURL
        ));
        Process p;
        try {
            p = pb.start();
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            Scanner scanner = new Scanner(p.getInputStream());
            if (scanner.hasNextLine()) fileName = scanner.nextLine();
            p.waitFor();
            if (debug) System.out.println(p.exitValue());
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }

        if (!fileName.isEmpty()) videoFileName = fileName.split("[\\\\.]")[1];
    }

    public static String getCommand() {
        // the options to pass to the binary
        String advancedSettings = getAdvancedSettings();

        return downloadBinary + " " + advancedSettings + "-o \"" + downloadDirectoryPath + "\\%(title)s.%(ext)s\" " + "\"" + rawURL + "\"";
    }

    public static void download(String cmd) {
        if (cmd.isEmpty()) {
            System.err.println("Download command is empty.");
            return;
        }
        // start download
        new Thread(() -> downloadProcess(cmd.split(" "))).start();
    }

    private static void downloadProcess(String[] cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p;
        try {
            try {
                p = pb.start();
                globalDefaultProcess = p;
                new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                try (Scanner scanner = new Scanner(p.getInputStream())) {
                    downloadProgressPanes(scanner, p);
                }
                p.waitFor();
                if (debug) System.out.println(p.exitValue());
            } catch (Exception e) {
                if (debug) e.printStackTrace(System.err);
            }
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
    }

    private static void downloadProgressPanes(Scanner scanner, Process p) {
        workingPane = new WorkingPane();

        boolean doDownload = true;
        boolean downloadComplete = false;
        boolean downloadChecked = false;
        downloadStatus = "oops";

        int delCount = 0;
        int delMax = (videoAudio == 0) ? 2 : 0;
        downloadMax = (videoAudio == 0) ? 2 : 1;
        if (recode) { // account for recoding operations
            delMax += 1;
        }
        if (writeThumbnail|| !advancedSettingsEnabled) {
            downloadMax += 1;
            if (embedThumbnail|| !advancedSettingsEnabled) {
                delMax += 1;
            }
        }


        if (!scanner.hasNextLine()) {
            workingPane.closeWorkingPane();
            System.err.println("[ERROR] No output from process.");
            for (String binaryFile : binaryFiles) {
                closeProcess(p, binaryFile);
            }
            doDownload = false;
        }

        if (doDownload) {
            while (scanner.hasNextLine()) {
                //Skip lines until after the download begins
                String s = scanner.nextLine();
                if (debug) System.out.println(s);
                if (s.contains("[info]") && s.contains("Downloading")) {
                    break;
                }
            }

            //OptionPanes to show the progress of the download
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();

                if (debug) System.out.println(s);

                if (s.contains("[download]")) {
                    setWorkingPaneMessage(workingPane, s);
                    downloadStarted = true;

                    if (s.contains("Destination:")) {
                        downloadComplete = false;
                    }
                }

                if (!downloadChecked) {
                    if (s.contains("Deleting")) {
                        delCount++;
                    }

                    if ( ( s.contains("[download] 100% of") && !s.contains("ETA") ) // video
                            || ( s.contains("Downloading") && s.contains("thumbnail") ) ) { // thumbnail
                        downloadComplete = true;
                        downloadCount = Math.min(downloadCount + 1, downloadMax);
                    }

                    if (debug) System.out.println("Downloads: " + downloadCount + " / " + downloadMax +
                                "\nDeletes: " + delCount + " / " + delMax);

                    if (( downloadComplete && (downloadCount == downloadMax) )
                            && !s.contains("ERROR:")
                            && !s.contains("has already been downloaded") ) {
                        String message =
                                "<tab>Recoding to " + ((arrayRecodeExt.length >= recodeExt) ?
                                        arrayRecodeExt[recodeExt] :
                                        "[FORMAT ERROR]") +
                                        "..." +
                                        "<p><tab>Note: Recoding can take a while.";
                        if (videoAudio == 0) {
                            if (recode) {
                                workingPane.setTitle("Recoding Video...");
                                workingPane.setMessage(String.format("<html><body style='width: 300px'>%s</body></html>", message));
                                workingPane.setProgress(-1);
                            } else {
                                workingPane.setTitle("Merging...");
                                workingPane.setMessage(" Merging audio and video...");
                                workingPane.setProgress(-1);
                            }
                        } else {
                            if (recode) {
                                switch (videoAudio) {
                                    case 1:
                                        workingPane.setTitle("Recoding Video...");
                                        break;
                                    case 2:
                                        workingPane.setTitle("Recoding Audio...");
                                        break;
                                }
                                workingPane.setMessage(String.format("<html><body style='width: 300px'>%s</body></html>", message));
                                workingPane.setProgress(-1);
                            } else {
                                workingPane.setTitle("Finishing...");
                                workingPane.setMessage(" Finishing up...");
                                workingPane.setProgress(-1);
                            }
                        }

                        // if the download is complete, but the process is still running, wait for it to finish
                        if (delCount == delMax) {
                            downloadChecked = downloadComplete(workingPane);
                        }

                    } else if (s.contains("ERROR:")) {
                        System.err.println("An error occurred while downloading the video.:\n" + s);
                        workingPane.setVisible(false);
                        workingPane.closeWorkingPane();
                        JOptionPane.showMessageDialog(null, "An error occurred while downloading the video.:\n" + s, "Error!", JOptionPane.ERROR_MESSAGE);
                        for (String binaryFile : binaryFiles) {
                            closeProcess(p, binaryFile);
                        }
                        downloadChecked = true;
                        downloadStatus = validDownloadStatus[1];
                    } else if (s.contains("has already been downloaded")) {
                        if (debug) System.out.println("This video has already been downloaded.");
                        workingPane.setVisible(false);
                        workingPane.closeWorkingPane();
                        JOptionPane.showMessageDialog(null, "This video has already been downloaded.", "Error!", JOptionPane.ERROR_MESSAGE);
                        for (String binaryFile : binaryFiles) {
                            closeProcess(p, binaryFile);
                        }
                        downloadChecked = true;
                        downloadStatus = validDownloadStatus[2];
                    }
                }
            }
        }

        if (debug) System.out.println(scanner.hasNextLine() ? scanner.nextLine() : "No more output from process.");
        if (workingPane != null) workingPane.closeWorkingPane();
        scanner.close();

        logDownloadHistory();
    }

    protected static void logDownloadHistory() {
        System.out.println("Logging download history.");
        if (logHistory) {
            // check if the download failed somehow
            if ( !containsAny(validDownloadStatus, downloadStatus) ) {
                // if the download was canceled by the program (error from process, likely by an invalid url),
                // show the dialog and skip logging download history
                if (debug) System.out.println(
                        "Error from process. Skipped logging download history. Showing Dialog." + downloadStatus);
                checkURLDialog(true);
                return;
            }

            // log download history
            HistoryLogger historyLogger = new HistoryLogger();
            String[] data = { videoTitle, rawURL, downloadStatus, getVideoAudioStatus(), getCurrentTime() };
            historyLogger.logHistory(data);
            if (debug) System.out.println("Logged download history: \n" + Arrays.toString(data));

        } else if (debug) {
            System.out.println("Logging download history is disabled. Skipping.");
        }
    }

    private static boolean downloadComplete(WorkingPane workingPane) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            if (debug) e.printStackTrace(System.err);
        }
        if (debug) System.out.println("Finished.");
        workingPane.setVisible(false);
        workingPane.closeWorkingPane();
        JOptionPane.showMessageDialog(null, "Download Completed", "Finished!", JOptionPane.INFORMATION_MESSAGE);
        boolean downloadChecked = true;
        downloadStatus = validDownloadStatus[0];

        return downloadChecked;
    }

    private static String getVideoAudioStatus() {
        switch (videoAudio) {
            case 0:
                return "Video + Audio";
            case 1:
                return "Video Only";
            case 2:
                return "Audio Only";
            default:
                return "Unknown Type";
        }
    }

    private static String getCurrentTime() {
        return java.time.LocalDateTime.now()
                .toString()
                .replace("T", " ")
                .replace("Z", "")
                .split("\\.")[0];
        // 2021-08-01T18:00:00.000Z
        // 2021-08-01 18:00:00
    }

    private static void setWorkingPaneMessage(WorkingPane workingPane, String s) {
        int dC = Math.max(1, Math.min(downloadCount, downloadMax));

        String[] split = s.split(" ");
        String[] messageLine1 = Arrays.copyOfRange(split, 1, split.length-2);
        String[] messageLine2 = Arrays.copyOfRange(split, split.length-2, split.length);

        String joinedMessage1 = String.join(" ", messageLine1);
        String joinedMessage2 = String.join(" ", messageLine2);
        String message = "<tab>" + joinedMessage1 + "<br><tab>" + joinedMessage2;

        workingPane.setMessage(message);
        workingPane.setCTitle(" Working...   (" + (dC) + "/" + downloadMax + ")");

        if (joinedMessage1.contains("%")) {
            String progress = joinedMessage1.split("%")[0].replace(".", "").replace(" ", "");
            if (!progress.isEmpty()) {
                int progressInt = Math.round(Float.parseFloat(progress) / 10);
                workingPane.setProgress(progressInt);
            }
        }
    }

    protected static String openFileChooser() {
        String output = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setVisible(true);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            output = fileChooser.getSelectedFile().getAbsolutePath();
            JOptionPane.showMessageDialog(frame, "Download location set to: " + output, "Download Location", JOptionPane.INFORMATION_MESSAGE);
        }

        return output;
    }

    protected static void closeProcess(Process p, String binaryFile) {
        if (p == null) {
            p = globalDefaultProcess;
        }
        try {
            if (windows) Runtime.getRuntime().exec("taskkill /F /IM " + binaryFile);
            if (macOS) Runtime.getRuntime().exec("killall -SIGINT " + binaryFile);
            System.out.println("Attempted to close task: " + binaryFile);
            if (p != null && p.isAlive()) {
                p.destroy();
            }
        } catch (IOException e) {
            if (debug) e.printStackTrace(System.err);
        }
    }

    protected static Icon getIcon(String pathFromSrc) {
        Icon icon = null;
        try (InputStream iconStream = MainWorker.class.getClassLoader().getResourceAsStream(pathFromSrc)) {
            if (iconStream != null) {
                icon = new ImageIcon(ImageIO.read(iconStream));
            }
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
        if (icon == null) {
            System.err.println("[ERROR] Could not find icon file at: " + pathFromSrc);
        }
        return icon;
    }

    protected static void openLink(HistoryWindow historyWindow, JTable historyTable, int selectedRow) {
        if (selectedRow == -1) {
            // show an error dialog if no row is selected
            JOptionPane.showMessageDialog(historyWindow,
                    "No row selected. Please select a row and try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        } else {
            // get the string in the first column of the selected row
            String url = (String) historyTable.getValueAt(selectedRow, colUrl);
            try {
                if (debug) System.out.println("Opening link: " + url);
                Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception e) {
                if (debug) e.printStackTrace(System.err);
            }
        }
    }

    protected static void insertURL(HistoryWindow historyWindow, JTable historyTable, int selectedRow) {
        if (selectedRow == -1) {
            // show an error dialog if no row is selected
            JOptionPane.showMessageDialog(historyWindow,
                    "No row selected. Please select a row and try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        } else {
            // get the string in the first column of the selected row
            String url = (String) historyTable.getValueAt(selectedRow, colUrl);
            try {
                if (debug) System.out.println("Inserting link: " + url);
                textField_URL.setText(url);
                textField_URL.requestFocus();
                simulateKeyEvent(textField_URL, KeyEvent.VK_ENTER, '\n', 0, KeyEvent.KEY_RELEASED);
                textField_URL.selectAll();

                historyWindow.dispose();
            } catch (Exception e) {
                if (debug) e.printStackTrace(System.err);
            }
        }

    }

    protected static void copyRowToClipboard(HistoryWindow historyWindow, JTable historyTable, int selectedRow, int i) {
        if (selectedRow == -1) {
            // show an error dialog if no row is selected
            JOptionPane.showMessageDialog(historyWindow,
                    "No row selected. Please select a row and try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        } else {
            // get the string in the i-th column of the selected row
            StringBuilder copyString;
            if (i == -1) {
                // add all columns to the clipboard
                copyString = new StringBuilder();
                for (int j = 0; j < historyTable.getColumnCount(); j++) {
                    copyString.append(historyTable.getValueAt(selectedRow, j)).append("\t");
                }
            } else {
                copyString = new StringBuilder((String) historyTable.getValueAt(selectedRow, i));
            }

            try {
                if (debug) System.out.println("Copying to clipboard: " + copyString);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(copyString.toString()), null);
            } catch (Exception e) {
                if (debug) e.printStackTrace(System.err);
            }
        }
    }

    /** Simulates an ENTER keyReleased event on a component.
     * @param component the component to simulate the event on
     */
    protected static void simulateKeyEvent(JComponent component) {
        simulateKeyEvent(component, KeyEvent.VK_ENTER, '\n', 0, KeyEvent.KEY_RELEASED);
    }

    /**
     * Simulates a keyEvent event on a component.
     * @param component the component to simulate the event on
     * @param keyCode ex: KeyEvent.VK_ENTER for ENTER
     * @param keyChar ex: '\n' for ENTER
     * @param modifiers KeyEvent.CTRL_MASK, KeyEvent.SHIFT_MASK, KeyEvent.ALT_MASK, KeyEvent.META_MASK
     * @param event KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, or KeyEvent.KEY_TYPED
     */
    @SuppressWarnings("SameParameterValue")
    protected static void simulateKeyEvent(JComponent component, int keyCode, char keyChar, int modifiers, int event) {
        KeyEvent keyEvent = new KeyEvent(component, event, System.currentTimeMillis(), modifiers, keyCode, keyChar);
        component.dispatchEvent(keyEvent);
    }

    protected static void deleteRelatedFiles() {
        // an array of all files with .part extension that match the video file name
        String[] fileList = new File(downloadDirectoryPath).list((dir, name) ->
                name.contains(videoFileName)
        );
        if (fileList == null) {
            if (debug) System.out.println("No files to delete.");
            return;
        } else {
            if (debug) System.out.println("Files to delete: " + Arrays.toString(fileList));
        }

        // delete all matching files
        for (String file : fileList) {
            Path pathToDelete = Paths.get(downloadDirectoryPath + "\\" + file);
            for (int tries = 0; tries < 5; tries++) {
                try {
                    Files.delete(pathToDelete);
                    if (debug) System.out.println("Deleted file: " + pathToDelete.getFileName());
                    break;
                } catch (IOException e1) {
                    System.err.println("[ERROR] Failed to delete file: " + pathToDelete.getFileName());
                    if (debug) e1.printStackTrace(System.err);
                }
            }
        }
    }
}
