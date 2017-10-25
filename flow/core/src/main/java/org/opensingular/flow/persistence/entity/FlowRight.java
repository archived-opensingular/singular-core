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

import org.opensingular.lib.support.persistence.util.Constants;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the flow permission database table.
 */
@Entity
@Table(name = "RL_PERMISSAO_PROCESSO", schema = Constants.SCHEMA)
public class FlowRight {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private FlowRightPK id;

    //bi-directional many-to-one association to FlowDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO", insertable = false, updatable = false)
    private FlowDefinitionEntity flowDefinition;

    public FlowRight() {
    }

    public FlowRightPK getId() {
        return this.id;
    }

    public void setId(FlowRightPK id) {
        this.id = id;
    }

    public FlowDefinitionEntity getFlowDefinition() {
        return this.flowDefinition;
    }

    public void setFlowDefinition(FlowDefinitionEntity flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

}