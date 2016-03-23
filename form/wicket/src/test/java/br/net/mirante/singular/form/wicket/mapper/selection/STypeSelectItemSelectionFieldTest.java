package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

@RunWith(Enclosed.class)
public class STypeSelectItemSelectionFieldTest {

    private static class Base extends SingularFormBaseTest {
        STypeComposite selectType;
        STypeSimple nomeUF;
        STypeString idUF;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = (STypeComposite) baseType.addFieldComposite("originUF");
            idUF = selectType.addFieldString("id");
            nomeUF = selectType.addFieldString("nome");
        }

        protected SIComposite newSelectItem(String id, String descricao) {
            SIComposite instancia = (SIComposite) selectType.newInstance();
            instancia.setValue("id", id);
            instancia.setValue("nome", descricao);
            return instancia;
        }

        protected SOptionsProvider newProviderFrom(SIComposite... compostos) {
            return new SOptionsProvider() {
                @Override
                public SIList<? extends SInstance> listOptions(
                        SInstance optionsInstance, String filter) {
                    SIList lista = selectType.newList();
                    for (SIComposite composto : compostos) {
                        SInstance instancia = lista.addNew();
                        Object value = Value.dehydrate(composto);
                        Value.hydrate(instancia, value);
                    }
                    return lista;
                }
            };
        }

        protected SIComposite currentSelectionInstance(SIComposite instance) {
            SIComposite value = (SIComposite) instance.getAllFields().iterator().next();
            return value;
        }

        protected Object getSelectKeyFromMInstancia(SInstance instancia) {
            return getInstanciaSelect().getOptionsConfig().getKeyFromOption(instancia);
        }

        protected SInstance getInstanciaSelect() {
            return page.getCurrentInstance().getField("originUF");
        }

        protected List<DropDownChoice> options() {
            return (List) findTag(form.getForm(), DropDownChoice.class);
        }
    }

    public static class WithProvider extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionFromProvider(nomeUF,
                    newProviderFrom(newSelectItem("DF", "Distrito Federal"),
                            newSelectItem("SP", "São Paulo")));
        }

        @Test public void rendersFiedl(){
            tester.assertEnabled(formField(form, "originUF"));
        }

        @Test public void rendersAnDropDownWithSpecifiedOptionsByName() {
            assertThat(options()).hasSize(1);
            DropDownChoice choices = options().get(0);
            Object valueDF = getSelectKeyFromMInstancia(newSelectItem("DF", "Distrito Federal"));
            Object valueSP = getSelectKeyFromMInstancia(newSelectItem("SP", "São Paulo"));
            assertThat(extractProperty("value").from(choices.getChoices()))
                    .containsExactly(valueDF, valueSP);
            assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                    .containsExactly("Distrito Federal", "São Paulo");
        }

        @Test public void submitsSelectedValue() {
            form.select(findId(form.getForm(), "originUF").get(), 0);
            form.submit();
            SIComposite value = currentSelectionInstance(page.getCurrentInstance());
            assertThat(value.getValue(idUF)).isEqualTo("DF");
        }

    }

    public static class WithDanglingOptions extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionFromProvider(nomeUF,
                newProviderFrom(newSelectItem("DF", "Distrito Federal"),
                        newSelectItem("SP", "São Paulo")));
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            SIComposite value = currentSelectionInstance(instance);
            value.setValue("id", "GO");
            value.setValue("nome", "Goias");
        }

        @Test public void rendersAnDropDownWithDanglingOptions() {
            DropDownChoice choices = options().get(0);
            Object valueGO = getSelectKeyFromMInstancia(newSelectItem("GO", "Goias"));
            Object valueDF = getSelectKeyFromMInstancia(newSelectItem("DF", "Distrito Federal"));
            Object valueSP = getSelectKeyFromMInstancia(newSelectItem("SP", "São Paulo"));
            assertThat(extractProperty("value").from(choices.getChoices()))
                    .containsExactly(valueGO, valueDF, valueSP);
            assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                    .containsExactly("Goias", "Distrito Federal", "São Paulo");
        }
    }

    public static class WithMandatoryFIeld extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionFromProvider(nomeUF,
                    newProviderFrom(newSelectItem("DF", "Distrito Federal"),
                            newSelectItem("SP", "São Paulo")));
            selectType.withRequired(true);
        }
        @Test public void alsoWorksWhenFieldIsMandatory() {
            form.select(findId(form.getForm(), "originUF").get(), 0);
            form.submit();
            SIComposite value = currentSelectionInstance(page.getCurrentInstance());
            assertThat(value.getValue(idUF)).isEqualTo("DF");
        }
    }

    public static class WithIncorrectLabel extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionFromProvider(nomeUF,
                    newProviderFrom(newSelectItem("DF", "Distrito Federal"),
                            newSelectItem("SP", "São Paulo")));
        }

        @Test
        public void verifiyIfSelectLabelIsCorrect() {
            form.select(findId(form.getForm(), "originUF").get(), 0);
            form.submit();
            SIComposite value = currentSelectionInstance(page.getCurrentInstance());
            assertThat(value.getSelectLabel()).isEqualTo("Distrito Federal");
        }
    }

}
