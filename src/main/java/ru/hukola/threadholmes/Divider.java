package ru.hukola.threadholmes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Babin Nikolay
 */
public class Divider implements Runnable{
    private final String POISON;
    private final BlockingQueue<Path> sourceQueue;
    private final BlockingQueue<List<String>> destinationQueue;
    private final int CHUNK_SIZE;

    public Divider(BlockingQueue<Path> sourceQueue,
                   BlockingQueue<List<String>> destinationQueue,
                   String POISON,
                   int CHUNK_SIZE) {
        this.sourceQueue = sourceQueue;
        this.destinationQueue = destinationQueue;
        this.POISON = POISON;
        this.CHUNK_SIZE = CHUNK_SIZE;
    }

    @Override
    public void run() {
        System.out.println("Divider: started");
        try {
            Path file = sourceQueue.take();
            System.out.println("Divider: File taken");
            Stream<String> lines = Files.lines(file);
            Stream<List<String>> chunked = chunked(lines, CHUNK_SIZE);
            chunked.forEach(e -> {
                try {
                    destinationQueue.put(e);
                    System.out.println("Divider: put chunk lines");
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });
            destinationQueue.put(List.of(POISON));
        } catch (InterruptedException e) {
            System.out.println("Divider: Interrupted divider");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Divider: stopped");
    }

    public static <T> Stream<List<T>> chunked(Stream<T> stream, int chunkSize) {
        AtomicInteger index = new AtomicInteger(0);
        return stream.collect(Collectors.groupingBy(x -> index.getAndIncrement() / chunkSize))
                .entrySet().stream().map(Map.Entry::getValue);
    }
}
