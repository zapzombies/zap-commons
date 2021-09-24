package io.github.zap.commons.keyvaluegetters;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

/**
 * Represent errors during a {@link KeyValueGetter#bind(Object)} operation
 */
public class GetFailedException extends Exception {
    private final List<ImmutablePair<KeyField, OperationResult<String>>> faultyGetOperation;
    private final List<ImmutablePair<KeyField, OperationResult<?>>> faultyDeserializeOperation;
    private final List<ImmutablePair<KeyField, OperationResult<?>>> faultyBindOperation;
    private final Exception innerException;

    public GetFailedException(String message, Exception innerException) {
        this(message, null, null, null, innerException);
    }

    public GetFailedException(String message,
                              List<ImmutablePair<KeyField, OperationResult<String>>> faultyGetOperation,
                              List<ImmutablePair<KeyField, OperationResult<?>>> faultyDeserializeOperation,
                              List<ImmutablePair<KeyField, OperationResult<?>>> faultyBindOperation) {
        this(message, faultyGetOperation, faultyDeserializeOperation, faultyBindOperation, null);
    }

    // Kinda questionable
    public GetFailedException(
            String message,
            List<ImmutablePair<KeyField, OperationResult<String>>> faultyGetOperation,
            List<ImmutablePair<KeyField, OperationResult<?>>> faultyDeserializeOperation,
            List<ImmutablePair<KeyField, OperationResult<?>>> faultyBindOperation,
            Exception innerException) {
        super(message);
        this.faultyGetOperation = faultyGetOperation;
        this.faultyDeserializeOperation = faultyDeserializeOperation;
        this.faultyBindOperation = faultyBindOperation;
        this.innerException = innerException;
    }

    public List<ImmutablePair<KeyField, OperationResult<String>>> getFaultyGetOperation() {
        return faultyGetOperation;
    }

    public List<ImmutablePair<KeyField, OperationResult<?>>> getFaultyDeserializeOperation() {
        return faultyDeserializeOperation;
    }

    public List<ImmutablePair<KeyField, OperationResult<?>>> getFaultyBindOperation() {
        return faultyBindOperation;
    }
}
