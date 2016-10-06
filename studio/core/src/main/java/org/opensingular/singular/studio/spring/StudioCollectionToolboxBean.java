package org.opensingular.singular.studio.spring;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.persistence.FormPersistence;
import org.opensingular.singular.studio.core.CollectionCanvas;
import org.opensingular.singular.studio.core.CollectionEditorConfig;
import org.opensingular.singular.studio.core.CollectionGallery;
import org.opensingular.singular.studio.core.CollectionInfo;
import org.opensingular.singular.studio.persistence.StudioCollectionPersistenceFactory;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class StudioCollectionToolboxBean implements SFormConfig<Class<SType<?>>> {

    private Map<CollectionCanvas, CollectionToolbox> toolboxMap = new HashMap<>();

    public abstract StudioCollectionPersistenceFactory getPersistenceFactory();

    public abstract CollectionGallery getCollectionGallery();

    protected CollectionToolbox getTools(CollectionCanvas canvas) {
        synchronized (canvas) {
            if (!toolboxMap.containsKey(canvas)) {
                CollectionToolbox tools = new CollectionToolbox();
                tools.canvas = canvas;
                tools.collectionType = getTypeLoader().loadType(canvas.getCollectionInfo().getSTypeClass()).get();
                tools.persistence = getPersistenceFactory().get(tools.collectionType);
                tools.collectionEditorConfig = canvas.getEditorConfigFunction(tools.collectionType);
                tools.collectionInfo = canvas.getCollectionInfo();
                toolboxMap.put(canvas, tools);
            }
            return toolboxMap.get(canvas);
        }
    }

    public <T extends SInstance> FormPersistence<T> repository(CollectionCanvas canvas) {
        return (FormPersistence<T>) getTools(canvas).persistence;
    }


    public <T extends SInstance> SType<T> sType(CollectionCanvas canvas) {
        return (SType<T>) getTools(canvas).collectionType;
    }

    public CollectionEditorConfig editorConfig(CollectionCanvas canvas) {
        return getTools(canvas).collectionEditorConfig;
    }

    public CollectionInfo collectionInfo(CollectionCanvas canvas) {
        return getTools(canvas).collectionInfo;
    }

    protected class CollectionToolbox {
        protected SType<?> collectionType;
        protected CollectionCanvas canvas;
        protected FormPersistence<?> persistence;
        protected CollectionEditorConfig collectionEditorConfig;
        public CollectionInfo collectionInfo;
    }

}
