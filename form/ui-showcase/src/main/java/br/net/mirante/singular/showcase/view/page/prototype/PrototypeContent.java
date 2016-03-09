package br.net.mirante.singular.showcase.view.page.prototype;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.StringValue;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.showcase.dao.form.ExampleDataDTO;
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

    private Long idPrototype;
    protected Prototype prototype;

    static {
        dictionary.loadPackage(SPackagePrototype.class);
    }

    private MInstanceRootModel<SIComposite> model;
    private SingularFormPanel<String> singularFormPanel;

    public PrototypeContent(String id, StringValue idValue) {
        super(id);
        if (!idValue.isEmpty()) {
            idPrototype = idValue.toLong();
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form newItemForm = new Form("prototype_form");

        queue(buildSingularFormPanel());

        newItemForm.add(new ActionAjaxButton("save-btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                SIComposite instance = (SIComposite) singularFormPanel.getRootInstance().getObject();
                prototype.setName(instance.getValorString(SPackagePrototype.NAME_FIELD));
                prototype.setXml(getXmlFromInstance(instance));
                prototypeDAO.save(prototype);
                addToastrSuccessMessage("message.save.success");
            }

            private String getXmlFromInstance(SIComposite instance) {
                return Optional.ofNullable(MformPersistenciaXML.toXML(instance))
                        .map(MElement::toStringExato)
                        .orElse(null);
            }
        });

        newItemForm.add(new ActionAjaxButton("preview_btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                if (WicketFormProcessing.onFormSubmit(form, Optional.of(target), singularFormPanel.getRootInstance(), true)) {
                    setResponsePage(new PreviewPage(model, PrototypeContent.this.getPage()));
                } else {
                    addToastrWarningMessage("message.error.form");
                }
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

    private SingularFormPanel<String> buildSingularFormPanel() {
        singularFormPanel = new SingularFormPanel<String>("singular-panel", singularFormConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                loadOrBuildModel();

                SIComposite currentInstance = loadOrCreateInstance(new RefType() {
                    protected SType<?> retrieve() {
                        return dictionary.getType(SPackagePrototype.META_FORM_COMPLETE);
                    }
                });
                model = new MInstanceRootModel<>(currentInstance);

                return currentInstance;
            }

            private SIComposite loadOrCreateInstance(RefType refType) {
                String xml = prototype.getXml();
                SInstance instance;
                if (StringUtils.isBlank(xml)) {
                    instance = singularFormConfig.getDocumentFactory().createInstance(refType);
                } else {
                    instance = MformPersistenciaXML.fromXML(refType, xml, singularFormConfig.getDocumentFactory());
                }
                return (SIComposite) instance;
            }
        };
        return singularFormPanel;
    }

    protected void loadOrBuildModel() {
        if (idPrototype == null) {
            prototype = new Prototype();
        } else {
            prototype = prototypeDAO.findById(idPrototype);
        }
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
