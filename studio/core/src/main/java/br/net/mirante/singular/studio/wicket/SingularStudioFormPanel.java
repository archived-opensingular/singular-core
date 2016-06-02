package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class SingularStudioFormPanel extends SingularStudioPanel {

    @Inject
    private StudioCollectionToolboxBean studioCollectionToolboxBean;

    private Form<?> form;
    private BSContainer formPanel;
    private SingularFormPanel<Class<SType<?>>> singularFormPanel;
    private FormKey formKey;

    /**
     * Construtor do painel
     *
     * @param id           o markup id wicket
     * @param panelControl
     * @param formKey
     * @param canvas
     */
    public SingularStudioFormPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, CollectionCanvas canvas, FormKey formKey) {
        super(id, panelControl, canvas);
        this.formKey = formKey;
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
        queue(new AjaxButton("save-button", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                repository().insertOrUpdate(singularFormPanel.getRootInstance().getObject());
                showList(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                throw new RuntimeException("erro");
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
