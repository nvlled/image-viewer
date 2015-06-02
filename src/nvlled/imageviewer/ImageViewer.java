package nvlled.imageviewer;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.nio.file.FileSystems;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.regex.*;
import java.nio.file.Paths;

public class ImageViewer extends JFrame {
    private static final int DEFAULT_SCROLL_STEP = 50;

    private String imageDir;
    private String[] filenames;

    private Point scrollOffset;
    private JScrollPane scrollPane;
    private ImagePanel currentImage;
    private JLabel statusMessage;

    int scrollStep = DEFAULT_SCROLL_STEP;
    private int imgIndex = 0;

    private IOException lastError;
    private ImageLoader imageLoader;

    public ImageViewer(String imageDir) throws IOException {
        currentImage = new ImagePanel();
        scrollPane = new JScrollPane(currentImage);
        JViewport vport = scrollPane.getViewport();
        scrollOffset = vport.getViewPosition();

        statusMessage = new JLabel(" ");

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(statusMessage, BorderLayout.SOUTH);

        setupMenuBar();

        openDirectory(imageDir);
    }

    public void openFile(String filename) throws IOException {
        imageDir = "";
        imageLoader = new ImageLoader();
        filenames = new String[] { filename };
        loadCurrent();
    }

    public void openDirectory(File file) throws IOException {
        if (!file.isDirectory()) {
            throw new IOException(imageDir + " is not a directory");
        }
        this.imageDir = file.getAbsolutePath();
        filenames = listImages(file);
        loadCurrent();
    }

    public void openDirectory(String imageDir) throws IOException {
        imageLoader = new ImageLoader();
        File file = new File(imageDir);
        openDirectory(file);
    }

    private void setupMenuBar() {
        ViewerActions actions = new ViewerActions(this);

        JMenuBar mbar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(actions.openFile);
        fileMenu.add(actions.exit);
        mbar.add(fileMenu);
        setJMenuBar(mbar);
    }

    public ImageViewer() throws IOException {
        this(".");
    }

    public void loadCurrent() {
        loadImage(imgIndex);
    }

    public String getFilename(int index) {
        if (index >= 0 && index < filenames.length) {
            return Paths.get(imageDir, filenames[index]).toString();
        }
        return "";
    }

    public boolean loadImage(int index) {
        try {
            setStatusMessage("loading image...");
            Image img = imageLoader.load(getFilename(index));
            imageLoader.preload(getFilename(index-1));
            imageLoader.preload(getFilename(index+1));

            setImage(img);
            clearStatusMessage();
        } catch (IOException e) {
            lastError = e;
            setStatusMessage(getFilename(index) + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    private void setStatusMessage(String msg) {
        statusMessage.setText(msg);
        statusMessage.repaint();
    }

    private void clearStatusMessage() { setStatusMessage(" "); }

    public void setImage(Image img) {
        currentImage.setImage(img);
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    public void firstImage() { loadImage(0); }
    public void lastImage() { loadImage(filenames.length-1); }

    public void nextImage() {
        if (imgIndex < filenames.length - 1) {
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
        final Pattern filePattern  = Pattern.compile("^.+\\.(jpe?g|png|bmp|gif)$");

        File[] files = file.listFiles(new FileFilter() {
            public boolean accept(File file) {
                boolean isImage = filePattern.matcher(file.getName()).matches();
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
