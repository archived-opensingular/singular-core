package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;
import java.util.stream.Collectors;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;

import com.google.common.collect.Lists;

import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

@RunWith(Enclosed.class)
public class STypeStringSelectionFromProviderFieldTest  {
    //TODO:Fabs

    private static class Base extends AbstractSingularFormTest {
        protected List<String> referenceOptions =
                Lists.newArrayList("strawberry", "apple", "orange",
                        "banana", "avocado", "grapes");

        protected STypeString selectType;
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
        }

        protected SOptionsProvider createProviderWithOptions(final List<String> options) {
            return new SOptionsProvider() {
                public String toDebug() {
                    return "debug this";
                }

                public SIList<? extends SInstance> listOptions(SInstance optionsInstance) {
                    SIList<?> r = optionsInstance.getType().newList();
                    options.forEach((o) -> {r.addValue(o);});
                    return r;
                }
            };
        }

        protected Object getSelectKeyFromValue(String value) {
            SIString mvalue = selectType.newInstance();
            mvalue.setValue(value);
            return page.getCurrentInstance().getField("favoriteFruit").getOptionsConfig().getKeyFromOption(mvalue);
        }

        List<?> getReferenceOptionsKeys(){
            return referenceOptions.stream().map(value -> getSelectKeyFromValue(value)).collect(Collectors.toList());
        }

        @Test
        public void rendersAnDropDownWithSpecifiedOptionsByName() {
            tester.assertEnabled(formField(form, "favoriteFruit"));
            form.submit();
            List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
            assertThat(options).hasSize(1);
            DropDownChoice choices = options.get(0);
            assertThat(extractProperty("value").from(choices.getChoices()))
                    .containsExactly(getReferenceOptionsKeys().toArray());
        }
    }

    public static class WithSpecifiedProviderBindedByName extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionFromProvider("fruitProvider");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            SOptionsProvider provider = createProviderWithOptions(referenceOptions);
            SDocument document = instance.getDocument();
            document.bindLocalService("fruitProvider", SOptionsProvider.class, RefService.of(provider));
        }

    }

    public static class WithSpecifiedProviderBindedByType extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionFromProvider(SOptionsProvider.class);
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            SOptionsProvider provider = createProviderWithOptions(referenceOptions);
            SDocument document = instance.getDocument();
            document.bindLocalService(SOptionsProvider.class, RefService.of(provider));
        }

    }

}
