package ru.yvays.training.thread.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MessageHandler implements Runnable {
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
        if (log.isDebugEnabled()) {
            log.debug("strating message processing: " + message);
        }

        // send this message to all consumers
        List<Future<Boolean>> consumerResults = new ArrayList<>();
        consumerPools.stream()
                .forEach(consumerPool -> {
                    Future<Boolean> consumerResult = consumerPool.submit(new MessageConsumer(message));
                    consumerResults.add(consumerResult);
                });
        if (log.isDebugEnabled()) {
            log.debug("message sent to consumers, waiting");
        }

        // wait for consumer results
        try {
            for (Future<Boolean> consumerResult : consumerResults) {
                consumerResult.get();
            }
        } catch (InterruptedException e) {
            log.info("handler interrupted");
            Thread.currentThread().interrupt();
            return;
        } catch (ExecutionException e) {
            log.error("handler consumer execution error", e);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("all consumers processed the message: %s", message));
        }
        processedCount.getAndIncrement();
    }
}
