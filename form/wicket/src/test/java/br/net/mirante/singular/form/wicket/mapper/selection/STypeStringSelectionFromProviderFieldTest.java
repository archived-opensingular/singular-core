package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.provider.SimpleProvider;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeStringSelectionFromProviderFieldTest {

    private static class Base extends SingularFormBaseTest {

        protected List<String> referenceOptions = Lists.newArrayList("strawberry", "apple", "orange", "banana", "avocado", "grapes");
        protected STypeString selectType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
        }

        protected SimpleProvider createProviderWithOptions(final List<String> options) {
            return (SimpleProvider<String>) ins -> options;
        }

        protected Object getSelectKeyFromValue(String value) {
            SIString mvalue = selectType.newInstance();
            mvalue.setValue(value);
            return page.getCurrentInstance().getField("favoriteFruit").asAtrProvider().getIdFunction().apply(value);
        }

        List<?> getReferenceOptionsKeys() {
            return referenceOptions.stream().map(value -> getSelectKeyFromValue(value)).collect(Collectors.toList());
        }

        @Test
        public void rendersAnDropDownWithSpecifiedOptionsByName() {
            tester.assertEnabled(formField(form, "favoriteFruit"));
            form.submit();
            List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
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

    }

}
