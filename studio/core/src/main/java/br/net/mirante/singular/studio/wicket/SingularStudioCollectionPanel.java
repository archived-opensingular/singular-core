package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;
import java.io.Serializable;

public class SingularStudioCollectionPanel extends Panel {

    private final PanelControl panelControl = new PanelControl();
    private final CollectionCanvas canvas;
    private boolean showList = true;
    private FormKey formKey = null;
    private ViewMode viewMode = ViewMode.EDITION;
    private AnnotationMode annotationMode = AnnotationMode.NONE;

    public SingularStudioCollectionPanel(String content, CollectionCanvas canvas) {
        super(content);
        this.canvas = canvas;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (showList) {
            this.addOrReplace(new SingularStudioListPanel("content", panelControl, canvas));
        } else {
            this.addOrReplace(new SingularStudioFormPanel("content", panelControl, canvas, formKey, viewMode, annotationMode));
        }
    }

    public class PanelControl implements Serializable {

        public void setList() {
            SingularStudioCollectionPanel.this.showList = true;
        }

        public void setForm(FormKey formKey, ViewMode viewMode, AnnotationMode annotationMode) {
            SingularStudioCollectionPanel.this.showList = false;
            SingularStudioCollectionPanel.this.formKey = formKey;
            SingularStudioCollectionPanel.this.viewMode = viewMode;
            SingularStudioCollectionPanel.this.annotationMode = annotationMode;
        }

        public void showList(AjaxRequestTarget target) {
            setList();
            target.add(SingularStudioCollectionPanel.this);
        }

        /**
         * @param target
         * @param formKey Se nulo, cadastrar um novo form
         */
        public void showForm(AjaxRequestTarget target, FormKey formKey, ViewMode viewMode, AnnotationMode annotationMode) {
            setForm(formKey, viewMode, annotationMode);
            target.add(SingularStudioCollectionPanel.this);
        }
    }
}
