package br.net.mirante.singular.form.mform.basic.view;

public class MTextAreaView extends MView {

    private Integer linhas = 3;

    public MTextAreaView() {
    }

    public Integer getLinhas() {
        return linhas;
    }

    public MTextAreaView setLinhas(Integer linhas) {
        this.linhas = linhas;
        return this;
    }
}
