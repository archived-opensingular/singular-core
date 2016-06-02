package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SType;

import java.io.Serializable;
import java.util.Optional;

public class CollectionCanvas implements Serializable {
    private CollectionDefinition collectionDefinition;
    private CollectionInfo collectionInfo;
    private IFunction<SType<?>, CollectionEditorConfig> editorConfigFunction = t -> {
        CollectionEditorConfigBuilder collectionEditorConfigBuilder = new CollectionEditorConfigBuilder();
        collectionDefinition.configEditor(collectionEditorConfigBuilder, t);
        return collectionEditorConfigBuilder.getEditor();
    };

    CollectionCanvas(CollectionDefinition collectionDefinition, CollectionInfo collectionInfo) {
        this.collectionDefinition = collectionDefinition;
        this.collectionInfo = collectionInfo;
    }

    public CollectionInfo getCollectionInfo() {
        return collectionInfo;
    }

    public CollectionEditorConfig getEditorConfigFunction(SType<?> type) {
        return editorConfigFunction.apply(type);
    }

    public CollectionEditorConfig getEditorConfigFunction(IFunction<Class<SType<?>>, Optional<SType<?>>> typeLoader) {
        return editorConfigFunction.apply(typeLoader.apply(collectionInfo.getSTypeClass()).get());
    }
}