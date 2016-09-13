package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.FormPersistence;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.core.CollectionEditorConfig;
import br.net.mirante.singular.studio.core.CollectionInfo;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;

public abstract class SingularStudioPanel extends Panel implements Loggable {


    private final CollectionCanvas canvas;

    private final SingularStudioCollectionPanel.PanelControl panelControl;

    protected WebMarkupContainer portletContainer;

    protected WebMarkupContainer portletBodyContainer;


    @Inject
    private StudioCollectionToolboxBean studioCollectionToolboxBean;

    public SingularStudioPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, CollectionCanvas canvas) {
        super(id);
        this.canvas = canvas;
        this.panelControl = panelControl;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(portletContainer = new WebMarkupContainer("portletContainer"));
        queue(portletBodyContainer = new WebMarkupContainer("portletBodyContainer"));
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

   protected void showForm(AjaxRequestTarget target, FormKey formKey) {
        panelControl.showForm(target, formKey, ViewMode.EDIT, AnnotationMode.NONE);
    }

    protected void showForm(AjaxRequestTarget target, FormKey formKey, ViewMode viewMode) {
        panelControl.showForm(target, formKey, viewMode, AnnotationMode.NONE);
    }

    protected void showForm(AjaxRequestTarget target, FormKey formKey, ViewMode viewMode, AnnotationMode annotationMode) {
        panelControl.showForm(target, formKey, viewMode, annotationMode);
    }

    protected void showList(AjaxRequestTarget target) {
        panelControl.showList(target);
    }

}