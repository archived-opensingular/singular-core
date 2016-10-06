/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.persistence.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.opensingular.singular.support.persistence.util.Constants;

/**
 * The persistent class for the RL_PERMISSAO_PROCESSO database table.
 */
@Entity
@Table(name = "RL_PERMISSAO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessRight {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ProcessRightPK id;

    //bi-directional many-to-one association to ProcessDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO", insertable = false, updatable = false)
    private ProcessDefinitionEntity processDefinition;

    public ProcessRight() {
    }

    public ProcessRightPK getId() {
        return this.id;
    }

    public void setId(ProcessRightPK id) {
        this.id = id;
    }

    public ProcessDefinitionEntity getProcessDefinition() {
        return this.processDefinition;
    }

    public void setProcessDefinition(ProcessDefinitionEntity processDefinition) {
        this.processDefinition = processDefinition;
    }

}