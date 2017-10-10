package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageProducer implements Runnable {
    private static final Logger logger = Logger.getLogger(MessageProducer.class);

    private final ExecutorService handlerPool;
    private final List<ThreadPoolExecutor> consumerPools;
    private final AtomicInteger processedCount;
    private final AtomicInteger messageNumber;

    public MessageProducer(ExecutorService handlerPool, List<ThreadPoolExecutor> consumerPools, AtomicInteger processedCount, AtomicInteger messageNumber) {
        this.handlerPool = handlerPool;
        this.consumerPools = consumerPools;
        this.processedCount = processedCount;
        this.messageNumber = messageNumber;
    }

    @Override
    public void run() {
        logger.info("producer thread started");
        while (!Thread.currentThread().isInterrupted()) {
            int number = messageNumber.get();
            Message message = new Message("Message_" + number);
            logger.info(String.format("sending message #%d to handlers", number));
            MessageHandler messageHandler = new MessageHandler(message, consumerPools, processedCount);
            handlerPool.submit(messageHandler);
            messageNumber.getAndIncrement();
            Thread.yield();
        }
        logger.info("producer thread stopped");
    }
}
