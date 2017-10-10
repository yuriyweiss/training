package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class MessageHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(MessageHandler.class);

    private final Message message;
    private final List<ExecutorService> consumerPools;
    private final AtomicLong processedCount;

    public MessageHandler(Message message, List<ExecutorService> consumerPools, AtomicLong processedCount) {
        this.message = message;
        this.consumerPools = consumerPools;
        this.processedCount = processedCount;
    }

    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("strating message processing: " + message);
        }

        // send this message to all consumers
        List<Future<Boolean>> consumerResults = new ArrayList<>();
        consumerPools.stream()
                .forEach(consumerPool -> {
                    Future<Boolean> consumerResult = consumerPool.submit(new MessageConsumer(message));
                    consumerResults.add(consumerResult);
                });
        if (logger.isDebugEnabled()) {
            logger.debug("message sent to consumers, waiting");
        }

        // wait for consumer results
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

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("all consumers processed the message: %s", message));
        }
        processedCount.getAndIncrement();
    }
}
