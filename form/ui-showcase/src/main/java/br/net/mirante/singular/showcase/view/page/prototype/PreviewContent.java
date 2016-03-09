package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.context.SFormConfig;
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
                        return new TypeBuilder(PreviewContent.this.model.getObject()).createRootType();
                    }
                });
            }
        });
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

class TypeBuilder {

    private SIComposite metaInformation;
    private final PackageBuilder pkg;

    TypeBuilder(SIComposite metaInformation){
        this.metaInformation = metaInformation;
        pkg = createPackage();
    }

    private PackageBuilder createPackage() {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(SPackagePrototype.class);
        return dictionary.createNewPackage("com.mirante.singular.preview");
    }

    public STypeComposite<? extends SIComposite> createRootType() {
        final STypeComposite<? extends SIComposite> root = pkg.createTipoComposto("root");
        SList children = (SList) metaInformation.getCampo(SPackagePrototype.CHILDREN);
        addChildFieldsIfAny(root, children);
        return root;
    }

    private void addChildFieldsIfAny(STypeComposite<? extends SIComposite> root,
                                     SIComposite descriptor) {
        SList children = (SList) descriptor.getCampo(SPackagePrototype.CHILDREN);
        addChildFieldsIfAny(root, children);
    }

    private void addChildFieldsIfAny(STypeComposite<? extends SIComposite> root, SList children) {
        if(!children.isEmptyOfData()){
            for(SIComposite f: (List<SIComposite>)children.getValores()){
                addField(root, f);
            }
        }
    }

    private void addField(STypeComposite<? extends SIComposite> root,
                                            SIComposite descriptor) {
        String name = descriptor.getValorString("name"),
                type = descriptor.getValorString("type");
        SType<?> typeOfField = root.getDictionary().getType(type);
        SType<?> fieldType = root.addCampo(name, typeOfField);
        fieldType.asAtrBasic().label(name);
        if(typeOfField instanceof STypeComposite){
            SList children = (SList) descriptor.getCampo(SPackagePrototype.FIELDS);
            addChildFieldsIfAny((STypeComposite<? extends SIComposite>) fieldType, children);
        }
    }
}
