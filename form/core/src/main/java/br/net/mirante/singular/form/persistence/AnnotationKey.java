package br.net.mirante.singular.form.persistence;

import java.io.Serializable;

public interface AnnotationKey extends Serializable {
    public String toStringPersistence();

    public FormKey getFormKey();
}
