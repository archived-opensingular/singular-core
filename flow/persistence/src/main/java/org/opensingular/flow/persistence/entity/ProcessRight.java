/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.persistence.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.opensingular.lib.support.persistence.util.Constants;

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