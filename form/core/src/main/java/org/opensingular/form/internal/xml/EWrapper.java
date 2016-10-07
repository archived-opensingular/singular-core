/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.internal.xml;

import org.w3c.dom.Element;

/**
 * Indica que uma classe possui um Element embutido internamente. Permite
 * assim obter o elemento original.
 *
 * @author Daniel C. Bordin
 */
interface EWrapper {

    /**
     * Obtem o Element contido internamente pelo envoltorio.
     *
     * @return Geralmente not null, mas depente do wrapper
     */
    public Element getOriginal();

}
