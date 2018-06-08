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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The primary key class for the flow permission database table.
 */
@Embeddable
public class FlowRightPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "CO_DEFINICAO_PROCESSO")
    private Long codFlowDefinition;

    @Column(name = "TP_PERMISSAO")
    private Character rightType;

    public FlowRightPK() {
    }

    public Long getCodFlowDefinition() {
        return this.codFlowDefinition;
    }

    public void setCodFlowDefinition(Long codFlowDefinition) {
        this.codFlowDefinition = codFlowDefinition;
    }

    public Character getRightType() {
        return this.rightType;
    }

    public void setRightType(Character rightType) {
        this.rightType = rightType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FlowRightPK)) return false;

        FlowRightPK that = (FlowRightPK) o;

        return new EqualsBuilder()
                .append(codFlowDefinition, that.codFlowDefinition)
                .append(rightType, that.rightType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(codFlowDefinition)
                .append(rightType)
                .toHashCode();
    }
}