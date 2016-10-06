package com.opensingular.studio.core;

import org.opensingular.form.SType;

public class CollectionInfoBuilder<TYPE extends SType<?>> {

    private CollectionInfo collectionInfo = new CollectionInfo();


    public CollectionInfoBuilder<TYPE> form(Class<TYPE> clazz) {
        collectionInfo.setSTypeClass((Class<SType<?>>) clazz);
        return this;
    }

    public CollectionInfoBuilder<TYPE> title(String title) {
        collectionInfo.setTitle(title);
        return this;
    }

    CollectionInfo getCollectionInfo() {
        return collectionInfo;
    }

}
