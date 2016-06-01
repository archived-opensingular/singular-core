package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.form.SType;

public class CollectionInfoBuilder<TYPE extends SType<?>> {

    private CollectionInfo collectionInfo = new CollectionInfo();

    public CollectionInfoBuilder<TYPE> form(Class<TYPE> clazz) {
        collectionInfo.setSTypeClass((Class<SType<?>>) clazz);
        return this;
    }

    CollectionInfo getCollectionInfo() {
        return collectionInfo;
    }

}
