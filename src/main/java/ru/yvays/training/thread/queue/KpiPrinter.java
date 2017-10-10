package ru.yvays.training.thread.queue;

import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class KpiPrinter {
    private static final Logger logger = Logger.getLogger(KpiPrinter.class);

    private final AtomicLong messageNumber;
    private final AtomicLong processedCount;
    private final ThreadPoolExecutor handlerPool;
    private Timer timer;
    private KpiInfo kpiInfo = new KpiInfo();

    public KpiPrinter(AtomicLong messageNumber, AtomicLong processedCount, ExecutorService handlerPool) {
        this.messageNumber = messageNumber;
        this.processedCount = processedCount;
        this.handlerPool = (ThreadPoolExecutor) handlerPool;
    }

    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new KpiTimerTask(), 1000L, 1000L);
    }

    public void cancel() {
        timer.cancel();
    }

    private class KpiTimerTask extends TimerTask {
        @Override
        public void run() {
            kpiInfo.setProducedCurr(messageNumber.get());
            kpiInfo.setConsumedCurr(processedCount.get());
            logger.info(String.format("PRD: %8d;\tPRD/SEC: %7d;\t\tCNSM: %8d;\tCNSM/SEC: %7d\t\tQUEUE_SIZE: %d",
                    kpiInfo.getProducedCurr(), kpiInfo.getProducedInPeriod(),
                    kpiInfo.getConsumedCurr(), kpiInfo.getConsumedInPeriod(),
                    handlerPool.getQueue().size()));
            kpiInfo.resetPrevValues();
        }
    }
}
