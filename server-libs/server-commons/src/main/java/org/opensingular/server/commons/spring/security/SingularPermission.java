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

package org.opensingular.server.commons.spring.security;

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
