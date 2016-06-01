package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.persistence.FormPersistence;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.core.CollectionEditorConfig;
import br.net.mirante.singular.studio.core.CollectionInfo;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;

public interface SingularStudioPanel {

    public StudioCollectionToolboxBean getToolbox();

    public CollectionCanvas getCanvas();


    default public <T extends SInstance> FormPersistence<T> repository() {
        return getToolbox().repository(getCanvas());
    }

    default public CollectionEditorConfig editorConfig() {
        return getToolbox().editorConfig(getCanvas());
    }


    default public CollectionInfo collectionInfo() {
        return getToolbox().collectionInfo(getCanvas());
    }

    default public <T extends SInstance> SType<T> sType() {
        return getToolbox().sType(getCanvas());
    }

}
