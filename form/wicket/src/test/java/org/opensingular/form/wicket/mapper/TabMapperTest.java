/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author Daniel C. Bordin on 03/04/2017.
 */
public class TabMapperTest {

    @Test
    public void test_Edit() {
        testTab(ctx -> ctx.getDummyPage().setAsEditView());
    }

    @Test
    public void test_Edit_Annotation() {
        testTab(ctx -> {
            ctx.getDummyPage().setAsEditView();
            ctx.getDummyPage().getSingularFormPanel().setAnnotationMode(AnnotationMode.EDIT);
        });
        testTab(ctx -> ctx.getDummyPage().setAsEditView());
    }

    @Test
    public void test_ReadOnly() {
        testTab(ctx -> ctx.getDummyPage().setAsVisualizationView());
    }

    @Test
    public void test_ReadOnly_Annotation() {
        testTab(ctx -> {
            ctx.getDummyPage().setAsVisualizationView();
            ctx.getDummyPage().getSingularFormPanel().setAnnotationMode(AnnotationMode.EDIT);
        });
    }

    public void testTab(Consumer<SingularDummyFormPageTester> config) {
        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(TabMapperTest::createSimpleForm);
        config.accept(ctx);
        ctx.startDummyPage();

        AssertionsWComponent assertionsTab = getAssertionsTab(ctx);
        assertTabContent(assertionsTab, "nome", "idade");

        clickOnTab(ctx, assertionsTab, 1);
        assertTabContent(assertionsTab, "experiencia");

        clickOnTab(ctx, assertionsTab, 0);
        assertTabContent(assertionsTab, "nome", "idade");
    }

    private void clickOnTab(SingularDummyFormPageTester ctx, AssertionsWComponent assertionsTab, int tabIndex) {
        ctx.clickLink(assertionsTab.getSubCompomentWithId("tab").getSubCompomentsWithId("tabAnchor").get(tabIndex)
                .getTarget());
    }

    private void assertTabContent(AssertionsWComponent assertionsTab, String... expectedInstancesName) {
        AssertionsWComponent content = assertionsTab.getSubCompomentWithId("tab-content").isNotNull();
        content.getSubComponentsWithSInstance().isSize(expectedInstancesName.length);
        for(String name : expectedInstancesName) {
            content.getSubCompomentWithTypeNameSimple(name).isNotNull();
        }
    }

    @Nonnull
    private AssertionsWComponent getAssertionsTab(SingularDummyFormPageTester ctx) {
        AssertionsWComponent assertionsTab = ctx.getAssertionsPage().getSubCompomentForSInstance(
                ctx.getAssertionsInstance().getTarget());
        Assert.assertEquals(SViewTab.class, assertionsTab.assertSInstance().getTarget().getType().getView().getClass());
        return assertionsTab;
    }

    private static void createSimpleForm(STypeComposite testForm) {

        STypeString nome = testForm.addFieldString("nome");
        STypeInteger idade = testForm.addFieldInteger("idade");

        STypeComposite experiencia = testForm.addFieldComposite("experiencia");
        STypeString empresa = experiencia.addFieldString("empresa", true);
        STypeString cargo = experiencia.addFieldString("cargo", true);
        experiencia.asAtr().label("Experiencias");
        experiencia.asAtrAnnotation().setAnnotated();


        SViewTab tabbed = new SViewTab();
        tabbed.addTab("informacoes", "Informações pessoais")
                .add(nome)
                .add(idade);
        tabbed.addTab(experiencia);
        testForm.withView(tabbed);
    }
}
