/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.opensingular.flow.core.entity.IEntityProcessGroup;
import org.opensingular.singular.support.persistence.entity.BaseEntity;

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
