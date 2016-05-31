package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.form.SType;

/**
 * Configuração do editor do studio-form não serializável.
 */
public class CollectionInfo<TYPE extends SType<?>> {

    private Class<TYPE> clazz;


    public Class<TYPE> getSTypeClass() {
        return clazz;
    }

    void setSTypeClass(Class<TYPE> clazz) {
        this.clazz = clazz;
    }
}
