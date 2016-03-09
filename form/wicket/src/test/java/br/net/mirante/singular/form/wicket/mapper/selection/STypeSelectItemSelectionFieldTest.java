package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import static br.net.mirante.singular.form.wicket.test.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.test.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

public class STypeSelectItemSelectionFieldTest extends SelectionFieldBaseTest {

    STypeComposite selectType;
    STypeSimple nomeUF;
    private STypeString idUF;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    SType createSelectionType(STypeComposite group) {
        selectType = (STypeComposite) group.addCampoComposto("originUF");
        idUF = selectType.addCampoString("id");
        nomeUF = selectType.addCampoString("nome");
        return selectType;
    }


    private SIComposite newSelectItem(String id, String descricao) {
        SIComposite instancia = (SIComposite) selectType.novaInstancia();
        instancia.setValor("id", id);
        instancia.setValor("nome", descricao);
        return instancia;
    }

    private MOptionsProvider newProviderFrom(SIComposite... compostos) {
        return new MOptionsProvider() {
            @Override
            public SList<? extends SInstance> listOptions(SInstance optionsInstance) {
                SList lista = selectType.novaLista();
                for (SIComposite composto : compostos) {
                    SInstance instancia = lista.addNovo();
                    Object value = Value.dehydrate(composto);
                    Value.hydrate(instancia, value);
                }
                return lista;
            }
        };
    }


    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void rendersAnDropDownWithSpecifiedOptionsByName() {
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
        buildPage();
        driver.assertEnabled(formField(form, "originUF"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        Object valueDF = getSelectKeyFromMInstancia(newSelectItem("DF", "Distrito Federal"));
        Object valueSP = getSelectKeyFromMInstancia(newSelectItem("SP", "São Paulo"));
        assertThat(extractProperty("value").from(choices.getChoices()))
                .containsExactly(valueDF, valueSP);
        assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                .containsExactly("Distrito Federal", "São Paulo");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void hasADefaultProvider() {
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
        buildPage();
        driver.assertEnabled(formField(form, "originUF"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);

        Object valueDF = getSelectKeyFromMInstancia(newSelectItem("DF", "Distrito Federal"));
        Object valueSP = getSelectKeyFromMInstancia(newSelectItem("SP", "São Paulo"));
        assertThat(extractProperty("value").from(choices.getChoices()))
                .containsExactly(
                        valueDF,
                        valueSP
                );
        assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                .containsExactly("Distrito Federal", "São Paulo");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void rendersAnDropDownWithDanglingOptions() {
        setupPage();
        SIComposite value = currentSelectionInstance();
        value.setValor("id", "GO");
        value.setValor("nome", "Goias");
        selectType.withSelectionFromProvider(nomeUF, newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
        buildPage();
        driver.assertEnabled(formField(form, "originUF"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        Object valueGO = getSelectKeyFromMInstancia(newSelectItem("GO", "Goias"));
        Object valueDF = getSelectKeyFromMInstancia(newSelectItem("DF", "Distrito Federal"));
        Object valueSP = getSelectKeyFromMInstancia(newSelectItem("SP", "São Paulo"));
        assertThat(extractProperty("value").from(choices.getChoices()))
                .containsExactly(valueGO, valueDF, valueSP);
        assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                .containsExactly("Goias", "Distrito Federal", "São Paulo");
    }

    @Test
    public void submitsSelectedValue() {
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
        buildPage();
        form.select(findId(form.getForm(), "originUF").get(), 0);
        form.submit("save-btn");
        SIComposite value = currentSelectionInstance();
        assertThat(value.getValor(idUF)).isEqualTo("DF");
    }

    @Test
    public void alsoWorksWhenFieldIsMandatory() {
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
        selectType.withObrigatorio(true);
        buildPage();
        form.select(findId(form.getForm(), "originUF").get(), 0);
        form.submit("save-btn");
        SIComposite value = currentSelectionInstance();
        assertThat(value.getValor(idUF)).isEqualTo("DF");
    }

    @Test
    public void verifiyIfSelectLabelIsCorrect() {
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
        buildPage();
        form.select(findId(form.getForm(), "originUF").get(), 0);
        form.submit("save-btn");
        SIComposite value = currentSelectionInstance();
        assertThat(value.getSelectLabel()).isEqualTo("Distrito Federal");
    }


    private SIComposite currentSelectionInstance() {
        SIComposite currentInstance = page.getCurrentInstance();
        SIComposite value = (SIComposite) currentInstance.getAllFields().iterator().next();
        return value;
    }

    private Object getSelectKeyFromMInstancia(SInstance instancia) {
        return getInstanciaSelect().getOptionsConfig().getKeyFromOptions(instancia);
    }

    private SInstance getInstanciaSelect() {
        return page.getCurrentInstance().getCampo("originUF");
    }

}
