package br.net.mirante.singular.form.mform.basic.view;

public class SViewTextArea extends SView {

    private Integer lines = 3;

    public SViewTextArea() {
    }

    public Integer getLines() {
        return lines;
    }

    public SViewTextArea setLines(Integer lines) {
        this.lines = lines;
        return this;
    }
}
