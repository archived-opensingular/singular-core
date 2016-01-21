package br.net.mirante.singular.form.mform.basic.view;

import java.util.function.Supplier;

/**
 * Defines that a component can have an annotation attached to it when in read only mode,
 *
 * @author Fabricio Buzeto
 */
public class MAnnotationView extends MView {
    private String title;

    public MAnnotationView title(String title) {
        this.title = title;
        return this;
    }

    public String title() {
        return title;
    }
}
