/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core;

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
