package nvlled.imageviewer;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.event.*;
import java.util.concurrent.*;

// TODO: Improve image loading
public class Main {
    private static ImageViewer imgViewer;
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    imgViewer = new ImageViewer();
                } catch (IOException e) {
                    throw new RuntimeException("fuck you");
                }
            }
        });
        imgViewer.loadCurrent();

        imgViewer.setLocationByPlatform(true);
        imgViewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
        imgViewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imgViewer.setVisible(true);

        final Executor exec = Executors.newCachedThreadPool();

        imgViewer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String kcode = KeyEvent.getKeyText(e.getKeyCode());
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_N:
                    case KeyEvent.VK_SPACE:
                        exec.execute(new Runnable() {
                            public void run() { imgViewer.nextImage(); }
                        });
                        break;
                    case KeyEvent.VK_P:
                        exec.execute(new Runnable() {
                            public void run() { imgViewer.prevImage(); }
                        });
                        break;

                    case KeyEvent.VK_EQUALS:
                        imgViewer.zoomIn();
                        break;
                    case KeyEvent.VK_MINUS:
                        imgViewer.zoomOut();
                        break;
                    case KeyEvent.VK_UP:
                        imgViewer.scrollUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        imgViewer.scrollDown();
                        break;
                    case KeyEvent.VK_RIGHT:
                        imgViewer.scrollRight();
                        break;
                    case KeyEvent.VK_LEFT:
                        imgViewer.scrollLeft();
                        break;
                }
            }
        });
    }
}
