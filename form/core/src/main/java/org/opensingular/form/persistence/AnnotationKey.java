package org.opensingular.form.persistence;

import java.io.Serializable;

public interface AnnotationKey extends Serializable {

    public String toStringPersistence();

    /**
     * Constante textual que indentifica a anotação
     * O valor deve ser representativo do ponto de vista
     * do negócio
     * @return
     */
    public String getClassifier();

    public FormKey getFormKey();
}
