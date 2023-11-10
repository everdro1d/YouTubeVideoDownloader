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

public class mainWorker {

    public static String rawURL; // raw URL String from the text field
    public static int videoAudio; // 0 = video, 1 = audio
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
    }

    public static void downloadButtonClicked() {
        System.out.println("Download button clicked");
        System.out.println("URL: " + rawURL);
    }
}
