package br.net.mirante.singular.form.wicket.mapper.selection;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;

import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.wicket.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public abstract class SelectionFieldBaseTest extends AbstractWicketFormTest {

    protected PacoteBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    private static final AtomicInteger index = new AtomicInteger(0);

    protected void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage();
        page.setDicionario(dicionario);
        localPackage = dicionario.criarNovoPacote("test"+(index.getAndIncrement()));
        STypeComposite<? extends SIComposite> group = localPackage.createTipoComposto("group");
        createSelectionType(group);

        page.setNewInstanceOfType(group.getNome());
    }

    @SuppressWarnings("rawtypes")
    abstract SType createSelectionType(STypeComposite group);

    protected void buildPage() {
        page.build();
        driver.startPage(page);

        form = driver.newFormTester("test-form", false);
    }

    protected String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
}
