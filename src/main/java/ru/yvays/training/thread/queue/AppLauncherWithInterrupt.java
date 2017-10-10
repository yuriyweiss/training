package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AppLauncherWithInterrupt {
    private static final Logger logger = Logger.getLogger(AppLauncherWithInterrupt.class);

    private static final int CONSUMERS_COUNT = 3;

    private static ExecutorService handlerPool;
    private static List<ThreadPoolExecutor> consumerPools = new ArrayList<>();
    private static AtomicInteger processedCount = new AtomicInteger(0);

    private static List<Thread> producers = new ArrayList<>();
    private static AtomicInteger messageNumber = new AtomicInteger(0);

    public static void main(String[] args) {
        initializeThreadPools();
        startProducers();
        try {
            Thread.sleep(10000L);
            shutdownAll();
        } catch (InterruptedException e) {
            logger.info("thread pools shutdown interrupted");
            Thread.currentThread().interrupt();
        }
        logger.info("application finished work");
    }

    private static void initializeThreadPools() {
        logger.info("initializing thread pools");
        handlerPool = Executors.newFixedThreadPool(10);
        // consumer pools
        for (int i = 0; i < CONSUMERS_COUNT; i++) {
            ThreadPoolExecutor consumerPool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10000));
            consumerPool.setMaximumPoolSize(10);
            consumerPools.add(consumerPool);
        }
        logger.info("thread pools initialized");
    }

    private static void startProducers() {
        for (int i = 0; i < 1; i++) {
            Thread producerThread = new Thread(new MessageProducer(handlerPool, consumerPools, processedCount, messageNumber));
            producers.add(producerThread);
            producerThread.start();
        }
    }

    private static void shutdownAll() throws InterruptedException {
        logger.info("shutdown all");
        Thread producer = producers.get(0);
        producer.interrupt();
        while (producer.isAlive()) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        shutdownThreadPools();
    }

    private static void shutdownThreadPools() throws InterruptedException {
        shutdownThreadPool(handlerPool, "handlerPool");
        for (int i = 0; i < CONSUMERS_COUNT; i++) {
            ThreadPoolExecutor consumerPool = consumerPools.get(i);
            shutdownThreadPool(consumerPool, "consumerPool[" + i + "]");
        }
    }

    private static void shutdownThreadPool(ExecutorService threadPool, String poolName) throws InterruptedException {
        logger.info("shutting down " + poolName);
        threadPool.shutdownNow();
        logger.info("waiting " + poolName + " termination");
        threadPool.awaitTermination(10000L, TimeUnit.SECONDS);
        logger.info("SUCCESS " + poolName + " shutdown");
    }
}
