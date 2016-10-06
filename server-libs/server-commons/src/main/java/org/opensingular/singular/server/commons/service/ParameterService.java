/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.commons.service;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.flow.core.entity.IEntityProcessGroup;
import org.opensingular.singular.server.commons.persistence.dao.ParameterDAO;
import org.opensingular.singular.server.commons.persistence.entity.parameter.ParameterEntity;

@Transactional(readOnly = true)
public class ParameterService implements Loggable {

    @Inject
    private ParameterDAO parameterDAO;

    public Optional<ParameterEntity> findByNameAndProcessGroup(String name, IEntityProcessGroup processGroup) {
        return Optional.ofNullable(parameterDAO.findByNameAndProcessGroup(name, processGroup));
    }

}
