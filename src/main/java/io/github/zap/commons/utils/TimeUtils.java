package io.github.zap.commons.utils;

import io.github.zap.commons.ThrowableRunnable;
import io.github.zap.commons.TimeMeasurementException;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * Utils for ticks or time
 */
public class TimeUtils {
    /**
     * Converts a tick count to seconds as a string
     * @param ticks The tick count
     * @return A string representation of the seconds remaining
     */
    public static String convertTicksToSecondsString(long ticks) {
        return String.format("%.2fs", (double) (ticks / 20) + 0.05D * (ticks % 20));
    }

    public static long measure(@NotNull ThrowableRunnable action) throws TimeMeasurementException {
        Objects.requireNonNull(action, "action cannot be null!");
        StopWatch timer = StopWatch.createStarted();
        try {
            action.run();
        } catch (Throwable t) {
            throw new TimeMeasurementException(timer.getTime(), t);
        }
        finally {
            timer.stop();
        }

        return timer.getTime();
    }
}
