package nvlled.imageviewer;

import java.io.*;
import java.awt.*;
import javax.imageio.*;
import java.util.concurrent.*;
import java.util.*;

public class ImageLoader {
    private int limitSize = 5;

    private Map<String, Image> cache;
    private Queue<String> filenames;
    private LinkedBlockingQueue<String> loadQueue;

    public ImageLoader() {
        cache = new ConcurrentHashMap<String, Image>();
        loadQueue = new LinkedBlockingQueue<String>();
        filenames = new ConcurrentLinkedQueue<String>();
        startLoader();
    }

    private void startLoader() {
        Executor exec = Executors.newCachedThreadPool();
        exec.execute(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        String filename = loadQueue.take(); // should be blocking
                        readImage(filename);
                    } catch (InterruptedException e) {
                        break;
                    } catch (IOException e) {
                        /* ignore */
                    }
                }
            }
        });
    }

    public Image load(String filename) throws IOException {
        Image image = cache.get(filename);
        if (image == null) {
            image = readImage(filename);
        }
        return image;
    }

    private Image readImage(String filename) throws IOException {
        Image image = cache.get(filename);
        if (image == null) {
            image = ImageIO.read(new File(filename));
            System.out.println("read image " + filename);
            cache.put(filename, image);
            filenames.add(filename);
            if (limitSize < cache.size()) {
                String delname = filenames.poll();
                if (delname != null)
                    cache.remove(delname);
            }
        }
        return image;
    }

    public void preload(String filename) {
        loadQueue.add(filename);
    }
}
