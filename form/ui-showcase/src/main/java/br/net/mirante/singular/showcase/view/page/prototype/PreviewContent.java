package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.showcase.view.template.Content;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by nuk on 04/03/16.
 */
public class PreviewContent extends Content {

    @Inject @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    private MInstanceRootModel<SIComposite> model;

    public PreviewContent(String id, MInstanceRootModel<SIComposite> model) {
        super(id);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(new SingularFormPanel<String>("singular-panel", singularFormConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                return SDocumentFactory.empty().createInstance(new RefType(){
                    protected SType<?> retrieve() {
                        SDictionary dictionary = SDictionary.create();
                        PackageBuilder pkg = dictionary.createNewPackage("com.mirante.singular.preview");
                        final STypeComposite<? extends SIComposite> root =
                                createRootType(pkg);
                        return root;
                    }
                });
            }
        });
    }

    private STypeComposite<? extends SIComposite> createRootType(PackageBuilder pkg) {
        final STypeComposite<? extends SIComposite> root = pkg.createTipoComposto("root");
        SList children = (SList) model.getObject().getCampo(SPackagePrototype.CHILDREN);
        addChildFieldsIfAny(root, children);
        return root;
    }

    private void addChildFieldsIfAny(STypeComposite<? extends SIComposite> root, SList children) {
        if(!children.isEmptyOfData()){
            for(SIComposite f: (List<SIComposite>)children.getValores()){
                addField(root, f);
            }
        }
    }

    private void addField(STypeComposite<? extends SIComposite> root, SIComposite f) {
        String name = f.getValorString("name"), type = f.getValorString("type");
        SType<?> typeOfField = root.getDictionary().getType(type);
        root.addCampo(name, typeOfField).asAtrBasic().label(name);
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
