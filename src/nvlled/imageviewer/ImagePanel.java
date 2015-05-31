package nvlled.imageviewer;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    Image image;

    double scale;
    double zoomStep;
    int width;
    int height;
    Dimension dimension;

    public ImagePanel(Image img) {
        image = img;
        scale = 1;
        zoomStep = 0.1;
        width = img.getWidth(null)+1;
        height = img.getHeight(null)+1;
        dimension = new Dimension(width, height);
        setPreferredSize(dimension);
    }

    public int getWidth() {
        return (int) (width*scale);
    }

    public int getHeight() {
        return (int) (height*scale);
    }

    public void zoom(double step) {
        scale += step;
        if (scale < 0) {
            scale = 0;
        }
        int w = getWidth();
        int h = getHeight();
        dimension.setSize(w, h);
    }

    public void zoomIn()  { zoom(zoomStep); }
    public void zoomOut() { zoom(-zoomStep); }

    @Override
    public void paintComponent(Graphics g_) {
        Graphics2D g = (Graphics2D) g_;
        int w = getWidth();
        int h = getHeight();
        g.drawImage(image, 0, 0, w, h, Color.WHITE, null);
    }
}
