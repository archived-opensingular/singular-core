package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.showcase.view.template.Content;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by nuk on 04/03/16.
 */
public class PreviewContent extends Content {

    @Inject
    @Named("formConfigWithDatabase")
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
        Form enclosing = new Form("just-a-form");
        enclosing.add(new SingularFormPanel<String>("singular-panel", singularFormConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                return SDocumentFactory.empty().createInstance(new RefType() {
                    protected SType<?> retrieve() {
                        return new TypeBuilder(PreviewContent.this.model.getObject()).createRootType();
                    }
                });
            }
        });
        queue(enclosing);
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

    private final PackageBuilder pkg;
    private SIComposite metaInformation;
    private long id;

    TypeBuilder(SIComposite metaInformation) {
        this.metaInformation = metaInformation;
        pkg = createPackage();
    }

    private PackageBuilder createPackage() {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(SPackagePrototype.class);
        return dictionary.createNewPackage("com.mirante.singular.preview");
    }

    public STypeComposite<? extends SIComposite> createRootType() {
        final STypeComposite<? extends SIComposite> root = pkg.createCompositeType("root");
        SIList children = (SIList) metaInformation.getField(SPackagePrototype.CHILDREN);
        root.asAtrBasic().label(metaInformation.getValueString(SPackagePrototype.NAME));
        addChildFieldsIfAny(root, children);
        return root;
    }

    private void addChildFieldsIfAny(STypeComposite<? extends SIComposite> root, SIList children) {
        if (!children.isEmptyOfData()) {
            for (SIComposite f : (List<SIComposite>) children.getValues()) {
                addField(root, f);
            }
        }
    }

    private void addField(STypeComposite<? extends SIComposite> root, SIComposite descriptor) {
        String type = descriptor.getValueString(SPackagePrototype.TYPE);
        SType<?> typeOfField = root.getDictionary().getType(type);

        SType<?> fieldType = addFieldType(root, descriptor, typeOfField);
        addAttributesIfAny(descriptor, fieldType);
        addCompositeFieldsIfNeeded(descriptor, typeOfField, fieldType);
    }

    private SType<?> addFieldType(STypeComposite<? extends SIComposite> root, SIComposite descriptor, SType<?> typeOfField) {
        String name = descriptor.getValueString(SPackagePrototype.NAME);
        String genName = generateJavaIdentifier(name);
        if (isList(descriptor)) {
            return addListFieldType(root, typeOfField, name, genName);
        } else {
            return addSimpleOrCompositeFieldType(root, typeOfField, name, genName);
        }
    }

    private SType<?> addListFieldType(STypeComposite<? extends SIComposite> root, SType<?> typeOfField, String name, String genName) {
        STypeList fieldType = addAppropriateListFieldType(root, typeOfField, genName);
        fieldType.asAtrBasic().label(name);
        return fieldType.getElementsType();
    }

    private STypeList addAppropriateListFieldType(STypeComposite<? extends SIComposite> root, SType<?> typeOfField, String genName) {
        if (typeOfField instanceof STypeComposite) {
            return root.addFieldListOfComposite(genName, "sub_" + genName);
        } else {
            return root.addFieldListOf(genName, typeOfField);
        }
    }

    private SType<?> addSimpleOrCompositeFieldType(STypeComposite<? extends SIComposite> root, SType<?> typeOfField, String name, String genName) {
        return root.addField(genName, typeOfField)
                .asAtrBasic().label(name).getTipo();
    }

    private String generateJavaIdentifier(String name) {
        id++;
        String javaIdentifier = SingularUtil.convertToJavaIdentity(name, true);
        if (javaIdentifier.isEmpty()) {
            return "id" + id;
        }
        return javaIdentifier + id;
    }

    private void addAttributesIfAny(SIComposite descriptor, SType<?> fieldType) {
        addAttributeIfExists(descriptor.getValueInteger(SPackagePrototype.TAMANHO_CAMPO), fieldType.asAtrBootstrap()::colPreference);
        addAttributeIfExists(descriptor.getValueInteger(SPackagePrototype.TAMANHO_MAXIMO), fieldType.asAtrBasic()::tamanhoMaximo);
        addAttributeIfExists(descriptor.getValueInteger(SPackagePrototype.TAMANHO_INTEIRO_MAXIMO), fieldType.asAtrBasic()::tamanhoInteiroMaximo);
        addAttributeIfExists(descriptor.getValueInteger(SPackagePrototype.TAMANHO_DECIMAL_MAXIMO), fieldType.asAtrBasic()::tamanhoDecimalMaximo);
        addAttributeIfExists(descriptor.getValueBoolean(SPackagePrototype.OBRIGATORIO), fieldType.asAtrCore()::obrigatorio);

    }

    private <T> void addAttributeIfExists(T valor, Consumer<T> attributeConsumer) {
        if (valor != null) {
            attributeConsumer.accept(valor);
        }
    }

    private boolean isList(SIComposite descriptor) {
        return notNull(descriptor.getValueBoolean(SPackagePrototype.IS_LIST), false);
    }

    private boolean notNull(Boolean v, boolean defaultValue) {
        return v == null ? defaultValue : v;
    }

    private void addCompositeFieldsIfNeeded(SIComposite descriptor,
                                            SType<?> typeOfField,
                                            SType<?> fieldType) {
        if (typeOfField instanceof STypeComposite) {
            SIList children = (SIList) descriptor.getField(SPackagePrototype.FIELDS);
            addChildFieldsIfAny((STypeComposite<? extends SIComposite>) fieldType, children);
        }
    }
}
