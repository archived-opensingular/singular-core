/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.singular.form.showcase.view.page.prototype;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Bytes;
import org.opensingular.form.*;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.component.SingularFormWicket;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.singular.form.showcase.view.template.Content;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.function.Consumer;

public class PreviewContent extends Content {

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    private Page backPage;

    private SInstanceRootModel<SIComposite> model;

    public PreviewContent(String id, SInstanceRootModel<SIComposite> model, Page backpage) {
        super(id);
        this.model = model;
        this.backPage = backpage;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        SingularFormWicket<?> enclosing = new SingularFormWicket<>("just-a-form");
        enclosing.setMultiPart(true);
        enclosing.setFileMaxSize(Bytes.MAX);
        enclosing.setMaxSize(Bytes.MAX);
        SingularFormPanel panel = new SingularFormPanel("singular-panel");
        panel.setInstanceFromType(() -> new TypeBuilder(PreviewContent.this.model.getObject()).createRootType());
        enclosing.add(panel);
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
    private       SIComposite    metaInformation;
    private       long           id;

    TypeBuilder(SIComposite metaInformation) {
        this.metaInformation = metaInformation;
        pkg = createPackage();
    }

    private PackageBuilder createPackage() {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(SPackagePrototype.class);
        return dictionary.createNewPackage("org.opensingular.form.preview");
    }

    public STypeComposite<? extends SIComposite> createRootType() {
        STypeComposite<?> root     = pkg.createCompositeType("root");
        SIList            children = (SIList) metaInformation.getField(SPackagePrototype.CHILDREN);
        root.asAtr().label(metaInformation.getValueString(SPackagePrototype.NAME));
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
        String   type        = descriptor.getValueString(SPackagePrototype.TYPE);
        SType<?> typeOfField = root.getDictionary().getType(type);

        SType<?> fieldType = addFieldType(root, descriptor, typeOfField);
        addAttributesIfAny(descriptor, fieldType);
        addCompositeFieldsIfNeeded(descriptor, typeOfField, fieldType);
    }

    private SType<?> addFieldType(STypeComposite<? extends SIComposite> root, SIComposite descriptor, SType<?> typeOfField) {
        String name    = descriptor.getValueString(SPackagePrototype.NAME);
        String genName = generateJavaIdentifier(name);
        if (isList(descriptor)) {
            return addListFieldType(root, typeOfField, name, genName);
        } else {
            return addSimpleOrCompositeFieldType(root, typeOfField, name, genName);
        }
    }

    private SType<?> addListFieldType(STypeComposite<? extends SIComposite> root, SType<?> typeOfField, String name, String genName) {
        STypeList fieldType = addAppropriateListFieldType(root, typeOfField, genName);
        fieldType.asAtr().label(name);
        return fieldType.getElementsType();
    }

    private STypeList addAppropriateListFieldType(STypeComposite<? extends SIComposite> root, SType<?> typeOfField, String genName) {
        if (typeOfField.isComposite()) {
            return root.addFieldListOfComposite(genName, "sub_" + genName);
        } else {
            return root.addFieldListOf(genName, typeOfField);
        }
    }

    private SType<?> addSimpleOrCompositeFieldType(STypeComposite<? extends SIComposite> root, SType<?> typeOfField, String name, String genName) {
        return root.addField(genName, typeOfField)
                .asAtr().label(name).getTipo();
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
        addAttributeIfExists(descriptor.getValueInteger(SPackagePrototype.TAMANHO_MAXIMO), fieldType.asAtr()::maxLength);
        addAttributeIfExists(descriptor.getValueInteger(SPackagePrototype.TAMANHO_INTEIRO_MAXIMO), fieldType.asAtr()::integerMaxLength);
        addAttributeIfExists(descriptor.getValueInteger(SPackagePrototype.TAMANHO_DECIMAL_MAXIMO), fieldType.asAtr()::fractionalMaxLength);
        addAttributeIfExists(descriptor.getValueBoolean(SPackagePrototype.CAMPO_OBRIGATORIO), fieldType.asAtr()::required);

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
        if (typeOfField.isComposite()) {
            SIList children = (SIList) descriptor.getField(SPackagePrototype.FIELDS);
            addChildFieldsIfAny((STypeComposite<? extends SIComposite>) fieldType, children);
        }
    }
}
