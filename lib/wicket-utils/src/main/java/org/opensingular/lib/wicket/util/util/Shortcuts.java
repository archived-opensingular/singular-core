/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.util;

public class Shortcuts {

    public static final IModelsMixin     $m = Impl.INSTANCE;
    public static final IBehaviorsMixin  $b = Impl.INSTANCE;
    public static final IValidatorsMixin $v = Impl.INSTANCE;

    private Shortcuts() {}

    // é um enum para evitar problemas com a serialização
    private enum Impl implements IModelsMixin, IBehaviorsMixin, IValidatorsMixin {
        INSTANCE;
    }
}
