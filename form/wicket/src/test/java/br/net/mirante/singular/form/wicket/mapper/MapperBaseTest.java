package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import static org.junit.Assert.assertNotNull;

public abstract class MapperBaseTest {

    protected WicketTester wicketTester;
    protected MDicionario dicionario;

    protected MTipoComposto<? extends MIComposto> form;
    protected TestPage testPage;

    @Before
    public void setUp() {
        wicketTester = new WicketTester(new TestApp());
        dicionario = MDicionario.create();
        PacoteBuilder pacoteBuilder = dicionario.criarNovoPacote("MonetarioMapperPackage");
        form = pacoteBuilder.createTipoComposto("form");
        appendPackageFields(form);
    }

    public FormTester startPage(ViewMode viewMode) {
        testPage = new TestPage(new PageParameters().add("viewMode", viewMode));
        testPage.setDicionario(dicionario);

        MIComposto formInstance = (MIComposto) dicionario.getTipo(form.getNome()).novaInstancia();
        assertNotNull(formInstance);
        mockFormValues(formInstance);
        testPage.setCurrentInstance(formInstance);
        testPage.build();
        wicketTester.startPage(testPage);

        wicketTester.assertRenderedPage(TestPage.class);
        return wicketTester.newFormTester("test-form");
    }

    public abstract void appendPackageFields(MTipoComposto<? extends MIComposto> form);

    public abstract void mockFormValues(MIComposto formInstance);

}