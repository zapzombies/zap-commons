package io.github.zap.commons.keyvaluegetters.keytransformers;

import io.github.zap.commons.keyvaluegetters.KeyTransformer;

/**
 * Returns the original name
 */
public class RetainKeyTransformer implements KeyTransformer {
    @Override
    public String transform(String name) {
        return name;
    }
}
