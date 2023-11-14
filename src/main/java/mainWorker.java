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

import javax.swing.*;
import java.awt.*;

import static main.java.advancedOptions.getAdvancedOptions;

public class mainWorker {
    public static String rawURL; // raw URL String from the text field
    public static int videoAudio; // 0 = video and audio, 1 = audio only, 2 = video only
    protected static final String downloadBinary = "src/main/libs/yt-dlp.exe "; // the path to the binary to run
    protected static String filePath = ""; // the path to download the video to


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            System.err.println("Could not set look and feel of application.");
        }
        EventQueue.invokeLater(() -> {
            try {
                mainWindow window = new mainWindow();
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        });
        checkUpdate();
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
        if ( !(url.contains("youtube.com") || url.contains("youtu.be")) ) {
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

        filePath = getFilePath();
        String cmd = getCommand();
        download(cmd);

        System.out.println("Download button clicked");
        System.out.println("command to run: " + cmd);
    }

    public static String getCommand() {
        // the options to pass to the binary
        String advancedOptions = getAdvancedOptions();

        return downloadBinary + advancedOptions + "-o \"" + filePath + "%(title)s.%(ext)s\" " + "\"" + rawURL + "\"";
    }

    public static void download(String cmd) {
        // start download
        try {
            new Thread(()->{
                Process p;
                try {
                    p = Runtime.getRuntime().exec(cmd);
                    //TODO make a working dialog
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

    public static String getFilePath() {
        // TODO: add a way to select the download location
        // 1. get the file path from the file chooser
        // 2. update the file path variable
        String output = "";



        if ( output.isEmpty() ) {
            output = (System.getProperty("user.home") + "\\Downloads\\");
        }
        output = "C:\\Users\\everd\\Documents\\IntelliJ\\YoutubeVideoDownloaderV2\\";
        return output;
    }


}
