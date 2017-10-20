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

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@GenericGenerator(name = "GENERATED_CO_PORTLET", strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_PORTLET", schema = Constants.SCHEMA)
public class Portlet extends BaseEntity<Long> {

    @Id
    @Column(name = "CO_PORTLET")
    @GeneratedValue(generator = "GENERATED_CO_PORTLET")
    private Long cod;

    @Column(name = "NO_PORTLET", nullable = false)
    private String name;

    @Column(name = "NO_PROCESSO")
    private String flowDefinitionAbbreviation;

    @Column(name = "NU_ORDEM", nullable = false)
    private Long ordem;

    @Column(name = "NU_TAMANHO", nullable = false)
    private Long size;

    @Column(name = "ST_DINAMICO", nullable = false)
    private Boolean dynamic;

    @ManyToOne
    @JoinColumn(name = "CO_DASHBOARD", nullable = false)
    private Dashboard dashboard;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlowDefinitionAbbreviation() {
        return flowDefinitionAbbreviation;
    }

    public void setFlowDefinitionAbbreviation(String flowDefinitionAbbreviation) {
        this.flowDefinitionAbbreviation = flowDefinitionAbbreviation;
    }

    public Long getOrdem() {
        return ordem;
    }

    public void setOrdem(Long ordem) {
        this.ordem = ordem;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}
