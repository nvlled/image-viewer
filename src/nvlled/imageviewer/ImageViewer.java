package nvlled.imageviewer;

import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.awt.*;

public class ImageViewer extends JFrame {
    private static final int DEFAULT_SCROLL_STEP = 50;

    private ImagePanel currentImage;
    private JScrollPane scrollPane;
    private Point scrollOffset;
    int scrollStep = DEFAULT_SCROLL_STEP;

    public ImageViewer() { }

    public void setCurrentImage(Image img) {
        currentImage = new ImagePanel(img);
        scrollPane = new JScrollPane(currentImage);
        add(scrollPane);

        JViewport vport = scrollPane.getViewport();
        scrollOffset = vport.getViewPosition();
    }

    public void zoomIn() {
        if (currentImage != null) {
            currentImage.zoomIn();
            currentImage.revalidate();
            scrollPane.repaint();
        }
    }

    public void zoomOut() {
        if (currentImage != null) {
            currentImage.zoomOut();
            currentImage.revalidate();
            scrollPane.repaint();
        }
    }

    public void scrollLeft() { scroll(-scrollStep, 0); }
    public void scrollRight() { scroll(scrollStep, 0); }
    public void scrollUp() { scroll(0, -scrollStep); }
    public void scrollDown() { scroll(0, scrollStep); }

    public void scroll(int dx, int dy) {
        JViewport vport = scrollPane.getViewport();

        System.out.println(vport.getExtentSize());
        System.out.println(vport.getViewRect());
        System.out.println(vport.getViewSize());

        scrollOffset.translate(dx, dy);
        constrainBounds(scrollOffset);

        vport.setViewPosition(scrollOffset);
        scrollPane.repaint();
    }

    private void constrainBounds(Point p) {
        JViewport vport = scrollPane.getViewport();
        Dimension viewSize = vport.getViewSize();
        Dimension extSize = vport.getExtentSize();

        if (p.x < 0) {
            p.x = 0;
        } else if (p.getX()+extSize.getWidth() > viewSize.getWidth()) {
            p.x = (int) (viewSize.getWidth() - extSize.getWidth());
        }
        if (p.y < 0) {
            p.y = 0;
        } else if (p.getY()+extSize.getHeight() > viewSize.getHeight()) {
            p.y = (int) (viewSize.getHeight() - extSize.getHeight());
        }
    }
}
