package br.net.mirante.singular.form.wicket.mapper.selection;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;

public class MTipoSelectItemSelectionFieldTest extends SelectionFieldBaseTest {

    MTipoComposto selectType;
    MTipoSimples nomeUF;
    private MTipoString idUF;

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    MTipo createSelectionType(MTipoComposto group) {
        selectType = (MTipoComposto) group.addCampoComposto("originUF");
        idUF = selectType.addCampoString("id");
        nomeUF = selectType.addCampoString("nome");
        return selectType;
    }


    private MIComposto newSelectItem(String id, String descricao) {
        MIComposto instancia = (MIComposto) selectType.novaInstancia();
        instancia.setValor("id", id);
        instancia.setValor("nome", descricao);
        return instancia;
    }

    private MOptionsProvider newProviderFrom(MIComposto ...compostos){
        return new MOptionsProvider() {
            @Override
            public MILista<? extends MInstancia> listOptions(MInstancia optionsInstance) {
                MILista lista = selectType.novaLista();
                for (MIComposto composto : compostos){
                    MInstancia instancia = lista.addNovo();
                    Object value = Val.dehydrate(composto);
                    Val.hydrate(instancia, value);
                }
                return lista;
            }
        };
    }



    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
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
            .containsExactly("Distrito Federal","São Paulo");
    }

    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
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
                .containsExactly("Distrito Federal","São Paulo");
    }

    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithDanglingOptions() {
        setupPage();
        MIComposto value = currentSelectionInstance();
        value.setValor("id","GO");
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
            .containsExactly("Goias","Distrito Federal","São Paulo");
    }

    @Test public void submitsSelectedValue(){
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
        buildPage();
        form.select(findId(form.getForm(), "originUF").get(), 0);
        form.submit("save-btn");
        MIComposto value = currentSelectionInstance();
        assertThat(value.getValor(idUF)).isEqualTo("DF");
    }

    @Test public void alsoWorksWhenFieldIsMandatory(){
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
        selectType.withObrigatorio(true);
        buildPage();
        form.select(findId(form.getForm(), "originUF").get(), 0);
        form.submit("save-btn");
        MIComposto value = currentSelectionInstance();
        assertThat(value.getValor(idUF)).isEqualTo("DF");
    }


    private MIComposto currentSelectionInstance() {
        MIComposto currentInstance = page.getCurrentInstance();
        MIComposto value = (MIComposto) currentInstance.getAllFields().iterator().next();
        return value;
    }

    private Object getSelectKeyFromMInstancia(MInstancia instancia){
        return  getInstanciaSelect().getOptionsConfig().getKeyFromOptions(instancia);
    }

    private MInstancia getInstanciaSelect(){
        return page.getCurrentInstance().getCampo("originUF");
    }
    
}
