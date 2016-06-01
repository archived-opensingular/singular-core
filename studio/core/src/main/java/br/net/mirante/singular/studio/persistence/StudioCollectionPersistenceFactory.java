package br.net.mirante.singular.studio.persistence;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.FormPersistence;
import br.net.mirante.singular.form.persistence.FormPersistenceInMemory;

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
