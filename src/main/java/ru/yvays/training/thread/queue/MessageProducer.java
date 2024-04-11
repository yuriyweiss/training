package ru.yvays.training.thread.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MessageProducer implements Runnable {
    private final ExecutorService handlerPool;
    private final List<ExecutorService> consumerPools;
    private final AtomicLong processedCount;
    private final AtomicLong messageNumber;

    public MessageProducer( ExecutorService handlerPool, List<ExecutorService> consumerPools,
            AtomicLong processedCount, AtomicLong messageNumber ) {
        this.handlerPool = handlerPool;
        this.consumerPools = consumerPools;
        this.processedCount = processedCount;
        this.messageNumber = messageNumber;
    }

    @Override
    public void run() {
        log.info( "producer thread started" );
        while ( !Thread.currentThread().isInterrupted() ) {
            long currentNumber = messageNumber.get();
            Message message = new Message( "Message_" + currentNumber );
            if ( log.isDebugEnabled() ) {
                log.debug( String.format( "sending message #%d to handlers", currentNumber ) );
            }
            MessageHandler messageHandler = new MessageHandler( message, consumerPools, processedCount );
            handlerPool.submit( messageHandler );
            messageNumber.getAndIncrement();
            if ( currentNumber % 100 == 0 ) {
                try {
                    Thread.sleep( 5 );
                } catch ( InterruptedException e ) {
                    log.info( "producer thread sleep interrupted" );
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        log.info( "producer thread stopped" );
    }
}
