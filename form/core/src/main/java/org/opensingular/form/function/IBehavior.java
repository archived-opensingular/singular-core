/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
