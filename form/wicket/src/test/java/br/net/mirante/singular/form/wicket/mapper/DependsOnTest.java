package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateInputBehavior;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class DependsOnTest extends AbstractWicketFormTest {

    protected PackageBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    private STypeComposite<?> baseCompositeField;

    private STypeString category, element;

    protected void setup() {
        createBaseType();
        loadTestType(baseCompositeField);
        setupPage();
    }

    private void createBaseType() {
        localPackage = dicionario.createNewPackage("test");
        baseCompositeField = localPackage.createTipoComposto("group");
    }

    private void setupPage() {
        driver = new WicketTester(new TestApp());

        page = new TestPage();
        page.setDicionario(dicionario);
        page.setNewInstanceOfType(baseCompositeField.getName());
    }

    protected void build() {
        page.build();
        driver.startPage(page);

        form = driver.newFormTester("test-form", false);
    }

    private static final Map<String, List<String>> OPTIONS =
            new ImmutableMap.Builder()
                    .put("fruits", Lists.newArrayList("avocado","apple","pineaple"))
                    .put("vegetables", Lists.newArrayList("cucumber","radish"))
                    .put("condiments", Lists.newArrayList("mustard","rosemary","coriander"))
                    .build();

    private void loadTestType(STypeComposite<?> baseCompositeField){
        category = baseCompositeField.addCampoString("category");
        element = baseCompositeField.addCampoString("element");

        category.as(SPackageBasic.aspect())
                .label("category");
        category.withSelectionOf(OPTIONS.keySet());

        element.as(SPackageBasic.aspect())
                .label("Word")
                .dependsOn(category);
        element.withSelectionFromProvider(ins -> {
            String prefix = ins.findNearest(category).get().getValue();
            return (prefix == null)
                    ? ins.getMTipo().novaLista()
                    : ins.getMTipo().novaLista()
                    .addValores(OPTIONS.getOrDefault(prefix, Lists.newArrayList()));
        });
    }

    @Test public void renderOnlyThePrimaryChoice() {
        setup();
        build();

        List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(2);

        DropDownChoice categoryChoice = options.get(0), elementChoice = options.get(1);

        assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                .containsOnly(OPTIONS.keySet().toArray());
        assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                .isEmpty();
    }

    @Test public void changingSelectionChangesValue() {
        setup();
        build();

        form.select(findId(form.getForm(), "category").get(), 0);
        form.submit("save-btn");

        List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);

        DropDownChoice categoryChoice = options.get(0), elementChoice = options.get(1);

        assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                .containsOnly(OPTIONS.keySet().toArray());
        assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                .containsOnly(OPTIONS.get("fruits").toArray());
    }

    @Test public void preloadSelectedValues() {
        setup();
        page.getCurrentInstance().getDescendant(category).setValue("vegetables");
        page.getCurrentInstance().getDescendant(element).setValue("radish");
        build();

        List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);

        DropDownChoice categoryChoice = options.get(0), elementChoice = options.get(1);

        assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                .containsOnly(OPTIONS.keySet().toArray());
        assertThat(categoryChoice.getValue()).isEqualTo("vegetables");
        assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                .containsOnly(OPTIONS.get("vegetables").toArray());
        assertThat(elementChoice.getValue()).isEqualTo("radish");
    }

    @Test public void addPreloadedOptionsToLisIfNotPresent() {
        setup();
        page.getCurrentInstance().getDescendant(category).setValue("special");
        page.getCurrentInstance().getDescendant(element).setValue("gluten");
        build();

        List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);

        DropDownChoice categoryChoice = options.get(0), elementChoice = options.get(1);

        assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                .contains("special");
        assertThat(categoryChoice.getValue()).isEqualTo("special");
        assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                .containsOnly("gluten");
        assertThat(elementChoice.getValue()).isEqualTo("gluten");
    }

    @Test public void addPreloadedOptionsToDependentLisIfNotPresent() {
        setup();
        page.getCurrentInstance().getDescendant(category).setValue("vegetables");
        page.getCurrentInstance().getDescendant(element).setValue("gluten");
        build();

        List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);

        DropDownChoice categoryChoice = options.get(0), elementChoice = options.get(1);

        assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                .contains("vegetables");
        assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                .contains("gluten").containsAll(OPTIONS.get("vegetables"));
    }

    @Test public void whenChangingValueRemovesDanglingOptions() {
        setup();
        page.getCurrentInstance().getDescendant(category).setValue("vegetables");
        page.getCurrentInstance().getDescendant(element).setValue("gluten");
        build();

//        form.select(findId(form.getForm(), "category").get(), 2);

        page.getCurrentInstance().getDescendant(category).setValue("condiments");

        List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);
        DropDownChoice categoryChoice = options.get(0), elementChoice = options.get(1);
        List<AjaxUpdateInputBehavior> behaviors = categoryChoice.getBehaviors(AjaxUpdateInputBehavior.class);
        behaviors.forEach((b) -> b.onUpdate(Mockito.mock(AjaxRequestTarget.class)));

//        form.submit("save-btn");

        options = (List)findTag(form.getForm(), DropDownChoice.class);

        categoryChoice = options.get(0);
        elementChoice = options.get(1);

        assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                .containsOnly(OPTIONS.get("condiments").toArray());
    }
}
