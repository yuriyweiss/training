package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(MessageHandler.class);

    private final Message message;
    private final List<ThreadPoolExecutor> consumerPools;
    private final AtomicInteger processedCount;

    public MessageHandler(Message message, List<ThreadPoolExecutor> consumerPools, AtomicInteger processedCount) {
        this.message = message;
        this.consumerPools = consumerPools;
        this.processedCount = processedCount;
    }

    public MessageHandler(Message message, List<ThreadPoolExecutor> consumerPools, Map<ThreadPoolExecutor, Long> consumerMinDelays, AtomicInteger processedCount) {
        this(message, consumerPools, processedCount);
    }

    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("strating message processing: " + message);
        }

        List<Future<Boolean>> consumerResults = new ArrayList<>();
        consumerPools.stream()
                .forEach(consumerPool -> {
                    Future<Boolean> consumerResult = consumerPool.submit(new MessageConsumer(message));
                    consumerResults.add(consumerResult);
                });
        if (logger.isDebugEnabled()) {
            logger.debug("message sent to consumers, waiting");
        }

        try {
            for (Future<Boolean> consumerResult : consumerResults) {
                consumerResult.get();
            }
        } catch (InterruptedException e) {
            logger.info("handler interrupted");
            Thread.currentThread().interrupt();
            return;
        } catch (ExecutionException e) {
            logger.error("handler consumer execution error", e);
            return;
        }

        logger.info(String.format("all consumers processed the message: %s", message));
        processedCount.getAndIncrement();
    }
}
