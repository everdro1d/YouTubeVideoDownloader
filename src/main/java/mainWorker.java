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
    public static int videoAudio; // 0 = video and audio, 1 = audio only, 2 = video only
    private static final String downloadBinary = "libs/yt-dlp.exe "; // the path to the binary to run
    protected static String filePath = ""; // the path to download the video to

    protected static int videoFormat = 0; // the video format to download
    protected static int audioFormat = 0; // the audio format to download

    protected static String[] arrayVideoFormats = new String[]
            {"mp4", "avi", "mov", "mkv", "flv", "webm"};
    protected static String[] arrayAudioFormats = new String[]
            {"m4a", "mp3", "aac", "aiff", "flac", "ogg", "wav", "webm"};

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
        //TODO - read the table from console output and sort it into the GUI

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

        return downloadBinary + advancedOptions + "-o \"" + filePath + "%(title)s.%(ext)s\" " + "\"" + rawURL + "\"";
    }

    public static String getAdvancedOptions() {
        StringBuilder output = new StringBuilder();
        ArrayList<String> arrayListAdvancedOptions = new ArrayList<>();

        //TODO

        // restrict filenames to ascii characters only, get the best video of selected format, get the best audio of selected format, merge the two files
        // if none of the selected formats are available, get the best available format
        String defaultVideoAudioOptions
                = "--restrict-filenames -f \"bv*[ext=" + arrayVideoFormats[videoFormat] + "]+ba[ext="
                + arrayAudioFormats[audioFormat] + "]\"";
        String defaultVideoOptions
                = "--restrict-filenames -f \"bv[ext=" + arrayVideoFormats[videoFormat] + "]\"";
        String defaultAudioOptions
                = "--restrict-filenames -x --audio-format " + arrayAudioFormats[audioFormat] + " --audio-quality 0";


        if (videoAudio == 0) {
            arrayListAdvancedOptions.add(defaultVideoAudioOptions);
        } else if (videoAudio == 1) {
            arrayListAdvancedOptions.add(defaultVideoOptions);
        } else if (videoAudio == 2) {
            arrayListAdvancedOptions.add(defaultAudioOptions);
        }
        //TODO
        // add the selected options to the cmd variable


        for (String arrayListAdvancedOption : arrayListAdvancedOptions) {
            // add the options to the cmd variable
            output.append(arrayListAdvancedOption).append(" ");
        }

        return output.toString();
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
        // TODO
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
