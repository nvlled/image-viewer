package nvlled.imageviewer;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private static final double DEFAULT_ZOOM_STEP = 0.1;

    double scale = 1;
    double zoomStep = DEFAULT_ZOOM_STEP;

    Image image;
    int width = 0;
    int height = 0;
    Dimension dimension;


    public ImagePanel() {
        dimension = new Dimension();
    }

    public ImagePanel(Image img) {
        setImage(img);
    }


    public void setImage(Image image) {
        this.image = image;
        if (image != null) {
            width = image.getWidth(null)+1;
            height = image.getHeight(null)+1;
            dimension = new Dimension(width, height);
            setPreferredSize(dimension);
        }
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
        if (image == null)
            return;
        Graphics2D g = (Graphics2D) g_;
        int w = getWidth();
        int h = getHeight();
        g.drawImage(image, 0, 0, w, h, Color.WHITE, null);
    }
}
