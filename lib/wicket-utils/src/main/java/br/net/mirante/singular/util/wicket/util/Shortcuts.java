/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.util;

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
