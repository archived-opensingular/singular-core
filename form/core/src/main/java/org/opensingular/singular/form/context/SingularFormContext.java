/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.context;

import org.opensingular.singular.form.document.ServiceRegistry;

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
