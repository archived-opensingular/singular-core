/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;

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
