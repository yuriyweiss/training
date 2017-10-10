package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class MessageConsumer implements Callable<Boolean> {
    private static final Logger logger = Logger.getLogger(MessageConsumer.class);

    private final Message message;

    public MessageConsumer(Message message) {
        this.message = message;
    }

    @Override
    public Boolean call() throws Exception {
        int delay = ThreadLocalRandom.current().nextInt(1, 2);
        if (logger.isDebugEnabled()) {
            logger.debug("delayed [" + delay + "millis] processing message: " + message);
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            logger.info("consumer interrupted for message: " + message);
            Thread.currentThread().interrupt();
            return false;
        }
        logger.info(String.format("message processed: %s", message));
        return true;
    }
}
