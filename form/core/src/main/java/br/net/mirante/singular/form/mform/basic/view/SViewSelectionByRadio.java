package br.net.mirante.singular.form.mform.basic.view;

@SuppressWarnings("serial")
public class SViewSelectionByRadio extends SViewSelectionBySelect {

    public static enum Layout {
        VERTICAL,
        HORIZONTAL
    }
    private Layout layout = Layout.HORIZONTAL;

    public SViewSelectionByRadio verticalLayout() {
        this.layout = Layout.VERTICAL;
        return this;
    }

    public SViewSelectionByRadio horizontalLayout() {
        this.layout = Layout.HORIZONTAL;
        return this;
    }

    public Layout getLayout() {
        return layout;
    }

}
