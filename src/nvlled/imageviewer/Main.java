package nvlled.imageviewer;

import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Image img = ImageIO.read(new File("test.png"));

        // TODO: use invokeLater thingy
        final ImageViewer imgViewer = new ImageViewer();
        imgViewer.setCurrentImage(img);

        imgViewer.setVisible(true);
        imgViewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
        imgViewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imgViewer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String kcode = KeyEvent.getKeyText(e.getKeyCode());
                switch (e.getKeyCode()) {
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
