/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity.util;

import org.hibernate.Session;

@FunctionalInterface
public interface SessionLocator {

    Session getCurrentSession();
}
