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

package org.opensingular.form.function;

import org.opensingular.form.SInstance;

@FunctionalInterface
public interface IBehavior<T extends SInstance> {

    public void on(IBehaviorContext ctx, T instance);

    public default IBehavior<T> andThen(IBehavior<T> next) {
        return (ctx, instance) -> {
            this.on(ctx, instance);
            if (next != null)
                next.on(ctx, instance);
        };
    }

    public static IBehavior<SInstance> noop() {
        return (c, i) -> {};
    }
    public static IBehavior<SInstance> noopIfNull(IBehavior<SInstance> behavior) {
        return (behavior != null) ? behavior : noop();
    }
}
