package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;

import com.google.common.collect.Lists;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

public class STypeStringSelectionFromProviderFieldTest extends SelectionFieldBaseTest {
    private List<String> referenceOptions = 
        Lists.newArrayList("strawberry", "apple", "orange", 
                            "banana", "avocado", "grapes");

    protected STypeString selectType;
    
    @Override
    @SuppressWarnings("rawtypes")
    SType createSelectionType(STypeComposite group) {
        return selectType = group.addCampoString("favoriteFruit");
    }
    
    @SuppressWarnings("serial")
    private MOptionsProvider createProviderWithOptions(final List<String> options) {
        return new MOptionsProvider() {
            public String toDebug() {
                return "debug this";
            }

            public SList<? extends SInstance> listOptions(SInstance optionsInstance) {
                SList<?> r = optionsInstance.getType().novaLista();
                options.forEach((o) -> {r.addValor(o);});
                return r;
            }
        };
    }
    
    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithSpecifiedOptionsByName() {
        setupPage();
        selectType.withSelectionFromProvider("fruitProvider");
        
        MOptionsProvider provider = createProviderWithOptions(referenceOptions);
        SDocument document = page.getCurrentInstance().getDocument();
        document.bindLocalService("fruitProvider", MOptionsProvider.class, RefService.of(provider));
        
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(extractProperty("value").from(choices.getChoices()))
            .containsExactly(getReferenceOptionsKeys().toArray());
    }
    
    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithSpecifiedOptionsByClass() {
        setupPage();
        selectType.withSelectionFromProvider(MOptionsProvider.class);
        
        MOptionsProvider provider = createProviderWithOptions(referenceOptions);
        SDocument document = page.getCurrentInstance().getDocument();
        document.bindLocalService(MOptionsProvider.class, RefService.of(provider));
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(extractProperty("value").from(choices.getChoices()))
        .containsExactly(getReferenceOptionsKeys().toArray());
    }

    List<?> getReferenceOptionsKeys(){
        return referenceOptions.stream().map(value -> getSelectKeyFromValue(value)).collect(Collectors.toList());
    }

    private Object getSelectKeyFromValue(String value) {
        SIString mvalue = selectType.novaInstancia();
        mvalue.setValue(value);
        return page.getCurrentInstance().getCampo("favoriteFruit").getOptionsConfig().getKeyFromOption(mvalue);
    }

}
