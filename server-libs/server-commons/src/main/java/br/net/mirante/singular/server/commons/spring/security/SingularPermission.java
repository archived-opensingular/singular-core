/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.spring.security;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe que representa uma permissão do Singular.
 * Ela armazena tanto a representação do próprio singular,
 * quanto o identificador utilizado no reposítorio de autorização
 * do cliente.
 */
public class SingularPermission implements Serializable {

    private String singularId;

    private Serializable internalId;

    public SingularPermission() {
    }

    public SingularPermission(String singularId, Serializable internalId) {
        this.singularId = singularId;
        this.internalId = internalId;
    }

    public String getSingularId() {
        return singularId;
    }

    public void setSingularId(String singularId) {
        this.singularId = singularId;
    }

    public Serializable getInternalId() {
        return internalId;
    }

    public void setInternalId(Serializable internalId) {
        this.internalId = internalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingularPermission that = (SingularPermission) o;
        return Objects.equals(singularId, that.singularId) &&
                Objects.equals(internalId, that.internalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(singularId, internalId);
    }
}
