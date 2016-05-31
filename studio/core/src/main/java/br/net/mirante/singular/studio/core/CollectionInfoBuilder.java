package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.form.SType;

public class CollectionInfoBuilder<TYPE extends SType<?>> {

    private CollectionInfo<TYPE> collectionInfo = new CollectionInfo<>();

    public CollectionInfoBuilder<TYPE> form(Class<TYPE> clazz) {
        collectionInfo.setSTypeClass(clazz);
        return this;
    }

    CollectionInfo<TYPE> getCollectionInfo() {
        return collectionInfo;
    }

}
