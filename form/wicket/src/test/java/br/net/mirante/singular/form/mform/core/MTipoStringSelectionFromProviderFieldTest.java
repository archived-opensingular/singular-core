package br.net.mirante.singular.form.mform.core;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SDocument;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class MTipoStringSelectionFromProviderFieldTest {
    private static MDicionario dicionario;
    private PacoteBuilder localPackage;
    private MTipoString selectType;

    private WicketTester driver;
    private TestPage page;
    private FormTester form;
    private List<String> referenceOptions;

    @BeforeClass
    public static void createDicionario() {
        dicionario = MDicionario.create();
    }

    public void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage(null);
        page.setDicionario(dicionario);
        localPackage = dicionario.criarNovoPacote("test"+(int)(Math.random()*1000));
        MTipoComposto<? extends MIComposto> group = localPackage.createTipoComposto("group");
        selectType = group.addCampoString("favoriteFruit");
        
        page.setNewInstanceOfType(group.getNome());
        referenceOptions = Lists.newArrayList("strawberry", "apple", "orange", "banana", "avocado", "grapes");
    }

    @SuppressWarnings("serial")
    private ServiceRef<MOptionsProvider> ref(MOptionsProvider provider) {
        return new ServiceRef<MOptionsProvider>() {
            public MOptionsProvider get() {
                return provider;
            }
        };
    }

    private MOptionsProvider createProviderWithOptions(final List<String> options) {
        return new MOptionsProvider() {
            public String toDebug() {
                return "debug this";
            }

            public MILista<? extends MInstancia> getOpcoes(MInstancia optionsInstance) {
                MTipoString s = dicionario.getTipo(MTipoString.class);
                MILista<?> r = s.novaLista();
                options.forEach((o) -> {r.addValor(o);});
                return r;
            }
        };
    }

    private void buildPage() {
        page.build();
        driver.startPage(page);
        
        form = driver.newFormTester("test-form", false);
    }
    
    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithSpecifiedOptionsByName() {
        setupPage();
        selectType.withSelectionFromProvider("fruitProvider");
        
        MOptionsProvider provider = createProviderWithOptions(referenceOptions);
        SDocument document = page.getCurrentInstance().getDocument();
        document.bindLocalService("fruitProvider", ref(provider));
        
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(choices.getChoices()).containsExactly(referenceOptions.toArray());
    }
    
    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithSpecifiedOptionsByClass() {
        setupPage();
        selectType.withSelectionFromProvider(MOptionsProvider.class);
        
        MOptionsProvider provider = createProviderWithOptions(referenceOptions);
        SDocument document = page.getCurrentInstance().getDocument();
        document.bindLocalService(MOptionsProvider.class, ref(provider));
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(choices.getChoices()).containsExactly(referenceOptions.toArray());
    }

    private String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
}
