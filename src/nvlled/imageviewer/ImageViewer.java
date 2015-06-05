package nvlled.imageviewer;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.nio.file.FileSystems;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.regex.*;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class ImageViewer extends JFrame {
    private static final int DEFAULT_SCROLL_STEP = 50;

    private String[] filenames;

    private Point scrollOffset;
    private JScrollPane scrollPane;
    private ImagePanel currentImage;
    private JLabel statusMessage;

    int scrollStep = DEFAULT_SCROLL_STEP;
    private int imgIndex = 0;

    private ImageLoader imageLoader;

    private Executor exec;

    public ImageViewer() {
        exec = Executors.newSingleThreadExecutor();

        currentImage = new ImagePanel();
        scrollPane = new JScrollPane(currentImage);
        JViewport vport = scrollPane.getViewport();
        scrollOffset = vport.getViewPosition();

        statusMessage = new JLabel(" ");

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(statusMessage, BorderLayout.SOUTH);

        ViewerActions actions = new ViewerActions(this);

        setupInput(actions);
        setupToolBar(actions);
        setupMenuBar(actions);

        imageLoader = new ImageLoader();
        filenames = new String[] {};
    }

    public void openFile(String filename) throws IOException {
        imageLoader.clear();
        filenames = new String[] { filename };
        loadCurrent();
    }

    public void openDirectory(File file) throws IOException {
        if (!file.isDirectory()) {
            throw new IOException(file.toString() + " is not a directory");
        }
        filenames = listImages(file);
        loadCurrent();
        imgIndex = 0;
    }

    public void openDirectory(String imageDir) throws IOException {
        imageLoader.clear();
        File file = new File(imageDir);
        openDirectory(file);
    }

    private void setupInput(ViewerActions actions) {
        InputMap imap = currentImage.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap amap = currentImage.getActionMap();

        // tuples would be nice
        Object[][] entries = new Object[][] {
            new Object[]{"typed n", "viewer.next", actions.nextImage},
            new Object[]{"SPACE", "viewer.next", actions.nextImage},
            new Object[]{"typed p", "viewer.prev", actions.prevImage},
            new Object[]{"typed -", "viewer.zoomOut", actions.zoomOut},
            new Object[]{"typed =", "viewer.zoomIn", actions.zoomIn},
            new Object[]{"typed +", "viewer.zoomIn", actions.zoomIn},

            new Object[]{"typed k", "viewer.scrollUp", actions.scrollUp},
            new Object[]{"typed j", "viewer.scrollDown", actions.scrollDown},
            new Object[]{"typed h", "viewer.scrollLeft", actions.scrollLeft},
            new Object[]{"typed l", "viewer.scrollRight", actions.scrollRight},

            new Object[]{"released UP", "viewer.scrollUp", actions.scrollUp},
            new Object[]{"released DOWN", "viewer.scrollDown", actions.scrollDown},
            new Object[]{"released LEFT", "viewer.scrollLeft", actions.scrollLeft},
            new Object[]{"released RIGHT", "viewer.scrollRight", actions.scrollRight},
        };

        for (Object[] entry: entries) {
            String key = (String) entry[0];
            String name = (String) entry[1];

            imap.put(KeyStroke.getKeyStroke(key), name);
            amap.put(name, (Action) entry[2]);
        }
    }

    private void setupToolBar(ViewerActions actions) {
        JToolBar bar = new JToolBar();
        bar.add(actions.openFile);
        bar.add(actions.prevImage);
        bar.add(actions.nextImage);
        bar.add(actions.zoomOut);
        bar.add(actions.zoomIn);
        add(bar, BorderLayout.NORTH);
    }

    private void setupMenuBar(ViewerActions actions) {
        JMenuBar mbar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(actions.openFile);
        fileMenu.add(actions.exit);
        mbar.add(fileMenu);
        setJMenuBar(mbar);
    }

    public void loadCurrent() {
        loadImage(imgIndex);
    }

    public String getFilename(int index) {
        if (index >= 0 && index < filenames.length) {
            return filenames[index];
        }
        return "";
    }

    public void loadImage(final int index) {
        final JFrame that = this;
        exec.execute(new Runnable() {
            public void run() {
                Exception lastError = null;
                try {
                    setStatusMessage("loading image...");
                    String filename = getFilename(index);
                    if (filename == "") {
                        lastError = new Exception("No image found");
                    } else {
                        Image img = imageLoader.load(filename);
                        imageLoader.preload(getFilename(index-1));
                        imageLoader.preload(getFilename(index+1));

                        setImage(img);
                        clearStatusMessage();
                    }
                } catch (ImageLoader.InvalidImage e) {
                    lastError = e;
                } catch (IOException e) {
                    lastError = e;
                }

                if (lastError != null) {
                    setImage(null);
                    String msg = lastError.getMessage();
                    setStatusMessage("Image read failed: " + msg);
                    JOptionPane.showMessageDialog(that, msg);
                }
            }
        });
    }

    private void setStatusMessage(String msg) {
        statusMessage.setText(msg);
        statusMessage.repaint();
    }

    private void clearStatusMessage() { setStatusMessage(" "); }

    public void setImage(Image img) {
        currentImage.setImage(img);
        redraw();
    }

    public void redraw() {
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
            redraw();
        }
    }

    public void zoomOut() {
        if (currentImage != null) {
            currentImage.zoomOut();
            redraw();
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
            System.out.println(">" + files[i].toString());
            filenames[i] = files[i].getAbsolutePath();
        }

        return filenames;
    }
}
