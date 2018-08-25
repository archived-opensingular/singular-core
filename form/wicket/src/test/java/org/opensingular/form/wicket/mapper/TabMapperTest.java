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

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.serialize.ISerializer;
import org.apache.wicket.serialize.java.DeflatedJavaSerializer;
import org.apache.wicket.serialize.java.JavaSerializer;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.ObjectMeta;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.DummyPage;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.lib.wicket.util.application.FSTSerializer;
import org.opensingular.lib.wicket.util.application.LZ4Serializer;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    @Test
    public void testLastTabAsDefault() {
        SingularFormDummyPageTester ctx = new SingularFormDummyPageTester();
        ctx.getDummyPage().setTypeBuilder(TabMapperTest::createSimpleFormUsingTabDefault);
        ctx.getDummyPage().setAsEditView();
        ctx.startDummyPage();

        AssertionsWComponent assertionsTab = getAssertionsTab(ctx);
        assertTabMenuActive(ctx, "experiencia", true);
        assertTabMenuActive(ctx, "informacoes", false);
        assertTabContent(assertionsTab, "experiencia");
    }

    @Test
    public void testTabSerialization() throws IllegalAccessException {
        SingularFormDummyPageTester ctx = new SingularFormDummyPageTester();
        ctx.getDummyPage().setTypeBuilder(TabMapperTest::createSimpleForm);
        ctx.startDummyPage();

        clickAndCompare(1, 1, ctx);
        clickAndCompare(1, 100, ctx);
        clickAndCompare(1, 1000, ctx);


    }

    @Test
    public void testTabSerializationSpeed() throws InterruptedException {
        SingularFormDummyPageTester ctx = new SingularFormDummyPageTester();
        ctx.getDummyPage().setTypeBuilder(TabMapperTest::createSimpleForm);
        ctx.startDummyPage();
        WebPage page = ctx.getDummyPage();

        System.gc();
        Thread.yield();
        Thread.sleep(10);

        Map<String, ISerializer> map = new TreeMap<>();


        map.put("java", new JavaSerializer(""));
        map.put("fst", new FSTSerializer());
        map.put("lz4-fst", new LZ4Serializer(new FSTSerializer()));
        map.put("lz4-java", new LZ4Serializer(new JavaSerializer("1")));
        map.put("java deflated", new DeflatedJavaSerializer("21"));

        for (int i = 0; i < 10; i++) {
            System.out.println("Rodada " + (i + 1));
            for (Map.Entry<String, ISerializer> o : map.entrySet()) {
                long milis = new Date().getTime();
                o.getValue().serialize(page);
                System.out.println("Tempo gasto " + o.getKey() + "(ms):" + (new Date().getTime() - milis));
                System.gc();
                Thread.yield();
                Thread.sleep(10);
            }
        }

    }

    private void clickAndCompare(int numberOfclicksFirstCase, int numberOfClicksSecondCase, SingularFormDummyPageTester ctx) throws IllegalAccessException {

        JavaSerializer javaSerializer = new JavaSerializer("");

        AssertionsWComponent assertionsTab = getAssertionsTab(ctx);


        for (int i = 0; i < numberOfclicksFirstCase; i++) {
            clickOnTab(ctx, assertionsTab, 1);
            assertTabMenuActive(ctx, "experiencia", true);
            assertTabContent(assertionsTab, "experiencia");
        }

        byte[] firstCase = javaSerializer.serialize(ctx.getDummyPage());


        for (int i = 0; i < numberOfClicksSecondCase; i++) {
            clickOnTab(ctx, assertionsTab, 1);
            assertTabMenuActive(ctx, "experiencia", true);
            assertTabContent(assertionsTab, "experiencia");
        }

        byte[] secondCase = javaSerializer.serialize(ctx.getDummyPage());

        DummyPage pageFirstCase = (DummyPage) javaSerializer.deserialize(firstCase);

        Map<String, List<ObjectMeta.ObjectData>> mapFirstCase = ObjectMeta.hihihi(pageFirstCase);

        DummyPage pageSecondCase = (DummyPage) javaSerializer.deserialize(secondCase);

        Map<String, List<ObjectMeta.ObjectData>> mapSecondCase = ObjectMeta.hihihi(pageSecondCase);

        for (Map.Entry<String, List<ObjectMeta.ObjectData>> e : mapSecondCase.entrySet()) {
            List<ObjectMeta.ObjectData> metaTwo = e.getValue();
            List<ObjectMeta.ObjectData> metaOne = mapFirstCase.get(e.getKey());
            if (metaTwo != null && metaOne != null) {
                if (metaOne.size() != metaTwo.size()) {
                    System.out.println("Diff On " + e.getKey() + " -> was: " + metaOne.size() + " now is: " + metaTwo.size());
                }
            } else {
                System.out.println("NULL em UM");
            }
        }

        List<ObjectMeta.ObjectData> one = mapFirstCase.get(String.class.getName());
        List<ObjectMeta.ObjectData> two = mapSecondCase.get(String.class.getName());

        for (ObjectMeta.ObjectData objectData : two) {
            if (!one.stream().map(o -> o.value).collect(Collectors.toList()).contains(objectData.value)) {
                System.out.println("not found:" + objectData.value);
                ObjectMeta.printPath(objectData);
            }
        }

        for (ObjectMeta.ObjectData objectData : one) {
            if (!two.stream().map(o -> o.value).collect(Collectors.toList()).contains(objectData.value)) {
                System.out.println("not found:" + objectData.value);
                ObjectMeta.printPath(objectData);
            }
        }

        if (secondCase.length != firstCase.length) {
            double limiar = 0.0001D;
            double value  = Double.valueOf(Math.abs(secondCase.length - firstCase.length)) / Double.valueOf(Math.min(firstCase.length, secondCase.length));
            Assert.assertTrue("Tamanho da serializacao difere em mais de " + limiar * 100 + "% entre chamadas", value < limiar);
        }


    }


    public void testTab(Consumer<SingularFormDummyPageTester> config) {
        SingularFormDummyPageTester ctx = new SingularFormDummyPageTester();
        ctx.getDummyPage().setTypeBuilder(TabMapperTest::createSimpleForm);
        config.accept(ctx);
        ctx.startDummyPage();

        AssertionsWComponent assertionsTab = getAssertionsTab(ctx);
        assertTabMenuActive(ctx, "informacoes", true);
        assertTabContent(assertionsTab, "nome", "idade");

        clickOnTab(ctx, assertionsTab, 1);
        assertTabMenuActive(ctx, "experiencia", true);
        assertTabContent(assertionsTab, "experiencia");

        clickOnTab(ctx, assertionsTab, 0);
        assertTabMenuActive(ctx, "informacoes", true);
        assertTabContent(assertionsTab, "nome", "idade");
    }

    private void clickOnTab(SingularFormDummyPageTester ctx, AssertionsWComponent assertionsTab, int tabIndex) {
        ctx.clickLink(assertionsTab.getSubComponentWithId("tab").getSubComponentsWithId("tabAnchor").element(tabIndex)
                .getTarget());
    }

    private void assertTabMenuActive(SingularFormDummyPageTester ctx, String tabName, boolean expected) {
        Assert.assertEquals(String.format("Tab %s is not active on the menu.", tabName), expected,
            TagTester.createTagByAttribute(ctx.getLastResponseAsString(), "data-tab-name", tabName)
                    .getAttributeContains("class", "active")
        );
    }

    private void assertTabContent(AssertionsWComponent assertionsTab, String... expectedInstancesName) {
        AssertionsWComponent content = assertionsTab.getSubComponentWithId("tab-content").isNotNull();
        content.getSubComponentsWithSInstance().hasSize(expectedInstancesName.length);
        for(String name : expectedInstancesName) {
            content.getSubComponentWithTypeNameSimple(name).isNotNull();
        }
    }

    @Nonnull
    private AssertionsWComponent getAssertionsTab(SingularFormDummyPageTester ctx) {
        AssertionsWComponent assertionsTab = ctx.getAssertionsPage().getSubComponentForSInstance(
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

    private static void createSimpleFormUsingTabDefault(STypeComposite sTypeComposite) {
        createSimpleForm(sTypeComposite);
        SViewTab viewTab = (SViewTab) sTypeComposite.getView();
        viewTab.getTabs().get(1).setDefault();
    }
}
