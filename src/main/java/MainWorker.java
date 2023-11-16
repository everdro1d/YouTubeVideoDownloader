/* TODO
 * 1. Add a way to select the download location
 * 2. Add a way to select the quality
 * 3. Add a way to select the format
 * 4. Create methods to do the following:
 *    -------------------------------
 *    a. take the options hashmap and pipe it to its comboboxes
 *    b. allow the user to select the options and update the advanced options array
 *    c. take the advanced options array and create a string to pass to the binary
 *
 */

package main.java;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

import static main.java.AdvancedSettings.getadvancedSettings;
import static main.java.MainWindow.fontName;
import static main.java.MainWindow.frame;

public class MainWorker {
    public static String rawURL; // raw URL String from the text field
    public static int videoAudio; // 0 = video and audio, 1 = audio only, 2 = video only
    protected static final String downloadBinary = "src/main/libs/yt-dlp.exe "; // the path to the binary to run
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
                MainWindow window = new MainWindow();
                window.coloringModeChange();
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        });

        //checkUpdate(); //TODO re-enable when done, this makes a 403 error

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
            } catch (Exception ex) {
                System.err.println("Could not set look and feel of application.");
            }
        }
    }

    public static void checkUpdate() {
        try {
            new Thread(()->{
                Process p;
                try {
                    p = Runtime.getRuntime().exec(downloadBinary + "-U");
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
        if ( !(url.contains("youtube.com") || url.contains("youtu.be"))) {
            return false;
        }
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ignored) { }
        return false;
    }

    private static boolean checkURLDialog() {
        if ((rawURL == null) || !validURL(rawURL)) {
            JOptionPane.showMessageDialog(null, "Please enter a valid YouTube URL.", "Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    public static void downloadButtonClicked() {
        // start download
        if ( checkURLDialog() ) { return; }

        if (filePath.isEmpty()) {
            filePath = openFileChooser();
        }

        String cmd = getCommand();
        download(cmd);

        System.out.println("Download button clicked");
        System.out.println("command to run: " + cmd);
    }

    public static String getCommand() {
        // the options to pass to the binary
        String advancedSettings = getadvancedSettings();

        return downloadBinary + advancedSettings + "-o \"" + filePath + "\\%(title)s.%(ext)s\" " + "\"" + rawURL + "\"";
    }

    public static void download(String cmd) { //TODO add a way to show the progress of the download
        // start download
        try {
            new Thread(()->{
                Process p;
                try {
                    p = Runtime.getRuntime().exec(cmd);
                    //TODO make a working dialog - note from later: IF I EVER FIGURE OUT HOW TO DO IT, I WILL
                    //show the dialog
                    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
                    p.waitFor();

                    //dispose of the dialog

                    //show a dialog saying the download is done
                    JOptionPane.showMessageDialog(null, "Download Completed", "Finished!", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
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
