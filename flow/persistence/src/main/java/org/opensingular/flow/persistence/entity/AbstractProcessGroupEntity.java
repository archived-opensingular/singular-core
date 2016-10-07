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
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.opensingular.flow.core.entity.IEntityProcessGroup;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

/**
 * The base persistent class for the TB_GRUPO_PROCESSO database table.
 * <p>
 *
 * @param <PROCESS_DEF>
 */
@MappedSuperclass
@Table(name = "TB_GRUPO_PROCESSO")
public abstract class AbstractProcessGroupEntity extends BaseEntity<String> implements IEntityProcessGroup {

    @Id
    @Column(name = "CO_GRUPO_PROCESSO")
    private String cod;

    @Column(name = "NO_GRUPO", length = 100, nullable = false)
    private String name;

    @Column(name = "URL_CONEXAO", length = 300, nullable = false)
    private String connectionURL;

    @Override
    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

}
