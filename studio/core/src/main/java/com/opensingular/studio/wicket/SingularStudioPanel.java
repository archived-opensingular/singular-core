package com.opensingular.studio.wicket;

import com.opensingular.studio.core.CollectionCanvas;
import com.opensingular.studio.core.CollectionEditorConfig;
import com.opensingular.studio.core.CollectionInfo;
import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormPersistence;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import com.opensingular.studio.spring.StudioCollectionToolboxBean;
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
