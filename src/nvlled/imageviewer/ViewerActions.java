package nvlled.imageviewer;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import static javax.swing.Action.*;

public class ViewerActions {
    ImageViewer imageViewer;

    public ViewerActions(ImageViewer viewer) {
        imageViewer = viewer;
    }

    Action exit = new Abstraction(
        new ActionProps()
            .property(NAME, "Exit")
            .property(SMALL_ICON, new ImageIcon("icons/exit.png"))
            ,
        new ActionListener() {
            // teh bloat is inevitable
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }
    );

    Action prevImage = new Abstraction(
        new ActionProps()
            .property(NAME, "Previous Image")
            .property(SMALL_ICON, new ImageIcon("icons/prev-image.png"))
            ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { imageViewer.prevImage(); }
        }
    );

    Action nextImage = new Abstraction(
        new ActionProps()
            .property(NAME, "Next Image")
            .property(SMALL_ICON, new ImageIcon("icons/next-image.png"))
            ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { imageViewer.nextImage(); }
        }
    );

    Action zoomOut = new Abstraction(
        new ActionProps()
            .property(NAME, "Zoom-in")
            .property(SMALL_ICON, new ImageIcon("icons/zoom-out.png"))
            ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { imageViewer.zoomOut(); }
        }
    );

    Action zoomIn = new Abstraction(
        new ActionProps()
            .property(NAME, "Zoom-out")
            .property(SMALL_ICON, new ImageIcon("icons/zoom-in.png"))
            ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { imageViewer.zoomIn(); }
        }
    );

    Action openFile = new Abstraction(
        new ActionProps()
            .property(NAME, "Open file or directory")
            .property(SMALL_ICON, new ImageIcon("icons/open-file.png"))
            ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(".");
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                // TODO: filter files to images and directories
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
        }
    );

    Action scrollUp = new Abstraction(
        new ActionProps() ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { imageViewer.scrollUp(); }
        }
    );

    Action scrollDown = new Abstraction(
        new ActionProps() ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { imageViewer.scrollDown(); }
        }
    );

    Action scrollLeft = new Abstraction(
        new ActionProps() ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { imageViewer.scrollLeft(); }
        }
    );

    Action scrollRight = new Abstraction(
        new ActionProps() ,
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { imageViewer.scrollRight(); }
        }
    );

    class Abstraction extends AbstractAction {
        ActionProps properties;
        ActionListener handler;

        public Abstraction(ActionProps props, ActionListener al) {
            properties = props;
            handler = al;
        }

        @Override
        public Object getValue(String key) {
            Object val = properties.get(key);
            if (val != null)
                return val;
            return super.getValue(key);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handler.actionPerformed(e);
        }
    }

    class ActionProps extends HashMap<String, Object> {
        public ActionProps property(String key, Object value) {
            put(key, value);
            return this;
        }
    }
}
