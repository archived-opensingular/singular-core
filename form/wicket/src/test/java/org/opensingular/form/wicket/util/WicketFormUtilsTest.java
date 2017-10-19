/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.util;

import static org.junit.Assert.*;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.SILong;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.DummyPage;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.util.WicketUtils;

@SuppressWarnings("rawtypes")
public class WicketFormUtilsTest {

    static class TypeBuilder implements IConsumer<STypeComposite> {
        @Override
        @SuppressWarnings("unchecked")
        public void accept(STypeComposite root) {
            root.asAtr().label("FORM");
            root.addField("id", STypeLong.class, true).asAtr().label("ID");
            root.addField("descricao", STypeString.class, true).asAtr().label("Descrição");

            STypeList items = root.addFieldListOfComposite("items", "item");
            items.asAtr().label("Items");
            STypeComposite<?> item = (STypeComposite<?>) items.getElementsType();
            item.asAtr().label("Item");
            item.addField("codigo", STypeString.class).asAtr().label("COD");
            item.addField("nome", STypeString.class).asAtr().label("Nome");
        }
    }

    @Test
    public void test() {
        TypeBuilder typeBuilder = new TypeBuilder();

        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(typeBuilder);

        tester.startDummyPage();

        DummyPage page = (DummyPage) tester.getLastRenderedPage();
        SIComposite rootInstance = page.getInstance();
        SInstance idInstance = rootInstance.getField("id");
        SInstance descricaoInstance = rootInstance.getField("descricao");

        MarkupContainer rootComponent = (MarkupContainer) WicketFormUtils.findChildByInstance(page, rootInstance).get();
        Component idComponent = WicketFormUtils.findChildByInstance(rootComponent, idInstance).get();
        Component descricaoComponent = WicketFormUtils.findChildByInstance(rootComponent, descricaoInstance).get();

        assertSame(rootInstance, WicketFormUtils.resolveInstance(rootComponent).get());
        assertTrue(WicketFormUtils.findCellContainer(rootComponent).isPresent());
        assertTrue(WicketFormUtils.findCellContainer(rootComponent.iterator().next()).isPresent());

        assertTrue(idComponent.getDefaultModelObject() instanceof SILong);
        assertTrue(descricaoComponent.getDefaultModelObject() instanceof SIString);

        assertSame(rootComponent, WicketFormUtils.getRootContainer(idComponent));
        assertTrue(WicketFormUtils.isForInstance(idComponent, idInstance));

        assertSame(idComponent, WicketFormUtils.findUpdatableComponentInHierarchy(idComponent));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void generateTitlePath() {
        TypeBuilder typeBuilder = new TypeBuilder();

        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(typeBuilder);

        tester.startDummyPage();

        DummyPage page = (DummyPage) tester.getLastRenderedPage();
        SIComposite rootInstance = page.getInstance();
        SInstance idInstance = rootInstance.getField("id");
        SInstance descricaoInstance = rootInstance.getField("descricao");
        SIList<SIComposite> itemsInstance = (SIList<SIComposite>) rootInstance.getFieldList("items");

        idInstance.setValue(1L);
        descricaoInstance.setValue("XXX");

        SIComposite item0Instance = itemsInstance.addNew();
        item0Instance.setValue("codigo", "A");
        item0Instance.setValue("nome", "AAA");
        
        SInstance item0CodigoInstance = item0Instance.getField("codigo");
        
        tester.startPage(page);
        

        MarkupContainer rootComponent = (MarkupContainer) WicketFormUtils.findChildByInstance(page, rootInstance).get();
        MarkupContainer idComponent = (MarkupContainer) WicketFormUtils.findChildByInstance(rootComponent, idInstance).get();
        MarkupContainer descricaoComponent = (MarkupContainer) WicketFormUtils.findChildByInstance(rootComponent, descricaoInstance).get();
        
        Component idTextField = WicketUtils.findFirstChild(idComponent, Component.class, it -> it.getDefaultModel() instanceof SInstanceValueModel<?>).get();
        Component descricaoTextField = WicketUtils.findFirstChild(descricaoComponent, Component.class, it -> it.getDefaultModel() instanceof SInstanceValueModel<?>).get();
        Component item0CodigoField = WicketFormUtils.findChildByInstance(rootComponent, item0CodigoInstance).get();

        assertEquals("FORM", WicketFormUtils.generateTitlePath(rootComponent, rootInstance, idTextField, idInstance));
        assertEquals("FORM", WicketFormUtils.generateTitlePath(rootComponent, rootInstance, descricaoTextField, descricaoInstance));
        assertEquals("FORM > Items > Item", WicketFormUtils.generateTitlePath(rootComponent, rootInstance, item0CodigoField, item0CodigoInstance));
    }

}
