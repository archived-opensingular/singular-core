/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.core.entity.IEntityProcessGroup;
import br.net.mirante.singular.server.commons.persistence.dao.ParameterDAO;
import br.net.mirante.singular.server.commons.persistence.entity.parameter.ParameterEntity;

@Transactional(readOnly = true)
public class ParameterService implements Loggable {

    @Inject
    private ParameterDAO parameterDAO;

    public Optional<ParameterEntity> findByNameAndProcessGroup(String name, IEntityProcessGroup processGroup) {
        return Optional.ofNullable(parameterDAO.findByNameAndProcessGroup(name, processGroup));
    }

}
