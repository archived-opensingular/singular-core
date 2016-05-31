package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SType;

import java.io.Serializable;
import java.util.Optional;

public class CollectionCanvas<T extends SType<?>> implements Serializable{
    private CollectionDefinition<T> collectionDefinition;
    private CollectionInfo<T> collectionInfo;
    private IFunction<T, CollectionEditorConfig> editorConfigFunction = t -> {
        CollectionEditorConfigBuilder collectionEditorConfigBuilder = new CollectionEditorConfigBuilder();
        collectionDefinition.configEditor(collectionEditorConfigBuilder, t);
        return collectionEditorConfigBuilder.getEditor();
    };

    CollectionCanvas(CollectionDefinition<T> collectionDefinition, CollectionInfo<T> collectionInfo) {
        this.collectionDefinition = collectionDefinition;
        this.collectionInfo = collectionInfo;
    }

    public CollectionInfo<T> getCollectionInfo() {
        return collectionInfo;
    }

    public CollectionEditorConfig getEditorConfigFunction(T type) {
        return editorConfigFunction.apply(type);
    }

    public CollectionEditorConfig getEditorConfigFunction(IFunction<Class<T>, Optional<T>> typeLoader) {
        return editorConfigFunction.apply(typeLoader.apply(collectionInfo.getSTypeClass()).get());
    }
}