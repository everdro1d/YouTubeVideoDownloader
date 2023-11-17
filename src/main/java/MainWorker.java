/*
 */

package main.java;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.prefs.Preferences;

import static main.java.AdvancedSettings.getAdvancedSettings;
import static main.java.MainWindow.fontName;
import static main.java.MainWindow.frame;
import static main.java.WorkingPane.workingFrame;

public class MainWorker {
    protected static MainWindow window;
    protected static Thread downloadThread;
    public static String rawURL; // raw URL String from the text field
    protected static final String downloadBinary = "yt-dlp.exe "; // the name of the binary to run
    protected static final String binaryPath = "src/main/libs/"; // the path to the binary to run
    protected static String filePath = ""; // the path to download the video to
    protected static boolean darkMode = false; // whether dark mode is enabled
    static Preferences prefs = Preferences.userNodeForPackage(MainWorker.class);



    public static void main(String[] args) {
        FlatLightLaf.setup();
        FlatDarkLaf.setup();

        darkMode = prefs.getBoolean("darkMode", false);
        filePath = prefs.get("filePath", (System.getProperty("user.home") + "\\Downloads"));

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
                ProcessBuilder pb = new ProcessBuilder(Arrays.asList(binaryPath+downloadBinary, "-U"));
                pb.directory(new File(binaryPath));
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

    protected static boolean validURL(String url) {
        // check if the URL is valid
        if ( !(url.contains("youtube.com") || url.contains("youtu.be")) || (url.contains("list"))) {
            return false;
        }

        if (!ytDLPValidatedURL(url)) {
            return false;
        }

        //check if the url is valid
        try {
            new java.net.URI(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean ytDLPValidatedURL(String url) {
        boolean valid = false;
        String[] validURLs = {
                "https://www.youtube.com/watch?v=",
                "https://youtu.be/",
                "https://www.youtube.com/shorts/"
        };

        String[] splitURL = url.split("[/=]");
        String videoID = splitURL[splitURL.length-1];
        if (videoID.length() == 11) {
            for (String validURL : validURLs) {
                if (url.contains(validURL)) {
                    valid = true;
                    break;
                }
            }
        }

        return valid;
    }

    private static boolean checkURLDialog() {
        if ((rawURL == null) || !validURL(rawURL)) {
            if (rawURL != null && rawURL.contains("list")) {
                JOptionPane.showMessageDialog(null, """
                        Playlist downloading is not supported yet.
                        If you are trying to download a single video, try removing:
                        "&list=(whatever the rest of the url is here)".""", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a valid YouTube URL.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
        return true;
    }

    public static void downloadButtonClicked() {
        // start download
        if ( !checkURLDialog() ) { return; }

        if (filePath.isEmpty()) {
            filePath = openFileChooser();
        }

        String cmd = getCommand();
        download(cmd);
    }

    public static String getCommand() {
        // the options to pass to the binary
        String advancedSettings = getAdvancedSettings();

        return binaryPath + downloadBinary + advancedSettings + "-o \"" + filePath + "\\%(title)s.%(ext)s\" " + "\"" + rawURL + "\"";
    }

    public static void download(String cmd) {
        // start download
        try {
            downloadThread = new Thread(()->{
                ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
                pb.directory(new File(binaryPath));
                Process p;
                try {
                    try {
                        p = pb.start();
                        new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                        try (Scanner scanner = new Scanner(p.getInputStream())) {
                            downloadProgressPanes(scanner);
                        }
                        p.waitFor();
                        System.out.println(p.exitValue());
                    } catch (Exception e) {
                        System.out.println("Download interrupted.");
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

    private static void downloadProgressPanes(Scanner scanner) {
        WorkingPane workingPane = new WorkingPane();

        if (!scanner.hasNextLine()) {
            System.out.println("No output from process");
            return;
        }

        while (scanner.hasNextLine()) {
            //Skip lines until after the download begins
            String s = scanner.nextLine();
            if (s.contains("[info]") && s.contains("Downloading")) {
                break;
            }
        }

        //OptionPanes to show the progress of the download
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if (s.contains("[download]")) {
                setWorkingPaneMessage(workingPane, s);
            }

            if (s.contains("[download] 100% of")) {
                workingPane.setVisible(false);
                workingFrame.dispose();
                JOptionPane.showMessageDialog(null, "Download Completed", "Finished!", JOptionPane.INFORMATION_MESSAGE);
                break;

            } else if (s.contains("ERROR:")) {
                workingPane.setVisible(false);
                workingFrame.dispose();
                JOptionPane.showMessageDialog(null, "An error occurred while downloading the video.:\n" + s, "Error!", JOptionPane.ERROR_MESSAGE);
                break;

            } else if (s.contains("has already been downloaded")) {
                workingPane.setVisible(false);
                workingFrame.dispose();
                JOptionPane.showMessageDialog(null, "This video has already been downloaded.", "Error!", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
        workingFrame.dispose();
        scanner.close();
    }

    private static void setWorkingPaneMessage(WorkingPane workingPane, String s) {
        String[] split = s.split(" ");
        String[] split2 = Arrays.copyOfRange(split, 1, split.length-2);
        String message = String.join(" ", split2);
        workingPane.setMessage(message);

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

}
