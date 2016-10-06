/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.function;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

public interface IBehaviorContext {

    public IBehaviorContext update(SType<?>... fields);

    public default IBehaviorContext update(SInstance... fields) {
        SType<?>[] tipos = new SType<?>[fields.length];
        for (int i = 0; i < fields.length; i++)
            tipos[i] = fields[i].getType();
        update(tipos);
        return this;
    }
}
