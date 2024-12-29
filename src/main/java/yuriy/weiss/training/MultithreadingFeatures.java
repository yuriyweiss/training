package yuriy.weiss.training;

import lombok.extern.slf4j.Slf4j;
import yuriy.weiss.training.utils.ThreadUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MultithreadingFeatures {

    private static final int THREAD_NUM = 5;

    private static final AtomicInteger finishedCount = new AtomicInteger(0);

    public static void main( String[] args ) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool( THREAD_NUM );
        final Semaphore semaphore = new Semaphore( 1, true );
        for ( int i = 0; i < THREAD_NUM; i++ ) {
            executorService.submit( new FakeRunnable( i, semaphore ) );
        }
        while (finishedCount.get() < THREAD_NUM) {
            Thread.sleep(100L);
        }
        executorService.shutdownNow();
    }

    private record FakeRunnable(int threadNumber, Semaphore semaphore) implements Runnable {
        @Override
        public void run() {
            for ( int i = 0; i < 10; i++ ) {
                try {
                    log.info( "-{}- before acquire semaphore", threadNumber );
                    semaphore.acquire();
                    log.info( "-{}- semaphore acquired", threadNumber );
                    Thread.sleep( 300L );
                    log.info( "-{}- SLEEP FINISHED", threadNumber );
                } catch ( InterruptedException e ) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();
                    log.info( "-{}- semaphore released", threadNumber );
                }
                ThreadUtils.sleep( 1000L + 2000L * threadNumber );
                log.info( "-{}- WORK FINISHED", threadNumber );
            }
            finishedCount.getAndIncrement();
        }
    }
}
