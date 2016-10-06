package org.opensingular.singular.studio.wicket;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.singular.studio.core.CollectionCanvas;
import org.opensingular.singular.studio.spring.StudioCollectionToolboxBean;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class SingularStudioFormPanel extends SingularStudioPanel {

    @Inject
    private StudioCollectionToolboxBean studioCollectionToolboxBean;

    private Form<?> form;
    private BSContainer formPanel;
    private SingularFormPanel<Class<SType<?>>> singularFormPanel;
    private FormKey formKey;
    private ViewMode viewMode;
    private AnnotationMode annotationMode;

    /**
     * Construtor do painel
     *
     * @param id             o markup id wicket
     * @param panelControl
     * @param canvas
     * @param formKey
     * @param viewMode
     * @param annotationMode
     */
    public SingularStudioFormPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, CollectionCanvas canvas, FormKey formKey, ViewMode viewMode, AnnotationMode annotationMode) {
        super(id, panelControl, canvas);
        this.formKey = formKey;
        this.viewMode = viewMode;
        this.annotationMode = annotationMode;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        queue(form = new Form<>("studio-form"));
        queue(formPanel = new BSContainer("form-panel"));
        addSingularFormPanel();
        saveButton();
        backButton();

    }

    protected void addSingularFormPanel() {
        formPanel.appendTag("div", true, "",
                singularFormPanel = new SingularFormPanel<Class<SType<?>>>("singular-form-panel", studioCollectionToolboxBean) {

                    @Override
                    public AnnotationMode getAnnotationMode() {
                        return annotationMode;
                    }

                    @Override
                    public ViewMode getViewMode() {
                        return viewMode;
                    }

                    @Override
                    protected SInstance createInstance(SFormConfig<Class<SType<?>>> singularFormConfig) {
                        if (formKey != null) {
                            return loadFormInstance();
                        } else {
                            return newFormInstance();
                        }
                    }
                });
    }

    private SInstance newFormInstance() {
        return studioCollectionToolboxBean.getDocumentFactory().createInstance(new RefType() {
            @Override
            protected SType<?> retrieve() {
                return sType();
            }
        });
    }

    private SInstance loadFormInstance() {
        return repository().load(formKey);
    }

    protected void saveButton() {
        queue(new SingularSaveButton("save-button", singularFormPanel.getRootInstance(), true) {

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                repository().insertOrUpdate(instanceModel.getObject(), null);
                showList(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                getLogger().error("Erro ao processar executar ação do botão salvar.");
            }

            @Override
            public boolean isVisible() {
                return viewMode == ViewMode.EDIT || annotationMode == AnnotationMode.EDIT;
            }
        });
    }

    protected void backButton() {
        queue(new AjaxLink("back-button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                showList(target);
            }
        });
    }
}
