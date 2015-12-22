package br.net.mirante.singular.form.wicket.enums;


public enum ViewMode {

    EDITION,
    VISUALIZATION;

    public boolean isEdition() {
        return this.equals(EDITION);
    }

    public boolean isVisualization() {
        return this.equals(VISUALIZATION);
    }
}
