package com.opensingular.studio.persistence;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormPersistence;
import org.opensingular.form.persistence.FormPersistenceInMemory;

import java.util.HashMap;
import java.util.Map;

public class StudioCollectionPersistenceFactory {

    private SDocumentFactory documentFactory;
    private Map<SType<?>, FormPersistence<?>> repositoryMap = new HashMap<>();

    public StudioCollectionPersistenceFactory() {}

    public StudioCollectionPersistenceFactory(SDocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    public <T extends SInstance> FormPersistence<T> get(SType<T> type) {
        if (!repositoryMap.containsKey(type)) {
            repositoryMap.put(type, new FormPersistenceInMemory<>(documentFactory, new RefType() {
                @Override
                protected SType<?> retrieve() {
                    return type;
                }
            }));
        }
        return (FormPersistence<T>) repositoryMap.get(type);
    }


    public <T extends SInstance> void register(SType<T> type, FormPersistence<T> persistence) {
        repositoryMap.put(type, persistence);
    }

}
