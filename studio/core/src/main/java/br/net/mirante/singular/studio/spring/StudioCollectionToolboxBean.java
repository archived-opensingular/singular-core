package br.net.mirante.singular.studio.spring;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.TypeLoader;
import br.net.mirante.singular.form.persistence.FormPersistence;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.core.CollectionEditorConfig;
import br.net.mirante.singular.studio.core.CollectionGallery;
import br.net.mirante.singular.studio.core.CollectionInfo;
import br.net.mirante.singular.studio.persistence.StudioCollectionPersistenceFactory;

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
