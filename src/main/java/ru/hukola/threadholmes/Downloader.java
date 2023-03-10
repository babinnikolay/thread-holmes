package ru.hukola.threadholmes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

/**
 * @author Babin Nikolay
 */
public class Downloader implements Runnable{
    private final BlockingQueue<Path> file;
    private final URL url;
    private final String FILE_DESTINATION = "bigfile.txt";

    public Downloader(URL url, BlockingQueue file) {
        this.file = file;
        this.url = url;
    }

    @Override
    public void run() {
        System.out.println("Downloader: started");
        Path path = Paths.get(FILE_DESTINATION);
        if (Files.exists(path)) {
            System.out.println("Downloader: file exists, skip downloading");
            try {
                file.put(path);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream fos = new FileOutputStream(FILE_DESTINATION)) {
            System.out.println("Downloader: downloading file...");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            file.put(path);
            System.out.println("Downloader: stopped");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
