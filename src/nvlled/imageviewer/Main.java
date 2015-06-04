package nvlled.imageviewer;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.event.*;
import java.util.concurrent.*;

public class Main {
    private static ImageViewer imgViewer;

    private static Dimension getScreenSize() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        return kit.getScreenSize();
    };

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    imgViewer = new ImageViewer();
                } catch (IOException e) {
                    throw new RuntimeException("censored message");
                }

                Dimension screen = getScreenSize();
                imgViewer.setSize((int) screen.getWidth()*3/4, (int) screen.getHeight()*3/4);

                Toolkit kit = Toolkit.getDefaultToolkit();
                imgViewer.setIconImage(kit.getImage("icons/logo.png"));

                imgViewer.setLocationByPlatform(true);
                imgViewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                imgViewer.setVisible(true);

            }
        });
        imgViewer.openDirectory("images/");
    }
}
