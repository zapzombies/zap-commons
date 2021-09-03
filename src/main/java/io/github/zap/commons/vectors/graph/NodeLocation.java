package io.github.zap.commons.vectors.graph;

import org.jetbrains.annotations.NotNull;

record NodeLocation(@NotNull NodeRow parent, @NotNull Object node, int parentIndex) {
    public void remove() {
        parent.set(parentIndex, null);
    }
}
