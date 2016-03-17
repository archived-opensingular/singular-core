/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core;

import java.util.Objects;

public class TransitionAccess {

    private final TransitionAccessLevel level;
    private final String message;

    public TransitionAccess(TransitionAccessLevel transitionAccessLevel, String message) {
        Objects.requireNonNull(transitionAccessLevel);
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
