package main.java;

import javax.swing.*;
import java.awt.*;

public class FileChooser extends JFileChooser {
    private final Font font = new Font(MainWindow.fontName,Font.PLAIN,16);
    public FileChooser() {
        super();
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        this.setAcceptAllFileFilterUsed(false);

        this.setDialogTitle("Select a folder to download to:");

        this.setApproveButtonText("Select");

        this.setMultiSelectionEnabled(false);

        this.setCurrentDirectory(new java.io.File(MainWorker.downloadDirectoryPath));

        this.setFileHidingEnabled(true);

        this.setPreferredSize(new Dimension(600, 450));


        setFileChooserFont(this.getComponents());

        this.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Directory (Folder)";
            }
        });

        MainWindow.setHandCursorToClickableComponents(this);
    }

    private void setFileChooserFont(Component[] comp) {
        for (Component component : comp) {
            if (component instanceof Container) setFileChooserFont(((Container) component).getComponents());
            try {
                component.setFont(font);
            } catch (Exception ignored) {}
        }
    }
}
