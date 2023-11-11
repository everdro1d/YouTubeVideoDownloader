/* TODO
 * 1. Add a way to select the download location
 * 2. Add a way to select the quality
 * 3. Add a way to select the format
 * 4. Create methods to do the following:
 *    a. Download video (default to mp4)
 *    b. Download audio (default to mp3)
 *    -----------------
 *    c. retrieve video quality and format options
 *    d. retrieve audio quality and format options
 *    e. put options into lists
 *    f. update lists in gui
 *
 * FIXME
 *
 */

package main.java;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class mainWorker {

    public static String rawURL; // raw URL String from the text field
    public static int videoAudio; // 0 = video, 1 = audio
    private static final String downloadBinary = "libs/yt-dlp.exe "; // the path to the binary to run
    protected static String filePath = ""; // the path to download the video to

    protected static String[] arrayVideoFormats = new String[]
            {"mp4", "mkv", "webm", "avi", "mov"};
    protected static String[] arrayAudioFormats = new String[]
            {"m4a", "mp3", "aac", "flac", "ogg", "opus", "vorbis", "aiff", "opus"};

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

    public static void readVideoOptionsFromYT() {
        //TODO

        // get the video options from the URL
        getVideoOptions();

    }
    private static void getVideoOptions() {
        String cmd = downloadBinary + "--list-formats " + rawURL;

        try {
            new Thread(()->{
                Process p;
                try {
                    p = Runtime.getRuntime().exec(cmd);
                    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
                    p.waitFor();
                    //System.out.println(p.exitValue());
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

        return downloadBinary + advancedOptions + "-o \"" + filePath + "%(title)s.%(ext)s\" " + rawURL;
    }

    public static String getAdvancedOptions() {
        String output = "";
        ArrayList<String> arrayListAdvancedOptions = new ArrayList<String>();

        //TODO
        String defaultVideoOptions = "--restrict-filenames -S res:1080,ext:" + arrayVideoFormats[0] + ":" + arrayAudioFormats[0];
        String defaultAudioOptions = "--restrict-filenames -x --audio-format " + arrayAudioFormats[0] + " --audio-quality 0";

        if (videoAudio == 0) {
            arrayListAdvancedOptions.add(defaultVideoOptions);
        } else if (videoAudio == 1) {
            arrayListAdvancedOptions.add(defaultAudioOptions);
        }
        //TODO
        // add the options to the cmd variable



        for (int i = 0; i < arrayListAdvancedOptions.size() ; i++) {
            // add the options to the cmd variable
            output += arrayListAdvancedOptions.get(i) + " ";
        }

        return output;
    }

    public static void download(String cmd) {
        // start download
        try {
            new Thread(()->{
                Process p;
                try {
                    p = Runtime.getRuntime().exec(cmd);
                    //make a working dialog
                    //show the dialog
                    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
                    p.waitFor();
                    //dispose of the dialog
                    JOptionPane.showConfirmDialog(null, "Done", "Finished!", JOptionPane.DEFAULT_OPTION);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static String getFilePath() {
        // TODO
        // 1. get the file path from the file chooser
        // 2. update the file path variable


        String output = "";

        if (output.equals("")) {
            output = (System.getProperty("user.home") + "\\Downloads\\");
        }
        output = "C:\\Users\\everd\\Documents\\IntelliJ\\YoutubeVideoDownloaderV2\\";
        return output;
    }


}
