package nvlled.imageviewer;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ViewerActions {
    ImageViewer imageViewer;

    Action openFile =  new AbstractAction() {
        @Override
        public Object getValue(String key) {
            if (key == Action.NAME)
                return "Open file or directory";
            return super.getValue(key);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(".");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int choice = chooser.showOpenDialog(imageViewer);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    if (file.isDirectory()) {
                        imageViewer.openDirectory(file);
                    } else {
                        imageViewer.openFile(file.getAbsolutePath());
                    }
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(imageViewer, "nope");
                }
            }
        }
    };

    Action exit =  new AbstractAction() {
        @Override
        public Object getValue(String key) {
            if (key == Action.NAME)
                return "Exit";
            return super.getValue(key);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    public ViewerActions(ImageViewer viewer) {
        imageViewer = viewer;
    }
}
