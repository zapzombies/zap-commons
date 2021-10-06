package io.github.zap.commons;

public class TimeMeasurementException extends RuntimeException {
    private final long timeElapsed;

    public TimeMeasurementException(long timeElapsed, Throwable cause) {
        super(cause);
        this.timeElapsed = timeElapsed;
    }

    /**
     * Time elapsed at the moment of the error being thrown
     * @return time elapsed, in milliseconds
     */
    public long getTimeElapsed() {
        return timeElapsed;
    }
}
