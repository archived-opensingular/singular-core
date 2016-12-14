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

/**
 * The primary key class for the RL_PERMISSAO_PROCESSO database table.
 */
@Embeddable
public class ProcessRightPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "CO_DEFINICAO_PROCESSO")
    private long codProcessDefinition;

    @Column(name = "TP_PERMISSAO")
    private String rightType;

    public ProcessRightPK() {
    }

    public long getCodProcessDefinition() {
        return this.codProcessDefinition;
    }

    public void setCodProcessDefinition(long codProcessDefinition) {
        this.codProcessDefinition = codProcessDefinition;
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
        if (!(other instanceof ProcessRightPK)) {
            return false;
        }
        ProcessRightPK castOther = (ProcessRightPK) other;
        return
                (this.codProcessDefinition == castOther.codProcessDefinition)
                        && this.rightType.equals(castOther.rightType);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + ((int) (this.codProcessDefinition ^ (this.codProcessDefinition >>> 32)));
        hash = hash * prime + this.rightType.hashCode();

        return hash;
    }
}