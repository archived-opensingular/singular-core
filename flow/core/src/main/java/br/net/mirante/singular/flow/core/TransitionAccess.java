package br.net.mirante.singular.flow.core;

import com.google.common.base.Preconditions;

public class TransitionAccess {

    private final TransitionAccessLevel level;
    private final String message;

    public TransitionAccess(TransitionAccessLevel transitionAccessLevel, String message) {
        Preconditions.checkNotNull(transitionAccessLevel);
        this.level = transitionAccessLevel;
        this.message = message;
    }

    public boolean isEnabled() {
        return level.equals(TransitionAccessLevel.ENABLED) || level.equals(TransitionAccessLevel.ENABLED_BUT_HIDDEN);
    }

    public boolean isVisible() {
        return level.equals(TransitionAccessLevel.ENABLED) || level.equals(TransitionAccessLevel.DISABLED_BUT_VISIBLE);
    }

    public String getMessage() {
        return message;
    }

    public enum TransitionAccessLevel {
        ENABLED,
        ENABLED_BUT_HIDDEN,
        DISABLED_BUT_VISIBLE,
        DISABLED_AND_HIDDEN;
    }

}
