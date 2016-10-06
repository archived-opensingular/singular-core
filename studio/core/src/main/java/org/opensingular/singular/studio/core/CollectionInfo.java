package org.opensingular.singular.studio.core;

import org.opensingular.singular.form.SType;

import java.io.Serializable;

/**
 * Configuração do editor do studio-form não serializável.
 */
public class CollectionInfo implements Serializable {

    private Class<SType<?>> clazz;
    private String title;


    public Class<SType<?>> getSTypeClass() {
        return clazz;
    }

    void setSTypeClass(Class<SType<?>> clazz) {
        this.clazz = clazz;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }
}
