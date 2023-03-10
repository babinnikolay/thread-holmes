package ru.hukola.threadholmes;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Babin Nikolay
 */
public class Calculator implements Callable<Void> {
    private final BlockingQueue<List<String>> sourceQueue;
    private final ConcurrentHashMap<String, Integer> globalResult;
    private final String POISON;

    public Calculator(BlockingQueue<List<String>> sourceQueue, ConcurrentHashMap<String, Integer> globalResult,
                      String POISON) {
        this.sourceQueue = sourceQueue;
        this.globalResult = globalResult;
        this.POISON = POISON;
    }

    @Override
    public Void call() {
        System.out.println("Calculator: <" + Thread.currentThread().getName() + "> started");

        while (!Thread.interrupted()) {
            try {
                List<String> strings = sourceQueue.take();
                if (strings.size() > 0 && strings.get(0).equals(POISON)) {
                    System.out.println("Calculator: <" + Thread.currentThread().getName() + "> got POISON");
                    sourceQueue.put(strings);
                    Thread.currentThread().interrupt();
                    break;
                }
                System.out.println("Calculator: <" + Thread.currentThread().getName() + "> got list for job");

                for (String string : strings) {
                    String[] words = string.trim().split("\s");
                    for (String word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        synchronized (globalResult) {
                            if (globalResult.computeIfPresent(word, (k, v) -> v + 1) == null) {
                                globalResult.put(word, 1);
                            }
                        }
                    }
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
