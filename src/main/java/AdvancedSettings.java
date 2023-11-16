package main.java;

import java.util.*;
import java.util.function.Predicate;

import static main.java.MainWindow.*;
import static main.java.TableReaderFromConsole.scannerTableMap;

public class AdvancedSettings {


    protected static Map<String, Map<String, String>> tableMap; // the table of video options (--list-formats)
    protected static int videoAudio = 0; // 0 = video and audio, 1 = audio only, 2 = video only
    protected static boolean advancedSettingsEnabled = false; // if the advanced options are enabled
    protected static volatile boolean getVideoOptions = false; // if the video options are enabled
    protected static int videoExt = 0; // the video format to download
    protected static int videoResolution = 0; // the video resolution to download
    protected static int videoCodec = 0; // the video codec to download
    protected static int videoFPS = 0; // the video FPS to download
    protected static int audioExt = 0; // the audio format to download
    protected static int audioChannels = 0; // the audio channels to download
    protected static int audioCodec = 0; // the audio codec to download
    protected static int audioASR = 0; // the audio sample rate to download
    protected static String[] arrayVideoExtensions = new String[] {""};
    protected static String[] arrayVideoResolution = new String[] {""};
    protected static String[] arrayVideoCodec = new String[] {""};
    protected static String[] arrayVideoFPS = new String[] {""};

    protected static String[] arrayAudioExtensions = new String[] {""};
    protected static String[] arrayAudioChannels = new String[] {""};
    protected static String[] arrayAudioCodec = new String[] {""};

    protected static String[] arrayAudioASR = new String[] {""};


    public static void readVideoOptionsFromYT() {
        // get the video options from the URL
        advancedSettingsEnabled = true;
        getVideoOptions();
    }

    private static void getVideoOptions() {
        String cmd = MainWorker.downloadBinary + "--list-formats " + MainWorker.rawURL;
        //System.out.println("command to run: " + cmd);
        try {
            new Thread(()->{
                ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
                try {
                    Process p = pb.start();
                    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                    Scanner scanner = new Scanner(p.getInputStream());
                    scannerTableMap(scanner);
                    p.waitFor();
                    //System.out.println("EOI");
                    getVideoOptions = true;
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static void setadvancedSettings() {
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

        arrayAudioASR =
                getUniqueValues( "ASR", option -> option.get("CH").matches("[0-9]")
                                && option.get("VCODEC").equals("audio only") ).toArray(new String[0]);
        sortArrayValues("ASR", arrayAudioASR);



        // Update the GUI
        MainWindow.updateComboBox( arrayVideoExtensions, comboBoxVideoExt );
        MainWindow.updateComboBox( arrayVideoResolution, MainWindow.comboBoxVideoResolution );
        MainWindow.updateComboBox( arrayVideoCodec, MainWindow.comboBoxVideoCodec );
        MainWindow.updateComboBox( arrayVideoFPS, MainWindow.comboBoxVideoFPS );

        MainWindow.updateComboBox( arrayAudioExtensions, MainWindow.comboBoxAudioExt );
        MainWindow.updateComboBox( arrayAudioChannels, MainWindow.comboBoxAudioChannels );
        MainWindow.updateComboBox( arrayAudioCodec, MainWindow.comboBoxAudioCodec );
        MainWindow.updateComboBox( arrayAudioASR, MainWindow.comboBoxAudioASR );
        MainWindow.doCascadeFilter(comboBoxVideoExt);
        MainWindow.doCascadeFilter(comboBoxAudioExt);
    }


    public static String getadvancedSettings() {
        StringBuilder output = new StringBuilder();
        ArrayList<String> arrayListadvancedSettings = new ArrayList<>();
        arrayListadvancedSettings.add("--restrict-filenames");
        arrayListadvancedSettings.add("-f");

        if (!advancedSettingsEnabled) {
            switch (videoAudio) {
                case 0:
                    arrayListadvancedSettings.add("\"bv*[ext=mp4]+ba[ext=m4a]/b[ext=mp4] / bv*+ba/b\"");
                    break;
                case 1:
                    arrayListadvancedSettings.add("\"bv[ext=mp4]\"");
                    break;
                case 2:
                    arrayListadvancedSettings.add("\"ba[ext=m4a]\"");
                    break;
            }
        } else {
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
                    "ASR", comboBoxAudioASR.getItemAt(audioASR),
                    "ACODEC", comboBoxAudioCodec.getItemAt(audioCodec),
                    "VCODEC", "audio only"
            );

            switch (videoAudio) {
                case 0: // video and audio
                    arrayListadvancedSettings.add(keyVideo + "+" + keyAudio);
                    break;
                case 1: // video only
                    arrayListadvancedSettings.add(String.valueOf(keyVideo));
                    break;
                case 2: // audio only
                    arrayListadvancedSettings.add(String.valueOf(keyAudio));
                    break;
            }
        }

        for (String arrayListAdvancedOption : arrayListadvancedSettings) {
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
                System.out.println("key: " + key);
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

            case "ASR":
                Arrays.sort(arrayValues,
                        Comparator.comparingInt(s -> -1 * Integer.parseInt(s.replaceAll("k", ""))));
                break;
        }
    }
}
