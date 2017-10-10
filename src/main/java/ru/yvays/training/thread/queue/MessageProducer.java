package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

public class MessageProducer implements Runnable {
    private static final Logger logger = Logger.getLogger(MessageProducer.class);

    private final ExecutorService handlerPool;
    private final List<ExecutorService> consumerPools;
    private final AtomicLong processedCount;
    private final AtomicLong messageNumber;

    public MessageProducer(ExecutorService handlerPool, List<ExecutorService> consumerPools, AtomicLong processedCount, AtomicLong messageNumber) {
        this.handlerPool = handlerPool;
        this.consumerPools = consumerPools;
        this.processedCount = processedCount;
        this.messageNumber = messageNumber;
    }

    @Override
    public void run() {
        logger.info("producer thread started");
        while (!Thread.currentThread().isInterrupted()) {
            long currentNumber = messageNumber.get();
            Message message = new Message("Message_" + currentNumber);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("sending message #%d to handlers", currentNumber));
            }
            MessageHandler messageHandler = new MessageHandler(message, consumerPools, processedCount);
            handlerPool.submit(messageHandler);
            messageNumber.getAndIncrement();
            if (currentNumber % 100 == 0) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    logger.info("producer thread sleep interrupted");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        logger.info("producer thread stopped");
    }
}
