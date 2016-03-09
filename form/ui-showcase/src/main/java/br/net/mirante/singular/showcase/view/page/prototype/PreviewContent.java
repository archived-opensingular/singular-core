package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.showcase.view.template.Content;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;
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

    private Page backPage;

    private MInstanceRootModel<SIComposite> model;

    public PreviewContent(String id, MInstanceRootModel<SIComposite> model, Page backpage) {
        super(id);
        this.model = model;
        this.backPage = backpage;
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
        queue(new Link("cancel-btn") {
            @Override
            public void onClick() {
                setResponsePage(backPage);
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
    private long id;

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
        String name = descriptor.getValorString(SPackagePrototype.NAME),
                type = descriptor.getValorString(SPackagePrototype.TYPE);
        Integer tamanhoCampo = descriptor.getValorInteger(SPackagePrototype.TAMANHO_CAMPO),
                tamanhoMaximo = descriptor.getValorInteger(SPackagePrototype.TAMANHO_MAXIMO),
                tamanhoInteiroMaximo = descriptor.getValorInteger(SPackagePrototype.TAMANHO_INTEIRO_MAXIMO),
                tamanhoDecimalMaximo = descriptor.getValorInteger(SPackagePrototype.TAMANHO_DECIMAL_MAXIMO);
        Boolean obrigatorio = descriptor.getValorBoolean(SPackagePrototype.OBRIGATORIO);

        SType<?> typeOfField = root.getDictionary().getType(type);
        SType<?> fieldType = root.addCampo(generateJavaIdentifier(name), typeOfField);
        fieldType.asAtrBasic().label(name);
        if (tamanhoCampo != null) {
            fieldType.asAtrBootstrap().colPreference(tamanhoCampo);
        }
        if (tamanhoMaximo != null) {
            fieldType.asAtrBasic().tamanhoMaximo(tamanhoMaximo);
        }
        if (tamanhoInteiroMaximo != null) {
            fieldType.asAtrBasic().tamanhoInteiroMaximo(tamanhoInteiroMaximo);
        }
        if (tamanhoDecimalMaximo != null) {
            fieldType.asAtrBasic().tamanhoDecimalMaximo(tamanhoDecimalMaximo);
        }
        if (obrigatorio != null) {
            fieldType.asAtrCore().obrigatorio(obrigatorio);
        }

        if(typeOfField instanceof STypeComposite){
            SList children = (SList) descriptor.getCampo(SPackagePrototype.FIELDS);
            addChildFieldsIfAny((STypeComposite<? extends SIComposite>) fieldType, children);
        }
    }

    private String generateJavaIdentifier(String name) {
        String javaIdentifier = SingularUtil.convertToJavaIdentity(name, true);
        if (javaIdentifier.isEmpty()) {
            javaIdentifier = "id" + id++;
        } else {
            javaIdentifier += id++;
        }
        return javaIdentifier;
    }
}
