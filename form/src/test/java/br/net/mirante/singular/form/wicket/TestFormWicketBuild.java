package br.net.mirante.singular.form.wicket;

import junit.framework.TestCase;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.panel.FormPanel;

public class TestFormWicketBuild extends TestCase {

    WicketTester tester;

    public void setUp() {
        tester = new WicketTester(new WebApplication() {
            @Override
            public Class<? extends Page> getHomePage() {
                return null;
            }
        });
    }

    public void testVeryBasic() {
        WicketBuildContext ctx = new WicketBuildContext();
        IModel<MIString> mCidade = new LoadableDetachableModel<MIString>() {
            @Override
            protected MIString load() {
                MDicionario dicionario = MDicionario.create();
                PacoteBuilder pb = dicionario.criarNovoPacote("teste");
                MTipoString tipoCidade = pb.createTipo("cidade", MTipoString.class);
                tipoCidade.as(AtrBasic.class).label("Cidade").tamanhoEdicao(21);

                MIString iCidade = tipoCidade.novaInstancia();
                iCidade.setValor("Brasilia");
                return iCidade;
            }
        };
        FormComponent<?> comp = (FormComponent<?>) UIBuilderWicket.createForEdit("cidade", ctx, mCidade);
        assertTrue(comp instanceof TextField);
        assertEquals("Cidade", comp.getLabel().getObject());
        assertEquals("Brasilia", comp.getModelObject());

        Form<Object> form = new Form<>("form");
        tester.startComponentInPage(new FormPanel("panel", form) {
            @Override
            protected Component newFormBody(String id) {
                BSContainer<?> layout = new BSContainer<>(id);
                layout.appendTag("input", comp);
                return layout;
            }
        });
        FormTester formTester = tester.newFormTester("panel:form");
        formTester.setValue(FormPanel.ID_FORM_BODY + ":_:1:cidade", "Guará");
        comp.processInput();
        //        formTester.submit();
        form.process(null);

        assertEquals("Guará", mCidade.getObject().getValor());
    }
}
