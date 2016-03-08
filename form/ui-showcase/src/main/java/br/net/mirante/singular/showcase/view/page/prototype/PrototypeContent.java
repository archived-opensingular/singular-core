package br.net.mirante.singular.showcase.view.page.prototype;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.showcase.dao.form.Prototype;
import br.net.mirante.singular.showcase.dao.form.PrototypeDAO;
import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;

/**
 * Created by nuk on 03/03/16.
 */
public class PrototypeContent extends Content {

    private static final SDictionary dictionary = SDictionary.create();

    @Inject @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    @Inject
    private PrototypeDAO prototypeDAO;

    static {
        dictionary.loadPackage(SPackagePrototype.class);
    }

    private MInstanceRootModel<SIComposite> model;

    public PrototypeContent(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form newItemForm = new Form("prototype_form");

        queue(new SingularFormPanel<String>("singular-panel", singularFormConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                SIComposite currentInstance = (SIComposite) SDocumentFactory.empty().createInstance(new RefType() {
                    protected SType<?> retrieve() {
                        return dictionary.getType(SPackagePrototype.META_FORM_COMPLETE);
                    }
                });
                model = new MInstanceRootModel<>(currentInstance);

                return currentInstance;
            }
        });

        newItemForm.add(new ActionAjaxButton("save-btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                Prototype prototype = new Prototype();
                prototype.setName("Legal");
                prototypeDAO.save(prototype);
            }
        });

        newItemForm.add(new ActionAjaxButton("preview_btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new PreviewPage(model));
            }
        });

        newItemForm.add(new ActionAjaxButton("cancel-btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new PrototypeListPage());
            }
        });
        queue(newItemForm);
    }


    @Override
    protected IModel<?> getContentTitleModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return new ResourceModel("label.content.title");
    }

}
