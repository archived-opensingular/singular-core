package br.net.mirante.singular.form.wicket;

import java.util.function.Supplier;

import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;

public abstract class AbstractWicketFormTest {

    protected SDictionary dicionario;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;

    @Before
    public void setUpDicionario() {
        dicionario = SDictionary.create();
    }

    protected static SInstance createIntance(Supplier<SType<?>> typeSupplier) {
        RefType ref = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return typeSupplier.get();
            }
        };
        return SDocumentFactory.empty().createInstance(ref);
    }

    protected void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage();
    }

    protected void buildPage() {
        page.build();
        driver.startPage(page);

        form = driver.newFormTester("test-form", false);
    }

}
