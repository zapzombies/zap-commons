package io.github.zap.commons.keyvaluegetters;

import java.util.Optional;

public record OperationResult<T>(Optional<T> result, String message, Throwable throwable) {
    public static <T> OperationResult<T> of(T result) {
        return new OperationResult<>(Optional.of(result), null, null);
    }

    public static <T> OperationResult<T> error(String message, Throwable throwable) {
        return new OperationResult<>(Optional.empty(), message, throwable);
    }

    public static <T> OperationResult<T> error(String message) {
        return error(message, null);
    }

    public static <T> OperationResult<T> error(Throwable e) {
        return error(null, e);
    }

    public boolean isSuccess () {
        return result.isPresent();
    }
}
