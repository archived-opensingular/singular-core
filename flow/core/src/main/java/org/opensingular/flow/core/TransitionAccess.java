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

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class TransitionAccess implements Serializable {

    private final TransitionVisibilityLevel level;
    private final String                    message;

    public enum TransitionVisibilityLevel {
        /**
         * Interface must display an action input/link enabled
         */
        ENABLED_AND_VISIBLE,
        /**
         * Interface must display an action input/link disabled
         */
        DISABLED_AND_VISIBLE,
        /**
         * Interface must not display any action input/link
         */
        DISABLED_AND_HIDDEN;
    }

    public TransitionAccess(TransitionVisibilityLevel transitionVisibilityLevel) {
        this(transitionVisibilityLevel, null);
    }

    public TransitionAccess(TransitionVisibilityLevel transitionVisibilityLevel, String message) {
        Objects.requireNonNull(transitionVisibilityLevel);
        this.level = transitionVisibilityLevel;
        this.message = message;
    }

    public boolean isEnabled() {
        return level == TransitionVisibilityLevel.ENABLED_AND_VISIBLE;
    }

    public boolean isVisible() {
        return level == TransitionVisibilityLevel.ENABLED_AND_VISIBLE || level == TransitionVisibilityLevel.DISABLED_AND_VISIBLE;
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

}
