/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.MProcessRole;

public interface BProcessRole<SELF extends BProcessRole<SELF>> {

    public MProcessRole getProcessRole();
}