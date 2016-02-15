package br.net.mirante.singular.form.wicket.mapper.selection;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.wicket.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class STypeStringKeyValueRadioTest extends AbstractWicketFormTest {

    protected PacoteBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    private STypeComposite<? extends SIComposite> baseCompositeField;
    private STypeString tipoDeMedia;

    protected void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage();
        page.setDicionario(dicionario);
        page.enableAnnotation();
        localPackage = dicionario.criarNovoPacote("test");
        baseCompositeField = localPackage.createTipoComposto("group");

        tipoDeMedia = baseCompositeField.addCampoString("tipoDeMedia");
        tipoDeMedia.withRadioView();
        tipoDeMedia.withSelectionFromProvider(new MOptionsProvider() {
            @Override
            public SList<? extends SInstance> listOptions(SInstance optionsInstance) {
                STypeString type = dicionario.getTipo(STypeString.class);
                SList<?> r = type.novaLista();
                r.addElement(newElement(type, "IMG", "Imagem"));
                r.addElement(newElement(type, "TXT", "Texto"));
                r.addElement(newElement(type, "BIN", "Binário"));
                return r;
            }

            private SIString newElement(STypeString type, String id, String label) {
                SIString e = type.novaInstancia();
                e.setValor(id);
                e.setSelectLabel(label);
                return e;
            }
        });
        tipoDeMedia.as(AtrBasic::new).label("Tipo do Arquivo");

        page.setNewInstanceOfType(baseCompositeField.getNome());
    }

    protected void buildPage() {
        page.build();
        driver.startPage(page);

        form = driver.newFormTester("test-form", false);
    }

    @Test public void rendersARadioChoiceWithInformedLabels(){
        setupPage();
        buildPage();

        List<RadioChoice> inputs = (List)findTag(form.getForm(), RadioChoice.class);
        assertThat(inputs).hasSize(1);
        assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
                .containsOnly("IMG", "TXT", "BIN");
        assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
.containsOnly("Imagem", "Texto", "Binário");
    }

    @Test public void rendersARadioChoiceWithInformedOptionsRegardlessOfSelection(){
        setupPage();
        page.getCurrentInstance().getDescendant(tipoDeMedia).setValor("TXT");
        buildPage();

        List<RadioChoice> inputs = (List)findTag(form.getForm(), RadioChoice.class);
        assertThat(inputs).hasSize(1);
        assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
                .containsOnly("IMG", "TXT", "BIN");
        assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
.containsOnly("Imagem", "Texto", "Binário");
        assertThat(inputs.get(0).getValue()).isEqualTo("TXT");
    }
}
