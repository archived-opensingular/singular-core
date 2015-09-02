package br.net.mirante.singular.util.wicket.resource;

public enum FeedIcon {
    success(" label-success"), danger(" label-danger"),
    info(" label-info"), warning(" label-warning"),
    padrao(" label-default");

    String descricao;

    FeedIcon(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
