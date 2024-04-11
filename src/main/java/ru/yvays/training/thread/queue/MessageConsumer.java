package ru.yvays.training.thread.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class MessageConsumer implements Callable<Boolean> {
    private final Message message;

    public MessageConsumer( Message message ) {
        this.message = message;
    }

    @Override
    public Boolean call() throws Exception {
        int delay = ThreadLocalRandom.current().nextInt( 1, 2 );
        if ( log.isDebugEnabled() ) {
            log.debug( "delayed [" + delay + "millis] processing message: " + message );
        }
        // work emulation
        try {
            Thread.sleep( delay );
        } catch ( InterruptedException e ) {
            log.info( "consumer interrupted for message: " + message );
            Thread.currentThread().interrupt();
            return false;
        }
        if ( log.isDebugEnabled() ) {
            log.debug( String.format( "message processed: %s", message ) );
        }
        return true;
    }
}
