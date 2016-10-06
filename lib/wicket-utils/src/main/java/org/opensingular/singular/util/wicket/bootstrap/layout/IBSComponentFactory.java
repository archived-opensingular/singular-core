/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.bootstrap.layout;

import java.io.Serializable;

import org.apache.wicket.Component;

/**
 * Interface funcional para a criação de um componente com um ID determinado.
 */
public interface IBSComponentFactory<C extends Component> extends Serializable {
    C newComponent(String componentId);
}
