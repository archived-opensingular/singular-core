/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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