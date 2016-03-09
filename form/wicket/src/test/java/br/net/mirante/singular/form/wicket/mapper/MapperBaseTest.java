package br.net.mirante.singular.form.wicket.mapper;

import static org.junit.Assert.assertNotNull;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.wicket.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public abstract class MapperBaseTest extends AbstractWicketFormTest {

    protected WicketTester wicketTester;

    protected STypeComposite<? extends SIComposite> form;
    protected TestPage testPage;

    @Before
    public void setUp() {
        wicketTester = new WicketTester(new TestApp());
        PackageBuilder pacoteBuilder = dicionario.createNewPackage("MonetarioMapperPackage");
        form = pacoteBuilder.createTipoComposto("form");
        appendPackageFields(form);
    }

    public FormTester startPage(ViewMode viewMode) {
        testPage = new TestPage(new PageParameters().add("viewMode", viewMode));

        SIComposite formInstance = (SIComposite) createIntance(() -> form);

        assertNotNull(formInstance);
        mockFormValues(formInstance);
        testPage.setCurrentInstance(formInstance);
        testPage.build();
        wicketTester.startPage(testPage);

        wicketTester.assertRenderedPage(TestPage.class);
        return wicketTester.newFormTester("test-form");
    }

    public abstract void appendPackageFields(STypeComposite<? extends SIComposite> form);

    public abstract void mockFormValues(SIComposite formInstance);

}