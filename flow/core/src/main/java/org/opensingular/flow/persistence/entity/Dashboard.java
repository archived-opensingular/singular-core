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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

@Entity
@GenericGenerator(name = "GENERATED_CO_DASHBOARD", strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_DASHBOARD", schema = Constants.SCHEMA)
public class Dashboard extends BaseEntity<Long> {

    @Id
    @Column(name = "CO_DASHBOARD")
    @GeneratedValue(generator = "GENERATED_CO_DASHBOARD")
    private Long cod;

    @Column(name = "NO_DASHBOARD", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dashboard")
    private List<Portlet> portlets;

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

    public List<Portlet> getPortlets() {
        return portlets;
    }

    public void setPortlets(List<Portlet> portlets) {
        this.portlets = portlets;
    }
}
