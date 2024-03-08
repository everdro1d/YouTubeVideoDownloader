/*  TODO #1 - see method validURL()
    - rework url validation;
        - take the rawURL and check a list of validExtractors to see if the list contains the urlHeader of the raw url (ex. "YouTube.com") then get the extractorID associated with it or return invalid if no match.
        - use <extractorID>.getValue("IDLength") then check how long the rawURL.id is and check matching.
        - maybe try to make a valid connection when urlHeader and extractorID are both valid and check for connection.
        - when only ID is rawURL, then default to YouTube
 */

package main.com.everdro1d.ytvd.core;

import com.everdro1d.libs.commands.CommandInterface;
import com.everdro1d.libs.commands.CommandManager;
import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.core.Utils;
import com.everdro1d.libs.io.Files;
import com.everdro1d.libs.io.SyncPipe;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.DebugConsoleWindow;
import com.everdro1d.libs.swing.components.FileChooser;
import main.com.everdro1d.ytvd.core.commands.DebugCommand;
import main.com.everdro1d.ytvd.ui.HistoryWindow;
import main.com.everdro1d.ytvd.ui.MainWindow;
import main.com.everdro1d.ytvd.ui.WorkingPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.prefs.Preferences;

import static main.com.everdro1d.ytvd.core.AdvancedSettings.*;
import static main.com.everdro1d.ytvd.core.HistoryLogger.colUrl;
import static main.com.everdro1d.ytvd.ui.MainWindow.*;
import static main.com.everdro1d.ytvd.ui.WorkingPane.progressBar;
import static main.com.everdro1d.ytvd.ui.WorkingPane.workingFrame;

public class MainWorker {
    public static final String dro1dDevWebsite = "https://everdro1d.github.io/";
    public static final String currentVersion = "1.2.3"; //TODO: update this with each release
    public static final String titleText = "YouTube Video Downloader";
    private static final Map<String, CommandInterface> CUSTOM_COMMANDS_MAP = Map.of(
            "-debug", new DebugCommand()
    );
    public static CommandManager commandManager = new CommandManager(CUSTOM_COMMANDS_MAP);
    public static DebugConsoleWindow debugConsoleWindow;
    public static boolean debug = false; // whether debug mode is enabled
    public static boolean closeAfterInsert;
    protected static MainWindow window;
    public static int[] windowPosition = new int[]{0, 0, 0};
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
     *     <li>[5] OVERRIDE - EMPTY</li>
     * </ul>
     */
    public static final String[] validDownloadStatus = new String[]{
            "Completed - Success", "Stopped - Fatal Error", "Stopped - Already Exists",
            "Canceled - Deleted Files", "Canceled - Saved Files", "OVERRIDE - EMPTY"
    };
    public static boolean downloadStarted = false;

    /**
     * The binary files are
     * <ul>
     *     <li>[0] yt-dlp *note: use downloadBinary to call for correct name</li>
     *     <li>[1] ffmpeg</li>
     *     <li>[2] ffprobe</li>
     * </ul>
     */
    public static String[] binaryFiles = {"yt-dlp", "ffmpeg", "ffprobe"};
    protected static String binaryPath = "main/libs/"; // the path to the binary to run
    public static String downloadDirectoryPath = ""; // the path to download the video to
    public static boolean darkMode = false; // whether dark mode is enabled
    public static boolean compatibilityMode = false; // if the compatability mode is enabled
    public static boolean logHistory = true; // whether to log the download history
    public static final Preferences prefs = Preferences.userNodeForPackage(MainWorker.class);
    protected static String videoTitle = "";
    protected static String videoFileName = "";
    private static WorkingPane workingPane;
    public static boolean downloadCanceled = false;
    protected static boolean windows = false;
    protected static boolean macOS = false;
    protected static String jarPath;
    public static String fileDiv = "\\";
    public static String stringQuotes = "\"";

    public static void main(String[] args) {
        ApplicationCore.checkCLIArgs(args, commandManager);
        checkOSCompatibility();

        SwingGUI.setLookAndFeel(true, true, debug);

        loadPreferencesAndQueueSave();

        SwingGUI.lightOrDarkMode(darkMode, new JFrame[]{frame, workingFrame, DebugConsoleWindow.debugFrame, HistoryWindow.historyFrame});
        SwingGUI.uiSetup(darkMode, MainWindow.fontName, MainWindow.fontSize);

        if (debug) showDebugConsole();

        jarPath = Files.getJarPath(MainWorker.class, debug);
        copyBinaryTempFiles();

        startMainWindow();

        checkUpdate();
    }

    private static void startMainWindow() {
        EventQueue.invokeLater(() -> {
            try {
                window = new MainWindow();
                SwingGUI.setFramePosition(
                        frame,
                        prefs.getInt("framePosX", windowPosition[0]),
                        prefs.getInt("framePosY", windowPosition[1]),
                        prefs.getInt("activeMonitor", windowPosition[2])
                );
                SwingGUI.setFrameIcon(frame, "images/diskIconLargeDownloadArrow.png", MainWorker.class, debug);

                window.coloringModeChange();
            } catch (Exception ex) {
                if (debug) ex.printStackTrace(System.err);
                System.err.println("Failed to start main window.");
            }
        });
    }

    public static void checkOSCompatibility() {
        String detectedOS = ApplicationCore.detectOS(debug);
        executeOSSpecificCode(detectedOS);
    }

    public static void executeOSSpecificCode(String detectedOS) {
        switch (detectedOS) {
            case "Windows" -> {
                // binary arg stuff
                fileDiv = "\\";
                stringQuotes = "\"";

                // set binary path
                binaryPath += "win/";
                for (int i = 0; i < 3; i++) {
                    binaryFiles[i] += ".exe";
                }
            }
            case "macOS" -> {
                // binary arg stuff
                fileDiv = "/";
                stringQuotes = "";

                // set binary path
                binaryPath += "mac/";
                binaryFiles[0] = "yt-dlp_macos";

                // UI
                System.setProperty("apple.awt.application.name", titleText);
                System.setProperty("apple.awt.application.appearance", "system");
            }
            default -> { // Anything not windows or macOS
                System.err.println("This program is not compatible with your operating system.");
                JOptionPane.showMessageDialog(frame, "This program is not compatible with your operating system.", "Error!", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    public static void showDebugConsole() {
        if (debugConsoleWindow == null) {
            debugConsoleWindow = new DebugConsoleWindow(MainWindow.frame, MainWindow.fontName, 14, prefs, debug);
            if (debug) System.out.println("Debug console created.");
        } else if (!debugConsoleWindow.isVisible()) {
            debugConsoleWindow.setVisible(true);
            if (debug) System.out.println("Debug console shown.");
        } else {
            if (debug) System.out.println("Debug console already open.");
        }
    }

    private static void copyBinaryTempFiles() {
        if (debug) System.out.println("Copying binary files to temp directory.");
        downloadBinary = jarPath + fileDiv + binaryFiles[0];

        for (String binaryFile : binaryFiles) {
            if (debug) System.out.println("Copying binary file: " + binaryFile);

            try (InputStream binaryPathStream = MainWorker.class.getClassLoader().getResourceAsStream(binaryPath + binaryFile)) {
                if (binaryPathStream == null) {
                    System.err.println("Could not find binary file: " + binaryFile);
                    continue;
                }
                Path outputPath = new File((jarPath + fileDiv + binaryFile)).toPath();

                java.nio.file.Files.copy(binaryPathStream, outputPath, StandardCopyOption.REPLACE_EXISTING);

                // set binary file to hidden
                if (windows) java.nio.file.Files.setAttribute(outputPath, "dos:hidden", true);
                // set binary file to executable (and hidden on macOS)
                if (macOS) {
                    new ProcessBuilder("chflags", "hidden", outputPath.toString()).start();
                    new ProcessBuilder("chmod", "+x", outputPath.toString()).start();
                }

                Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteBinaryTempFile(binaryFile)));

            } catch (Exception e) {
                if (debug) e.printStackTrace(System.err);
            }
        }
    }

    private static void deleteBinaryTempFile(String binaryFile) {
        if (debug) System.out.println("Deleting binary file: " + binaryFile);
        File fileToDelete = new File((jarPath + fileDiv + binaryFile));
        if (fileToDelete.exists()) {
            int iterations = 0;
            while (!fileToDelete.delete() && iterations++ < 5) {
                System.err.println("Failed to delete temp file: " + binaryFile + "\nRetrying...");
                try {
                    closeProcess(null, binaryFile);

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

    private static void loadPreferencesAndQueueSave() {
        loadUserSettings();
        loadWindowPosition();

        savePreferencesOnExit();
    }

    private static void savePreferencesOnExit() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveUserSettings();
            saveWindowPosition();
        }));
    }

    private static void loadUserSettings() {
        darkMode = prefs.getBoolean("darkMode", false);
        downloadDirectoryPath = prefs.get("filePath", (System.getProperty("user.home") + "\\Downloads"));
        compatibilityMode = prefs.getBoolean("compatibilityMode", false);
        logHistory = prefs.getBoolean("logHistory", true);
        closeAfterInsert = prefs.getBoolean("closeAfterInsert", false);
    }

    private static void saveUserSettings() {
        prefs.putBoolean("darkMode", darkMode);
        prefs.put("filePath", downloadDirectoryPath);
        prefs.putBoolean("compatibilityMode", compatibilityMode);
        prefs.putBoolean("logHistory", logHistory);
        prefs.putBoolean("closeAfterInsert", closeAfterInsert);
    }

    private static void loadWindowPosition() {
        windowPosition[0] = prefs.getInt("framePosX", 0);
        windowPosition[1] = prefs.getInt("framePosY", 0);
        windowPosition[2] = prefs.getInt("activeMonitor", 0);
    }

    private static void saveWindowPosition() {
        prefs.putInt("framePosX", windowPosition[0]);
        prefs.putInt("framePosY", windowPosition[1]);
        prefs.putInt("activeMonitor", windowPosition[2]);
    }

    public static void checkUpdate() {
        // checks project GitHub for latest version at launch
        new Thread(() -> SwingGUI.updateCheckerDialog(currentVersion, null, debug,
                "https://github.com/everdro1d/YouTubeVideoDownloader/releases/latest/",
                dro1dDevWebsite + "projects.html#youtube-video-downloader", prefs))
                .start();
    }

    public static boolean validURL(String url) { //TODO #1 - see top of file comments
        if (url.isEmpty()) {
            if (debug) System.out.println("Failed url - empty.");
            return false;
        }
        if (url.length() < 11) { // too short to be a valid URL with/or a video ID
            if (debug) System.out.println("Failed url - too short.");
            return false;
        } else if (url.length() == 11) { // if URL contains only the video ID
            videoID = url;
        } else { // if the URL is longer than 11 characters

            // check if the URL is valid
            String[] validURLs = {
                    "youtube.com/watch?v=",
                    "youtu.be/",
                    "youtube.com/shorts/",

                    "instagram.com/p/",
                    "instagram.com/reel/"
            };

            if (Utils.containsAny(validURLs, rawURL)) {
                if (!validatedURL(url, validURLs)) {
                    if (debug) System.out.println("Failed url - validatedURL check.");
                    return false;
                }
            } else {
                if (debug) System.out.println("Failed url - containsAny check.");
                return false;
            }

            //check if the url is valid
            try { new URI(url); } catch (Exception e) {
                if (debug) System.out.println("Failed url - validatedURI check.");
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

        String[] splitURL = url.split("[/=?&.]");

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
        if (overrideValidURL) {
            return true;
        }
        if ((rawURL == null) || !validURL(rawURL) || show) {
            JOptionPane.showMessageDialog(frame, "Please check the link or enter a valid URL.", "Error! Media not found.", JOptionPane.ERROR_MESSAGE);
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
            getVideoTitleProcess();
            workingPane.setMessage(" Getting file info...");
            getVideoFileNameProcess();
            if (debug) System.out.println("Video Title: " + videoTitle);
            if (debug) System.out.println("Video Filename: " + videoFileName);
            workingPane.closeWorkingPane();

            if (!downloadCanceled) {
                // get download command
                ArrayList<String> cmd = getDownloadCommandList();
                if (debug) System.out.println("Download command: " + cmd.toString().replace(",", ""));

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

    public static void getVideoTitleProcess() {
        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(
                downloadBinary, "--get-title", "-o", "%(title)s", rawURL
        ));
        Process p;
        try {
            p = pb.start();
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            Scanner scanner = new Scanner(p.getInputStream());
            if (scanner.hasNextLine()) videoTitle = scanner.nextLine().trim();
            p.waitFor();
            if (debug) System.out.println(p.exitValue());
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
    }

    private static void getVideoFileNameProcess() {
        String fileName = "";
        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(
                downloadBinary, "--restrict-filenames", "--get-filename", "-o %(title)s ", rawURL
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

        videoFileName = fileName.replaceAll("#", "").trim();
    }

    public static ArrayList<String> getDownloadCommandList() {
        // the options to pass to the binary
        ArrayList<String> advancedSettings = getAdvancedSettings();
        ArrayList<String> cmdList = new ArrayList<>();
            cmdList.add(downloadBinary);
            cmdList.addAll(advancedSettings);
            cmdList.add("-P");

            String tmpDiv = (windows) ? "\\\\" : "/";
            cmdList.add(stringQuotes + downloadDirectoryPath + tmpDiv + stringQuotes);
            cmdList.add("-o");
            cmdList.add(stringQuotes + "%(title)s.%(ext)s" + stringQuotes);
            cmdList.add(stringQuotes + rawURL + stringQuotes);

        return cmdList;
    }

    public static void download(ArrayList<String> cmd) {
        if (cmd.isEmpty()) {
            System.err.println("Download command is empty.");
            return;
        }

        // start download
        new Thread(() -> downloadProcess(cmd)).start();
    }

    private static void downloadProcess(ArrayList<String> cmd) {
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
        downloadMax = switch (videoAudio) {
            case 0:
                yield 2; // video and audio are downloaded separately and the thumbnail is accounted for below
            case 1:
                yield 1; // video only
            case 2:
                yield 1; // audio only
            default:
                throw new IllegalStateException("Unexpected value: " + videoAudio);
        };
        int delMax = 1;

        if (!overrideValidURL) {
            delMax = (videoAudio == 0) ? 2 : 0;
            if (recode) { // account for recoding operations
                delMax += 1;
            }
            if (writeThumbnail || !advancedSettingsEnabled) {
                downloadMax += 1;
                if (embedThumbnail || !advancedSettingsEnabled) {
                    delMax += 1;
                }
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

                    if (s.contains("does not exist")) {
                        downloadCount = Math.max(downloadCount - 1, 0);
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
                                SwingGUI.setProgressPercent(-1, progressBar);
                            } else {
                                workingPane.setTitle("Merging...");
                                workingPane.setMessage(" Merging audio and video...");
                                SwingGUI.setProgressPercent(-1, progressBar);
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
                                SwingGUI.setProgressPercent(-1, progressBar);
                            } else {
                                workingPane.setTitle("Finishing...");
                                workingPane.setMessage(" Finishing up...");
                                SwingGUI.setProgressPercent(-1, progressBar);
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
                        JOptionPane.showMessageDialog(frame, "An error occurred while downloading the video:\n" + s, "Error!", JOptionPane.ERROR_MESSAGE);
                        for (String binaryFile : binaryFiles) {
                            closeProcess(p, binaryFile);
                        }
                        downloadChecked = true;
                        downloadStatus = validDownloadStatus[1];
                    } else if (s.contains("has already been downloaded")) {
                        if (debug) System.out.println("This video has already been downloaded.");
                        workingPane.setVisible(false);
                        workingPane.closeWorkingPane();
                        JOptionPane.showMessageDialog(frame, "This video has already been downloaded.", "Aborted!", JOptionPane.ERROR_MESSAGE);
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

    public static void logDownloadHistory() {
        if (logHistory) {
            System.out.println("Logging download history.");
            String url = rawURL;

            if (overrideValidURL && rawURL.isEmpty()) {
                videoTitle = validDownloadStatus[5];
                url = validDownloadStatus[5];
                downloadStatus = validDownloadStatus[5];
            }

            // check if the download failed somehow
            if ( !Utils.containsAny(validDownloadStatus, downloadStatus) ) {
                // if the download was canceled by the program (error from process, likely by an invalid url),
                // show the dialog and skip logging download history
                if (debug) System.out.println(
                        "Error from process. Skipped logging download history. Showing Dialog." + downloadStatus);
                checkURLDialog(true);
                return;
            }

            // log download history
            HistoryLogger historyLogger = new HistoryLogger();
            sanitizeVideoTitle();
            String[] data = { videoTitle, url, downloadStatus, getVideoAudioStatus(), Utils.getCurrentTime(true, true, false) };
            historyLogger.logHistory(data);
            if (debug) System.out.println("Logged download history: \n" + Arrays.toString(data));

        } else if (debug) {
            System.out.println("Logging download history is disabled. Skipping.");
        }
    }

    private static void sanitizeVideoTitle() {
        if (videoTitle.isEmpty()) {
            videoTitle = "Untitled";
        }
        if (videoTitle.length() > 50) {
            videoTitle = videoTitle.substring(0, 47).concat("...");
        }
        videoTitle = videoTitle.replaceAll(",", "");
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
        JOptionPane.showMessageDialog(frame, "Download Completed", "Finished!", JOptionPane.INFORMATION_MESSAGE);
        boolean downloadChecked = true;
        downloadStatus = validDownloadStatus[0];

        return downloadChecked;
    }

    private static String getVideoAudioStatus() {
        return switch (videoAudio) {
            case 0 -> "Video + Audio";
            case 1 -> "Video Only";
            case 2 -> "Audio Only";
            default -> "Unknown Type";
        };
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
                SwingGUI.setProgressPercent(progressInt, progressBar);
            }
        }
    }

    public static String openFileChooser() {
        String output = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser(output, "Select Download Location", false);

        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            output = fileChooser.getSelectedFile().getAbsolutePath();
            JOptionPane.showMessageDialog(frame, "Download location set to: " + output, "Download Location", JOptionPane.INFORMATION_MESSAGE);

        } else if (!downloadDirectoryPath.isEmpty()) output = downloadDirectoryPath;

        return output;
    }

    public static void closeProcess(Process p, String binaryFile) {
        if (p == null) {
            p = globalDefaultProcess;
        }
        try {
            if (windows) new ProcessBuilder("taskkill", "/F", "/IM", binaryFile).start();
            if (macOS) new ProcessBuilder("killall", "-SIGINT", binaryFile).start();
            System.out.println("Attempted to close task: " + binaryFile);
            if (p != null && p.isAlive()) {
                p.destroy();
            }
        } catch (IOException e) {
            if (debug) e.printStackTrace(System.err);
        }
    }

    public static void openLinkFromTable(HistoryWindow historyWindow, JTable historyTable, int selectedRow) {
        if (selectedRow == -1) {
            // show an error dialog if no row is selected
            JOptionPane.showMessageDialog(historyWindow,
                    "No row selected. Please select a row and try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        } else {
            // get the string in the first column of the selected row
            String url = (String) historyTable.getValueAt(selectedRow, colUrl);
            Utils.openLink(url, debug);
        }
    }

    public static void insertURL(HistoryWindow historyWindow, JTable historyTable, int selectedRow) {
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
                SwingGUI.simulateKeyEvent(textField_URL, KeyEvent.VK_ENTER, '\n', 0, KeyEvent.KEY_RELEASED);
                textField_URL.selectAll();
            } catch (Exception e) {
                if (debug) e.printStackTrace(System.err);
            }
        }

        if (closeAfterInsert) {
            historyWindow.dispose();
        }
    }

    public static void copyRowToClipboard(HistoryWindow historyWindow, JTable historyTable, int selectedRow, int i) {
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

            Utils.copyToClipboard(copyString.toString(), debug);
        }
    }

    public static void deleteRelatedFiles() {
        // an array of all files in download directory that match the video file name

        String[] fileList = new File(downloadDirectoryPath + fileDiv).list((dir, name) -> name.startsWith(videoFileName));

        if ( fileList == null || fileList.length < 1 )  {
            System.err.println("[ERROR] Failed to retrieve list of files to delete."
                    + "\nDirectory: " + downloadDirectoryPath
                    + "\nFilename: " + videoFileName
            );
            return;
        } else {
            if (debug) {
                System.out.println("Files to delete: " + Arrays.toString(fileList));
                System.out.println("Directory: " + downloadDirectoryPath);
            }
        }

        // delete all matching files
        for (String file : fileList) {
            Path pathToDelete = Paths.get(downloadDirectoryPath + "\\" + file);
            for (int tries = 0; tries < 5; tries++) {
                try {
                    java.nio.file.Files.delete(pathToDelete);
                    if (debug) System.out.println("Deleted file: " + pathToDelete.getFileName());
                    break;
                } catch (IOException e1) {
                    System.err.println("[ERROR] Failed to delete file: " + pathToDelete.getFileName());
                    if (debug) e1.printStackTrace(System.err);
                    if (debug) System.out.println(
                            "Directory: " + downloadDirectoryPath
                            + "\nFilename: " + videoFileName
                    );
                }
            }
        }
    }
}
