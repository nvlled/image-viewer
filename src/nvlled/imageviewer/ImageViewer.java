package nvlled.imageviewer;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.nio.file.FileSystems;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.regex.*;

public class ImageViewer extends JFrame {
    private static final int DEFAULT_SCROLL_STEP = 50;

    private String imageDir;
    private String[] filenames;

    private Point scrollOffset;
    private JScrollPane scrollPane;
    private ImagePanel currentImage;
    int scrollStep = DEFAULT_SCROLL_STEP;

    private int imgIndex = 0;

    private IOException lastError;

    private Pattern imageFilenamePattern =
        Pattern.compile("^.+\\.(jpe?g|png|bmp|gif)$");

    public ImageViewer(String imageDir) throws IOException {
        this.imageDir = imageDir;
        File file = new File(imageDir);
        if (!file.isDirectory()) {
            throw new IOException(imageDir + " is not a directory");
        }
        filenames = listImages(file);

        currentImage = new ImagePanel();
        scrollPane = new JScrollPane(currentImage);
        JViewport vport = scrollPane.getViewport();
        scrollOffset = vport.getViewPosition();

        add(scrollPane);
    }

    public ImageViewer() throws IOException {
        this(".");
    }

    public void loadCurrent() {
        setCurrentImage(imgIndex);
    }

    public boolean setCurrentImage(int index) {
        if (index >= 0 && index < filenames.length) {
            String filename = filenames[index];
            File file = FileSystems
                    .getDefault()
                    .getPath(imageDir, filename)
                    .toFile();
            try {
                setImage(null);
                repaint();

                Image img = ImageIO.read(file);
                setImage(img);
            } catch (IOException e) {
                lastError = e;
            }

            return true;
        }
        return false;
    }

    public void setImage(Image img) {
        currentImage.setImage(img);
        currentImage.revalidate();
        scrollPane.repaint();
    }

    public void firstImage() { setCurrentImage(0); }
    public void lastImage() { setCurrentImage(filenames.length-1); }

    public void nextImage() {
        if (imgIndex < filenames.length) {
            imgIndex++;
            loadCurrent();
        }
    }

    public void prevImage() {
        if (imgIndex > 0) {
            imgIndex--;
            loadCurrent();
        }
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

    private String[] listImages(File file) {
        File[] files = file.listFiles(new FileFilter() {
            Pattern pat = imageFilenamePattern;
            public boolean accept(File file) {
                boolean isImage = pat.matcher(file.getName()).matches();
                return file.isFile() && isImage;
            }
        });
        String[] filenames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            System.out.println(">" + files[i].getName());
            filenames[i] = files[i].getName();
        }

        return filenames;
    }
}
