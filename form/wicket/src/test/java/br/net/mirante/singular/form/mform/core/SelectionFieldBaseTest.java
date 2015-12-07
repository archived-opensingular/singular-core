package br.net.mirante.singular.form.mform.core;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public abstract class SelectionFieldBaseTest {
    protected static MDicionario dicionario;
    protected PacoteBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    
    @BeforeClass
    public static void createDicionario() {
        dicionario = MDicionario.create();
    }

    protected void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage(null);
        page.setDicionario(dicionario);
        localPackage = dicionario.criarNovoPacote("test"+(int)(Math.random()*1000));
        MTipoComposto<? extends MIComposto> group = localPackage.createTipoComposto("group");
        createSelectionType(group);
        
        page.setNewInstanceOfType(group.getNome());
    }
    
    @SuppressWarnings("rawtypes")
    abstract MTipo createSelectionType(MTipoComposto group); 
    
    protected void buildPage() {
        page.build();
        driver.startPage(page);
        
        form = driver.newFormTester("test-form", false);
    }
}
