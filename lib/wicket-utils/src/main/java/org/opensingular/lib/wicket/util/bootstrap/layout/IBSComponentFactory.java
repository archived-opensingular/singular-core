/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.bootstrap.layout;

import java.io.Serializable;

import org.apache.wicket.Component;

/**
 * Interface funcional para a criação de um componente com um ID determinado.
 */
public interface IBSComponentFactory<C extends Component> extends Serializable {
    C newComponent(String componentId);
}
