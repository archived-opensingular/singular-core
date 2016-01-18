package br.net.mirante.singular.form.mform.basic.view;

@SuppressWarnings("serial")
public class MSelecaoPorRadioView extends MSelecaoPorSelectView {

    public static enum Layout {
        VERTICAL,
        HORIZONTAL
    }
    private Layout layout = Layout.HORIZONTAL;

    public MSelecaoPorRadioView layoutVertical() {
        this.layout = Layout.VERTICAL;
        return this;
    }

    public MSelecaoPorRadioView layoutHorizontal() {
        this.layout = Layout.HORIZONTAL;
        return this;
    }

    public Layout getLayout() {
        return layout;
    }

}
