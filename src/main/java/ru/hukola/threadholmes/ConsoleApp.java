package ru.hukola.threadholmes;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Babin Nikolay
 * 1. download file
 * 2. divide into chunks
 * 3. chunks put to concurent map
 * 4. consumers get from map
 * 5. consumers calculate statistic and put to map result
 */
public class ConsoleApp {
    private static final String POISON = "STOP";
    private static final int CALCULATE_THREADS_COUNT = 50;
    private static final int CHUNK_SIZE = 100;
    private final BlockingQueue<Path> file = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<List<String>> strings = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();

    private final String FILE_SOURCE = "https://norvig.com/big.txt";

    public void run() throws MalformedURLException, InterruptedException {

        final ExecutorService downloader = Executors.newSingleThreadExecutor();
        URL url = new URL(FILE_SOURCE);
        downloader.execute(new Downloader(url, file));

        final ExecutorService divider = Executors.newSingleThreadExecutor();
        divider.execute(new Divider(file, strings, POISON, CHUNK_SIZE));

        final ExecutorService calculator = Executors.newFixedThreadPool(CALCULATE_THREADS_COUNT);
        for (int i = 0; i < CALCULATE_THREADS_COUNT; i++) {
            calculator.submit(new Calculator(strings, result, POISON));
        }


        downloader.shutdown();
        divider.shutdown();
        calculator.shutdown();
        boolean done = calculator.awaitTermination(120, TimeUnit.SECONDS);

        List<Map.Entry<String, Integer>> collect = result.entrySet().stream()
                .sorted((e, ed) -> Integer.compare(ed.getValue(), e.getValue()))
                .limit(30)
                .collect(Collectors.toList());
        System.out.println(collect);

    }
}
