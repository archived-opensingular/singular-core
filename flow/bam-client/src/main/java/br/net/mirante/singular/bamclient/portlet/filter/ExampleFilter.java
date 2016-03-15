/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.portlet.filter;


import java.util.Date;

import br.net.mirante.singular.flow.core.DashboardFilter;

public class ExampleFilter implements DashboardFilter {

    @FilterField(label = "Quantidade", size = FieldSize.SMALL)
    private Integer quantidade;

    @FilterField(label = "Descricao", size = FieldSize.MEDIUM)
    private String descricao;

    @FilterField(label = "Tipo de agregação")
    private AggregationPeriod aggregationPeriod;

    @FilterField(label = "Data")
    private Date data;

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public AggregationPeriod getAggregationPeriod() {
        return aggregationPeriod;
    }

    public void setAggregationPeriod(AggregationPeriod aggregationPeriod) {
        this.aggregationPeriod = aggregationPeriod;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
