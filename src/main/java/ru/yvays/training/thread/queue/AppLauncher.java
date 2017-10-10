package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class AppLauncher {
    private static final Logger logger = Logger.getLogger(AppLauncher.class);

    private static final int CONSUMERS_COUNT = 3;
    private static final int THREADS_IN_POOL = 40;

    private static ExecutorService handlerPool;
    private static List<ExecutorService> consumerPools = new ArrayList<>();
    private static AtomicLong processedCount = new AtomicLong(0);

    private static Thread producer;
    private static AtomicLong messageNumber = new AtomicLong(0);

    private static KpiPrinter kpiPrinter;

    public static void main(String[] args) {
        initializeThreadPools();
        startProducer();
        startKpiPrinter();
        try {
            Thread.sleep(60000L);
            shutdownAll();
        } catch (InterruptedException e) {
            logger.info("thread pools shutdown interrupted");
            Thread.currentThread().interrupt();
        }
        logger.info("application finished work");
    }

    private static void initializeThreadPools() {
        logger.info("initializing thread pools");
        handlerPool = Executors.newFixedThreadPool(THREADS_IN_POOL);
        for (int i = 0; i < CONSUMERS_COUNT; i++) {
            ExecutorService consumerPool = Executors.newFixedThreadPool(THREADS_IN_POOL);
            consumerPools.add(consumerPool);
        }
        logger.info("thread pools initialized");
    }

    private static void startProducer() {
        producer = new Thread(new MessageProducer(handlerPool, consumerPools, processedCount, messageNumber));
        producer.start();
    }

    private static void startKpiPrinter() {
        kpiPrinter = new KpiPrinter(messageNumber, processedCount, handlerPool);
        kpiPrinter.start();
    }

    private static void shutdownAll() throws InterruptedException {
        logger.info("shutdown all");
        shutdownProducer();
        shutdownThreadPools();
        kpiPrinter.cancel();
    }

    private static void shutdownProducer() {
        producer.interrupt();
        while (producer.isAlive()) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.info("SUCCESS producer shutdown");
    }

    private static void shutdownThreadPools() throws InterruptedException {
        shutdownThreadPool(handlerPool, "handlerPool");
        for (int i = 0; i < CONSUMERS_COUNT; i++) {
            shutdownThreadPool(consumerPools.get(i), "consumerPool[" + i + "]");
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
