package com.blastlong.pmh.settings;

import java.io.*;


public class SettingsManager {

    private static final String baseDirectory = "PMH";
    private static final String settingsFileName = "settings.ser";

    public static final float[] screenMagnificationPhases = new float[]{
            0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f
    };

    private String directory;
    private String settingsFilePath;

    private Settings settings;

    public SettingsManager() {
        directory = System.getProperty("user.dir") + File.separator + baseDirectory;
        settingsFilePath = directory + File.separator + settingsFileName;
        loadSettings();
    }

    public void changeScreenMagnification() {
        int index = -9999;
        for(int i = 0; i < screenMagnificationPhases.length; i++) {
            float phase = screenMagnificationPhases[i];
            if (Math.abs(settings.screenMagnification - phase) < 0.01f) {
                index = i;
                break;
            }
        }
        index += 1;
        if (index >= screenMagnificationPhases.length - 1)
            index = 0;
        settings.screenMagnification = screenMagnificationPhases[index];
        saveSettings(settings);
    }

    public void initializeSettings() {
        settings = new Settings();
        saveSettings(settings);
    }

    public void loadSettings() {
        File file = new File(settingsFilePath);
        if (!file.exists()) {
            initializeSettings();
        }

        try (FileInputStream fileIn = new FileInputStream(settingsFilePath);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            settings = (Settings) in.readObject();
        } catch (IOException | ClassNotFoundException i) {
            initializeSettings();
            i.printStackTrace();
        }
    }

    public void saveSettings(Settings settings) {
        this.settings = settings;

        File directoryFile = new File(directory);
        if(!directoryFile.exists())
            directoryFile.mkdirs();

        try (FileOutputStream fileOut = new FileOutputStream(settingsFilePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this.settings);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public Settings getSettings() {
        return settings;
    }
}
