package main.java;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;

import static main.java.MainWindow.*;
import static main.java.MainWorker.*;
import static main.java.TableReaderFromConsole.scannerTableMap;

public class AdvancedSettings {
    protected static Map<String, Map<String, String>> tableMap; // the table of video options (--list-formats)
    protected static int videoAudio = 0; // 0 = video and audio, 1 = audio only, 2 = video only
    protected static boolean advancedSettingsEnabled = false; // if the advanced options are enabled
    protected static volatile boolean getVideoOptions = false; // if the video options are enabled

    protected static boolean recode = false; // if recode is enabled
    protected static boolean writeThumbnail = false; // if write thumbnail is enabled
    protected static boolean embedThumbnail = false; // if embed thumbnail is enabled
    protected static boolean addMetadata = false; // if add metadata is enabled

    protected static int videoExt = 0; // the video format to download
    protected static int videoResolution = 0; // the video resolution to download
    protected static int videoCodec = 0; // the video codec to download
    protected static int videoFPS = 0; // the video FPS to download
    protected static int videoVBR = 0; // the video VBR to download

    protected static int audioExt = 0; // the audio format to download
    protected static int audioChannels = 0; // the audio channels to download
    protected static int audioCodec = 0; // the audio codec to download
    protected static int audioABR = 0; // the audio sample rate to download
    protected static int audioASR = 0; // the audio sample rate to download

    protected static int recodeExt = 0; // the recode format to download
    protected static int writeThumbnailExt = 0; // the write thumbnail format to download

    protected static String[] arrayVideoExtensions = new String[] {""};
    protected static String[] arrayVideoResolution = new String[] {""};
    protected static String[] arrayVideoCodec = new String[] {""};
    protected static String[] arrayVideoFPS = new String[] {""};
    protected static String[] arrayVideoVBR = new String[] {""};

    protected static String[] arrayAudioExtensions = new String[] {""};
    protected static String[] arrayAudioChannels = new String[] {""};
    protected static String[] arrayAudioCodec = new String[] {""};
    protected static String[] arrayAudioABR = new String[] {""};
    protected static String[] arrayAudioASR = new String[] {""};
    protected static final String[] arrayVARecodeExt = new String[] {"avi", "flv", "mkv", "mov", "mp4", "webm"};
    protected static final String[] arrayVORecodeExt = new String[] {"avi", "flv", "mkv", "gif", "mov", "mp4", "webm"};
    protected static final String[] arrayAORecodeExt = new String[] {"aac", "aiff", "flac", "m4a", "mka", "mp3", "ogg", "opus", "wav"};
    protected static String[] arrayRecodeExt = new String[] {""}; // gets set depending on the videoAudio variable

    protected static final String[] arrayEmbedThumbnailSupported = new String[] // supported formats for embed thumbnail
            {"mp3", "mkv", "mka", "ogg", "opus", "flac", "m4a", "mp4", "m4v", "mov"};
    protected static final String[] arrayWriteThumbnailExt = new String[] // supported formats for write thumbnail
            {"png", "jpg", "webp"};


    public static void readVideoOptionsFromYT() {
        // get the video options from the URL
        advancedSettingsEnabled = true;
        getVideoOptions();
    }

    private static void getVideoOptions() {
        String cmd = downloadBinary + " --list-formats " + rawURL;
        new Thread(()-> videoOptionsProcess(cmd.split(" "))).start();
    }

    private static void videoOptionsProcess(String[] cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        try {
            Process p = pb.start();
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            Scanner scanner = new Scanner(p.getInputStream());
            scannerTableMap(scanner, p);
            int i = p.waitFor();
            if (i != 0) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                        "An error occurred when retrieving settings." +
                                "\nPlease check that the URL is correct and try again.",
                        "Error!", JOptionPane.ERROR_MESSAGE));

                advancedSettingsEnabled = false;
                MainWindow.checkBoxAdvancedSettings.setSelected(false);
                MainWorker.window.advancedSettingsEvent(true);
            }
            getVideoOptions = true;
        } catch (Exception e) {
            if (MainWorker.debug) e.printStackTrace(System.err);
        }
    }

    public static void setAdvancedSettings() {
        // set the advanced options comboBox arrays from the tableMap

        //Video ------------------------------------------
        arrayVideoExtensions =
                getUniqueValues( "EXT", option -> option.get("ACODEC").equals("video only") )
                .toArray(new String[0]);

        arrayVideoResolution =
                getUniqueValues( "RESOLUTION", option -> option.get("ACODEC").equals("video only") )
                .toArray(new String[0]);
        sortArrayValues("RESOLUTION", arrayVideoResolution);

        arrayVideoFPS =
                getUniqueValues( "FPS", option -> option.get("ACODEC").equals("video only") )
                .toArray(new String[0]);
        sortArrayValues("FPS", arrayVideoFPS);

        arrayVideoVBR =
                getUniqueValues( "VBR", option -> option.get("ACODEC").equals("video only") )
                .toArray(new String[0]);
        sortArrayValues("VBR", arrayVideoVBR);

        arrayVideoCodec =
                getUniqueValues( "VCODEC", option -> option.get("ACODEC").equals("video only") )
                .toArray(new String[0]);
        sortArrayValues("VCODEC", arrayVideoCodec);


        //Audio ------------------------------------------

        arrayAudioExtensions =
                getUniqueValues( "EXT", option -> option.get("CH").matches("[0-9]")
                                && option.get("VCODEC").equals("audio only") ).toArray(new String[0]);

        arrayAudioChannels =
                getUniqueValues( "CH", option -> option.get("CH").matches("[0-9]"))
                .toArray(new String[0]);
        sortArrayValues("CH", arrayAudioChannels);

        arrayAudioCodec =
                getUniqueValues( "ACODEC", option -> option.get("CH").matches("[0-9]")
                                && option.get("VCODEC").equals("audio only") ).toArray(new String[0]);
        sortArrayValues("ACODEC", arrayAudioCodec);

        arrayAudioABR =
                getUniqueValues( "ABR", option -> option.get("CH").matches("[0-9]")
                                && option.get("VCODEC").equals("audio only") ).toArray(new String[0]);
        sortArrayValues("ABR", arrayAudioABR);

        arrayAudioASR =
                getUniqueValues( "ASR", option -> option.get("CH").matches("[0-9]")
                                && option.get("VCODEC").equals("audio only") ).toArray(new String[0]);
        sortArrayValues("ASR", arrayAudioASR);


        //Recode ------------------------------------------
        switch (videoAudio) {
            case 0:
                arrayRecodeExt = arrayVARecodeExt;
                break;
            case 1:
                arrayRecodeExt = arrayVORecodeExt;
                break;
            case 2:
                arrayRecodeExt = arrayAORecodeExt;
                break;
        }
        sortArrayValues("RECODE", arrayRecodeExt);


        // Update the GUI
        MainWindow.updateComboBox( arrayVideoExtensions, comboBoxVideoExt );
        MainWindow.updateComboBox( arrayVideoResolution, MainWindow.comboBoxVideoResolution );
        MainWindow.updateComboBox( arrayVideoFPS, MainWindow.comboBoxVideoFPS );
        MainWindow.updateComboBox( arrayVideoVBR, MainWindow.comboBoxVideoVBR );
        MainWindow.updateComboBox( arrayVideoCodec, MainWindow.comboBoxVideoCodec );

        MainWindow.updateComboBox( arrayAudioExtensions, MainWindow.comboBoxAudioExt );
        MainWindow.updateComboBox( arrayAudioChannels, MainWindow.comboBoxAudioChannels );
        MainWindow.updateComboBox( arrayAudioABR, MainWindow.comboBoxAudioABR );
        MainWindow.updateComboBox( arrayAudioASR, MainWindow.comboBoxAudioASR );
        MainWindow.updateComboBox( arrayAudioCodec, MainWindow.comboBoxAudioCodec );

        MainWindow.updateComboBox( arrayRecodeExt, MainWindow.comboBoxRecodeExt );

        MainWindow.doCascadeFilter(comboBoxVideoExt);
        MainWindow.doCascadeFilter(comboBoxAudioExt);
    }


    public static String getAdvancedSettings() {
        StringBuilder output = new StringBuilder();
        ArrayList<String> arrayListAdvancedSettings = new ArrayList<>();
        arrayListAdvancedSettings.add("--ffmpeg-location " + stringQuotes + (jarPath + fileDiv + binaryFiles[1]) + stringQuotes );
        arrayListAdvancedSettings.add("--restrict-filenames");
        arrayListAdvancedSettings.add("--progress");
        arrayListAdvancedSettings.add("--newline");
        arrayListAdvancedSettings.add("--no-mtime");
        if (debug) {
            arrayListAdvancedSettings.add("--verbose");
        }

        if (recode) {
            arrayListAdvancedSettings.add("--recode \"" + arrayRecodeExt[recodeExt] + "\"");
        }

        if (!advancedSettingsEnabled) {
            arrayListAdvancedSettings.add("--embed-thumbnail");
            arrayListAdvancedSettings.add(
                    "--convert-thumbnails \"" + arrayWriteThumbnailExt[writeThumbnailExt] + "\"");
            arrayListAdvancedSettings.add("--add-metadata");

            switch (videoAudio) {
                case 0:
                    arrayListAdvancedSettings.add("-f");
                    arrayListAdvancedSettings.add(
                            "((bv[ext=mp4][height<=1080]" + (compatibilityMode ? "[vcodec~='^((he|a)vc|h26[45])']" : "[vcodec!*=vp09]") + "+ba[ext=m4a])/(b[ext=mp4][vcodec!*=vp09])/((bv+ba)/b))");
                    break;
                case 1:
                    arrayListAdvancedSettings.add("-f");
                    arrayListAdvancedSettings.add(
                            "((bv[ext=mp4][height<=1080]" + (compatibilityMode ? "[vcodec~='^((he|a)vc|h26[45])'])" : "[vcodec!*=vp09])") + "/bv)");
                    break;
                case 2:
                    arrayListAdvancedSettings.add("-f");
                    arrayListAdvancedSettings.add(
                            "((ba[ext=m4a])/ba)" );
                    break;
            }

        } else {
            if (writeThumbnail) {
                if (embedThumbnail) {
                    arrayListAdvancedSettings.add("--embed-thumbnail");
                    arrayListAdvancedSettings.add(
                            "--convert-thumbnails \"" + arrayWriteThumbnailExt[writeThumbnailExt] + "\"");
                } else {
                    arrayListAdvancedSettings.add("--write-thumbnail");
                }
            }

            if (addMetadata) {
                arrayListAdvancedSettings.add("--add-metadata");
            }

            int keyVideo = getKey(tableMap,
                    "EXT", comboBoxVideoExt.getItemAt(videoExt),
                    "RESOLUTION", comboBoxVideoResolution.getItemAt(videoResolution),
                    "FPS", comboBoxVideoFPS.getItemAt(videoFPS),
                    "VCODEC", comboBoxVideoCodec.getItemAt(videoCodec),
                    "ACODEC", "video only"
            );
            int keyAudio = getKey(tableMap,
                    "EXT", comboBoxAudioExt.getItemAt(audioExt),
                    "CH", comboBoxAudioChannels.getItemAt(audioChannels),
                    "ABR", comboBoxAudioABR.getItemAt(audioABR),
                    "ACODEC", comboBoxAudioCodec.getItemAt(audioCodec),
                    "VCODEC", "audio only"
            );


            arrayListAdvancedSettings.add("-f");
            switch (videoAudio) {
                case 0: // video and audio
                    arrayListAdvancedSettings.add(keyVideo + "+" + keyAudio);
                    break;
                case 1: // video only
                    arrayListAdvancedSettings.add(String.valueOf(keyVideo));
                    break;
                case 2: // audio only
                    arrayListAdvancedSettings.add(String.valueOf(keyAudio));
                    break;
            }
        }

        for (String arrayListAdvancedOption : arrayListAdvancedSettings) {
            // add the options to the cmd variable
            output.append(arrayListAdvancedOption).append(" ");
        }


        return output.toString();
    }

    // Example call for getUniqueValues() method:
    //        Set<String> uniqueValues = getUniqueValues("EXT", option -> option.get("ACODEC").equals("video only"));
    //        System.out.println(uniqueValues);
    public static Set<String> getUniqueValues(String property, Predicate<Map<String, String>> filter) {
        Set<String> uniqueValues = new HashSet<>();
        for (Map<String, String> option : tableMap.values()) {
            if (filter == null || filter.test(option)) {
                uniqueValues.add(option.get(property));
            }
        }
        return uniqueValues;
    }

    //// Example call for getKey() method:
    //        int key = getKey(hashMap, "EXT", "mp4", "RESOLUTION", "854x480", "ACODEC", "video only", "VCODEC", "vp09.00.30.08" true);
    //        int key = getKey(hashMap, "EXT", "m4a", "CH", "2", "ACODEC", "mp4a.40.5", "VCODEC", "audio only", false);
    //        System.out.println(key);
    public static int getKey(
            Map<String, Map<String, String>> hashMap,
            String option1, String value1,
            String option2, String value2,
            String option3, String value3,
            String option4, String value4,
            String option5, String value5) {

        int resultKey = -1; // Initialize with an invalid key

        for (String key : hashMap.keySet()) {
            Map<String, String> innerMap = hashMap.get(key);
            // Check if conditions are met
            if (innerMap.containsKey(option1) && innerMap.containsKey(option2) && innerMap.containsKey(option3)
                    && innerMap.containsKey(option4) && innerMap.containsKey(option5)
                    && innerMap.get(option1).equals(value1) && innerMap.get(option2).equals(value2)
                    && innerMap.get(option3).equals(value3) && innerMap.get(option4).equals(value4)
                    && innerMap.get(option5).equals(value5))
            {
                resultKey = Integer.parseInt(key);
            }
        }
        return resultKey;
    }

    public static void sortArrayValues(String property, String[] arrayValues) {
        switch (property) {
            case "RESOLUTION":
                Arrays.sort(arrayValues, Comparator.comparingInt(s -> -1 * Integer.parseInt(s.split("x")[1])));
                break;
            case "FPS":
            case "CH":
                Arrays.sort(arrayValues, Comparator.comparingInt(s -> -1 * Integer.parseInt(s)));
                break;
            case "VCODEC":
            case "ACODEC":
                Arrays.sort(arrayValues,
                        Comparator.<String, String>comparing(s -> s.replaceAll("[^A-Za-z]", ""))
                                .thenComparing(s -> s.replaceAll("[^0-9]", ""),
                                        Comparator.nullsLast(Comparator.naturalOrder())).reversed());
                break;
            case "VBR":
            case "ABR":
            case "ASR":
                Arrays.sort(arrayValues,
                        Comparator.comparingInt(s -> -1 * Integer.parseInt(s.replaceAll("[^0-9]", ""))));
                break;
            case "RECODE":
                Arrays.sort(arrayValues,
                        Comparator.comparing(s -> s.replaceAll("[^A-Za-z]", "")));
                break;
        }
    }
}
