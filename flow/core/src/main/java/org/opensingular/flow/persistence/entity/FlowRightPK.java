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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The primary key class for the RL_PERMISSAO_PROCESSO database table.
 */
@Embeddable
public class FlowRightPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "CO_DEFINICAO_PROCESSO")
    private long codFlowDefinition;

    @Column(name = "TP_PERMISSAO")
    private String rightType;

    public FlowRightPK() {
    }

    public long getCodFlowDefinition() {
        return this.codFlowDefinition;
    }

    public void setCodFlowDefinition(long codFlowDefinition) {
        this.codFlowDefinition = codFlowDefinition;
    }

    public String getRightType() {
        return this.rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FlowRightPK)) {
            return false;
        }
        FlowRightPK castOther = (FlowRightPK) other;
        return
                (this.codFlowDefinition == castOther.codFlowDefinition)
                        && this.rightType.equals(castOther.rightType);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + ((int) (this.codFlowDefinition ^ (this.codFlowDefinition >>> 32)));
        hash = hash * prime + this.rightType.hashCode();

        return hash;
    }
}