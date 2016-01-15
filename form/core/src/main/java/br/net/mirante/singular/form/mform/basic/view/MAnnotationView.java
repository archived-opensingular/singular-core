package br.net.mirante.singular.form.mform.basic.view;

import java.util.function.Supplier;

/**
 * Created by nuk on 14/01/16.
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
