package org.opensingular.form.wicket.mapper.selection;

import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.provider.SimpleProvider;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.lib.commons.context.RefService;

import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.opensingular.form.wicket.helpers.TestFinders.findTag;

@RunWith(Enclosed.class)
public class STypeStringSelectionFromProviderFieldTest {

    public abstract static class Base extends SingularFormBaseTest {

        protected List<String> referenceOptions = Lists.newArrayList("strawberry", "apple", "orange", "banana", "avocado", "grapes");
        protected STypeString selectType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
        }

        protected SimpleProvider createProviderWithOptions(final List<String> options) {
            return (SimpleProvider<String, SInstance>) ins -> options;
        }

        protected Object getSelectKeyFromValue(String value) {
            SIString mvalue = selectType.newInstance();
            mvalue.setValue(value);
            return page.getInstance().getField("favoriteFruit").asAtrProvider().getIdFunction().apply(value);
        }

        List<?> getReferenceOptionsKeys() {
            return referenceOptions.stream().map(value -> getSelectKeyFromValue(value)).collect(Collectors.toList());
        }

        public void rendersAnDropDownWithSpecifiedOptionsByName() {
            tester.assertEnabled(formField(form, "favoriteFruit"));
            form.submit();
            List<DropDownChoice> options = findTag(form.getForm(), DropDownChoice.class);
            assertThat(options).hasSize(1);
            DropDownChoice choices = options.get(0);
            assertThat(choices.getChoices())
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
            SimpleProvider provider = createProviderWithOptions(referenceOptions);
            SDocument      document = instance.getDocument();
            document.bindLocalService("fruitProvider", SimpleProvider.class, RefService.of(provider));
        }

        @Test
        public void rendersAnDropDownWithSpecifiedOptionsByName() {
            super.rendersAnDropDownWithSpecifiedOptionsByName();
        }
    }



    public static class WithSpecifiedProviderBindedByType extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionFromProvider(SimpleProvider.class);
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            SimpleProvider provider = createProviderWithOptions(referenceOptions);
            SDocument      document = instance.getDocument();
            document.bindLocalService(SimpleProvider.class, RefService.of(provider));
        }

        @Test
        public void rendersAnDropDownWithSpecifiedOptionsByName() {
            super.rendersAnDropDownWithSpecifiedOptionsByName();
        }
    }
}
