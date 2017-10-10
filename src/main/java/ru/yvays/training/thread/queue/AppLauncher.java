package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AppLauncher {
    private static final Logger logger = Logger.getLogger(AppLauncher.class);

    private static final int MESSAGES_COUNT = 35;
    private static final int CONSUMERS_COUNT = 3;

    private static ExecutorService handlerPool;
    private static List<ThreadPoolExecutor> consumerPools = new ArrayList<>();
    private static Map<ThreadPoolExecutor, Long> consumerMinDelays = new HashMap<>();
    private static AtomicInteger processedCount = new AtomicInteger(0);

    public static void main(String[] args) {
        initializeThreadPools();
        sendNewMessagesToProcessing();
        try {
            waitUntilAllProcessed();
            shutdownThreadPools();
        } catch (InterruptedException e) {
            logger.debug("thread pools shutdown interrupted");
            Thread.currentThread().interrupt();
        }
        logger.debug("application finished work");
    }

    private static void initializeThreadPools() {
        logger.debug("initializing thread pools");
        handlerPool = Executors.newFixedThreadPool(10);
        // consumer pools
        for (int i = 0; i < CONSUMERS_COUNT; i++) {
            ThreadPoolExecutor consumerPool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000));
            consumerPool.setMaximumPoolSize(10);
            consumerMinDelays.put(consumerPool, (i + 1) * 100L);
            consumerPools.add(consumerPool);
        }
        logger.debug("thread pools initialized");
    }

    private static void sendNewMessagesToProcessing() {
        logger.debug("sending messages to handlers");
        for (int i = 0; i < MESSAGES_COUNT; i++) {
            MessageHandler messageHandler = new MessageHandler(new Message("Message_" + i), consumerPools, consumerMinDelays, processedCount);
            handlerPool.submit(messageHandler);
        }
        logger.debug("all messages queued to processing");
    }

    private static void waitUntilAllProcessed() throws InterruptedException {
        while (processedCount.get() < MESSAGES_COUNT) {
            logger.debug("waiting all messages processed [" + processedCount.get() + "]");
            Thread.sleep(100L);
        }
        logger.debug("all messages processed");
    }

    private static void shutdownThreadPools() throws InterruptedException {
        shutdownThreadPool(handlerPool, "handlerPool");
        for (int i = 0; i < CONSUMERS_COUNT; i++) {
            ThreadPoolExecutor consumerPool = consumerPools.get(i);
            shutdownThreadPool(consumerPool, "consumerPool[" + i + "]");
        }
    }

    private static void shutdownThreadPool(ExecutorService threadPool, String poolName) throws InterruptedException {
        logger.debug("shutting down " + poolName);
        threadPool.shutdown();
        logger.debug("waiting " + poolName + " termination");
        threadPool.awaitTermination(10L, TimeUnit.SECONDS);
        logger.debug("SUCCESS " + poolName + " shutdown");
    }
}
