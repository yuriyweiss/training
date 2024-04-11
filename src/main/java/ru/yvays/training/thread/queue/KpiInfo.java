package ru.yvays.training.thread.queue;

public class KpiInfo {
    private long producedPrev = 0L;
    private long producedCurr = 0L;
    private long consumedPrev = 0L;
    private long consumedCurr = 0L;

    public void resetPrevValues() {
        producedPrev = producedCurr;
        consumedPrev = consumedCurr;
    }

    public void setProducedCurr( long producedCurr ) {
        this.producedCurr = producedCurr;
    }

    public void setConsumedCurr( long consumedCurr ) {
        this.consumedCurr = consumedCurr;
    }

    public long getProducedCurr() {
        return producedCurr;
    }

    public long getConsumedCurr() {
        return consumedCurr;
    }

    public long getProducedInPeriod() {
        return producedCurr - producedPrev;
    }

    public long getConsumedInPeriod() {
        return consumedCurr - consumedPrev;
    }
}
