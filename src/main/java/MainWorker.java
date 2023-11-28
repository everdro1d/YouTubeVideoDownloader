/*
 */

package main.java;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;
import java.util.prefs.Preferences;

import static main.java.AdvancedSettings.*;
import static main.java.MainWindow.fontName;
import static main.java.MainWindow.frame;
import static main.java.WorkingPane.workingFrame;

public class MainWorker {
    protected static MainWindow window;
    protected static Thread downloadThread;
    private static Process globalDefaultProcess;
    public static String rawURL = ""; // raw URL String from the text field
    public static String videoID; // the video ID from the URL
    protected static String downloadBinary = ""; // the name of the binary to run
    protected static int downloadCount = 0;
    protected static int downloadMax = 1;
    protected static boolean downloadStarted = false;
    protected static String[] binaryFiles = {"yt-dlp.exe", "ffmpeg.exe", "ffprobe.exe"};
    protected static String binaryPath = "main/libs/"; // the path to the binary to run
    protected static String filePath = ""; // the path to download the video to
    public static boolean debug = false; // whether debug mode is enabled
    protected static boolean darkMode = false; // whether dark mode is enabled
    protected static boolean compatibilityMode = false; // if the compatability mode is enabled
    static Preferences prefs = Preferences.userNodeForPackage(MainWorker.class);


    public static void main(String[] args) {
        if (args.length > 0) {
            String arg = args[0];
            if (arg.equals("debug")) {
                System.out.println("Debug mode enabled.");
                debug = true;
            } else {
                System.out.println(
                        "Unknown argument: " + arg +
                        "\nValid arguments: debug" +
                        "\nContinuing without arguments."
                );
            }
        }
        String osName = System.getProperty("os.name").toLowerCase();
        boolean windows = osName.contains("win");
        if (!windows) {
            System.err.println("This program is currently only compatible with Windows.");
            JOptionPane.showMessageDialog(null, "This program is currently only compatible with Windows.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        downloadBinary = binaryFiles[0];

        for (String binaryFile : binaryFiles) {
            try (InputStream binaryPathStream = MainWorker.class.getClassLoader().getResourceAsStream(binaryPath + binaryFile)) {
                if (binaryPathStream == null) {
                    System.err.println("Could not find binary file: " + binaryFile);
                    continue;
                }
                Path outputPath = new File(binaryFile).toPath();

                Files.copy(binaryPathStream, outputPath, StandardCopyOption.REPLACE_EXISTING);

                Files.setAttribute(outputPath, "dos:hidden", true);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    File fileToDelete = new File(binaryFile);
                    if (fileToDelete.exists()) {
                        if (!fileToDelete.delete()) {
                            System.err.println("Failed to delete temp file: " + binaryFile + "\nRetrying...");
                            try {
                                Runtime.getRuntime().exec("taskkill /F /IM " + binaryFile);

                                Thread.sleep(200);

                                if (fileToDelete.delete()) {
                                    System.err.println("Deleted temp file: " + binaryFile);
                                }
                            } catch (Exception e) {
                                e.printStackTrace(System.out);
                            }
                        }
                    }
                }));
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        FlatLightLaf.setup();
        FlatDarkLaf.setup();

        darkMode = prefs.getBoolean("darkMode", false);
        filePath = prefs.get("filePath", (System.getProperty("user.home") + "\\Downloads"));
        compatibilityMode = prefs.getBoolean("compatibilityMode", false);

        lightDarkMode();

        uiManager();

        EventQueue.invokeLater(() -> {
            try {
                window = new MainWindow();
                window.coloringModeChange();
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        });

        checkUpdate();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            prefs.put("filePath", filePath);
            prefs.putBoolean("darkMode", darkMode);
            prefs.putBoolean("compatibilityMode", compatibilityMode);
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
        try {
            new Thread(()->{
                ProcessBuilder pb = new ProcessBuilder(Arrays.asList(downloadBinary, "-U"));
                Process p;
                try {
                    p = pb.start();
                    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
                    p.waitFor();
                    System.out.println(p.exitValue());
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
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
        // check if the URL is valid
        String[] validURLs = {
                "https://www.youtube.com/watch?v=",
                "https://youtu.be/",
                "https://www.youtube.com/channel/",

                "https://www.instagram.com/p/",
                "https://www.instagram.com/reel/"
        };

        if (containsAny(validURLs, rawURL)) {
            if (!validatedURL(url, validURLs)) {
                return false;
            }
        }

        //check if the url is valid
        try {
            new java.net.URI(url);
            return true;
        } catch (Exception e) {
            return false;
        }
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

    private static boolean checkURLDialog() {
        if ((rawURL == null) || !validURL(rawURL)) {
                JOptionPane.showMessageDialog(null, "Please enter a valid YouTube URL.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void downloadButtonClicked() {
        downloadStarted = false;
        downloadCount = 0;
        // start download
        if ( !checkURLDialog() ) { return; }

        if (filePath.isEmpty()) {
            filePath = openFileChooser();
        }

        String cmd = getCommand();
        if (debug) {
            System.out.println(cmd);
        }

        download(cmd);
    }

    public static String getCommand() {
        // the options to pass to the binary
        String advancedSettings = getAdvancedSettings();

        return downloadBinary + " " + advancedSettings + "-o \"" + filePath + "\\%(title)s.%(ext)s\" " + "\"" + rawURL + "\"";
    }

    public static void download(String cmd) {
        // start download
        try {
            downloadThread = new Thread(()->{
                ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
                Process p;
                try {
                    try {
                        p = pb.start();
                        globalDefaultProcess = p;
                        new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                        try (Scanner scanner = new Scanner(p.getInputStream())) {
                            downloadProgressPanes(scanner, p);
                        }
                        //Thread.sleep(200);
                        p.waitFor();

                        System.out.println(p.exitValue());
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            });
            downloadThread.start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private static void downloadProgressPanes(Scanner scanner, Process p) {
        WorkingPane workingPane = new WorkingPane();

        boolean downloadComplete = false;
        boolean downloadChecked = false;

        int delCount = 0;
        int delMax = (videoAudio == 0) ? 2 : 1;
        downloadMax = (videoAudio == 0) ? 2 : 1;

        if (!scanner.hasNextLine()) {
            System.out.println("No output from process");
            workingFrame.dispose();
            JOptionPane.showMessageDialog(null, "No output from process.\nDownloadPP", "Error!", JOptionPane.ERROR_MESSAGE);
            for (String binaryFile : binaryFiles) {
                closeProcess(p, binaryFile);
            }
            return;
        }

        while (scanner.hasNextLine()) {
            //Skip lines until after the download begins
            String s = scanner.nextLine();
            if (debug) {
                System.out.println(s);
            }
            if (s.contains("[info]") && s.contains("Downloading")) {

                break;
            }
        }


        //OptionPanes to show the progress of the download
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if (debug) {
                System.out.println(s);
            }
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
                if (s.contains("[download] 100% of")) {
                    downloadComplete = true;
                    downloadCount++;
                }
                if (downloadComplete && (downloadCount == downloadMax)) {
                    if (videoAudio == 0) {
                        workingPane.setTitle("Merging...");
                        workingPane.setMessage(" Merging audio and video...");
                        workingPane.setProgress(-1);
                        if (recode) {
                            workingPane.setTitle("Recoding...");
                            workingPane.setMessage(
                                    " Recoding to " + ((arrayRecodeExt.length >= recodeExt) ?
                                            arrayRecodeExt[recodeExt] :
                                            "[FORMAT ERROR]") +
                                            "..." +
                                    "\nNote: recoding can take a while.");
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
                            workingPane.setMessage(
                                    " Recoding to " + ((arrayRecodeExt.length >= recodeExt) ?
                                            arrayRecodeExt[recodeExt] :
                                            "[FORMAT ERROR]") +
                                            "..." +
                                    "\nNote: recoding can take a while.");
                            workingPane.setProgress(-1);

                        }
                    }
                    if (delCount == delMax) {
                        workingPane.setVisible(false);
                        workingFrame.dispose();
                        JOptionPane.showMessageDialog(null, "Download Completed", "Finished!", JOptionPane.INFORMATION_MESSAGE);
                        downloadChecked = true;
                    }
                } else if (s.contains("ERROR:")) {
                    workingPane.setVisible(false);
                    workingFrame.dispose();
                    JOptionPane.showMessageDialog(null, "An error occurred while downloading the video.:\n" + s, "Error!", JOptionPane.ERROR_MESSAGE);
                    for (String binaryFile : binaryFiles) {
                        closeProcess(p, binaryFile);
                    }
                    downloadChecked = true;

                } else if (s.contains("has already been downloaded") && !s.contains(".part")) {
                    workingPane.setVisible(false);
                    workingFrame.dispose();
                    JOptionPane.showMessageDialog(null, "This video has already been downloaded.", "Error!", JOptionPane.ERROR_MESSAGE);
                    for (String binaryFile : binaryFiles) {
                        closeProcess(p, binaryFile);
                    }
                    downloadChecked = true;
                }
            }
        }

        workingFrame.dispose();
        scanner.close();
    }

    private static void setWorkingPaneMessage(WorkingPane workingPane, String s) {
        String[] split = s.split(" ");
        String[] split2 = Arrays.copyOfRange(split, 1, split.length-2);
        String message = String.join(" ", split2);
        workingPane.setMessage(" " + message);
        workingPane.setCTitle(" Working...   (" + (downloadCount + 1) + "/" + downloadMax + ")");

        if (message.contains("%")) {
            String progress = message.split("%")[0].replace(".", "").replace(" ", "");
            if (!progress.isEmpty()) {
                int progressInt = Math.round(Float.parseFloat(progress) / 10);
                workingPane.setProgress(progressInt);
            }
        }

    }

    public static String openFileChooser() {
        String output = System.getProperty("user.home") + "\\Downloads";

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
            Runtime.getRuntime().exec("taskkill /F /IM " + binaryFile);
            if (p != null && p.isAlive()) {
                p.destroy();
            }
            System.out.println("Closed task: " + binaryFile);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
