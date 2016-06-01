package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.persistence.FormPersistence;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.core.CollectionEditorConfig;
import br.net.mirante.singular.studio.core.CollectionInfo;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;

public abstract class SingularStudioPanel extends Panel {


    private final CollectionCanvas canvas;

    private final SingularStudioCollectionPanel.PanelControl panelControl;


    @Inject
    private StudioCollectionToolboxBean studioCollectionToolboxBean;

    public SingularStudioPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, CollectionCanvas canvas) {
        super(id);
        this.canvas = canvas;
        this.panelControl = panelControl;
    }

    public StudioCollectionToolboxBean getToolbox() {
        return studioCollectionToolboxBean;
    }

    public CollectionCanvas getCanvas() {
        return canvas;
    }


    public <T extends SInstance> FormPersistence<T> repository() {
        return getToolbox().repository(getCanvas());
    }

    public CollectionEditorConfig editorConfig() {
        return getToolbox().editorConfig(getCanvas());
    }


    public CollectionInfo collectionInfo() {
        return getToolbox().collectionInfo(getCanvas());
    }

    public <T extends SInstance> SType<T> sType() {
        return getToolbox().sType(getCanvas());
    }

   protected void showForm(AjaxRequestTarget target, Object formId) {
        panelControl.showForm(target, formId);
    }

    protected void showList(AjaxRequestTarget target) {
        panelControl.showList(target);
    }

}
