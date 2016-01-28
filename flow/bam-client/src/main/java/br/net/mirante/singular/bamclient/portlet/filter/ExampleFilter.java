package br.net.mirante.singular.bamclient.portlet.filter;


public class ExampleFilter implements SingularPortletFilter {

    @FilterField(label = "Quantidade", size = FieldSize.SMALL)
    private Integer quantidade;

    @FilterField(label = "Descricao", size = FieldSize.MEDIUM)
    private String descricao;

    @FilterField(label = "Tipo de agregação")
    private PeriodAggregation periodAggregation;

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

    public PeriodAggregation getPeriodAggregation() {
        return periodAggregation;
    }

    public void setPeriodAggregation(PeriodAggregation periodAggregation) {
        this.periodAggregation = periodAggregation;
    }
}
