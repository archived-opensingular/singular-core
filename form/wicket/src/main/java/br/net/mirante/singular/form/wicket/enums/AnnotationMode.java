package br.net.mirante.singular.form.wicket.enums;

public enum AnnotationMode {

    NONE, EDIT, READ_ONLY;

    public boolean editable() {
        return this.equals(EDIT);
    }

    public boolean enabled() {
        return !this.equals(NONE);
    }
}
