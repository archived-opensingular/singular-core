/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.context;

import org.opensingular.form.document.ServiceRegistry;

/**
 * Disponibiliza à aplicação os métodos de interação com o Singular Form.
 *
 * @param <T>
 *            tipo do builder da interface
 * @param <K>
 *            tipo do mapper que o builder utiliza
 */
public interface SingularFormContext {

    public ServiceRegistry getServiceRegistry();

}
