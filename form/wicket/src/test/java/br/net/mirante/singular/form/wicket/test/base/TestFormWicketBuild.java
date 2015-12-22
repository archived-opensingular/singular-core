package br.net.mirante.singular.form.wicket.test.base;

import br.net.mirante.singular.form.curriculo.mform.MPacoteCurriculo;
import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.wicket.SingularFormConfigWicketImpl;
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.panel.FormPanel;
import junit.framework.TestCase;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.findContainerRelativePath;

public class TestFormWicketBuild extends TestCase {

    WicketTester tester;
    private SingularFormContextWicket singularFormContext = new SingularFormConfigWicketImpl().getContext();

    public void setUp() {
        tester = new WicketTester(new WebApplication() {
            @Override
            public Class<? extends Page> getHomePage() {
                return null;
            }
        });
    }

    public void testBasic() {
        BSGrid rootContainer = new BSGrid("teste");
        TestPanel testPanel = buildTestPanel(rootContainer);

        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer());
        IModel<MTipoString> tCidade = new LoadableDetachableModel<MTipoString>() {
            @Override
            protected MTipoString load() {
                MDicionario dicionario = MDicionario.create();
                PacoteBuilder pb = dicionario.criarNovoPacote("teste");
                MTipoString tipoCidade = pb.createTipo("cidade", MTipoString.class);
                tipoCidade.as(AtrBasic.class).label("Cidade").tamanhoEdicao(21);
                return tipoCidade;
            }
        };
        IModel<MIString> mCidade = new MInstanceRootModel<MIString>(tCidade.getObject().novaInstancia());
        mCidade.getObject().setValor("Brasilia");
        singularFormContext.getUIBuilder().build(ctx, mCidade, ViewMode.EDITION);

        tester.startComponentInPage(testPanel);
        assertEquals("Brasilia", mCidade.getObject().getValor());

        FormTester formTester = tester.newFormTester("body-child:container:form");
        formTester.setValue(findContainerRelativePath(formTester.getForm(), "cidade").get(), "Guará");
        formTester.submit();

        assertEquals("Guará", mCidade.getObject().getValor());
    }

    public void testCurriculo() {
        BSGrid rootContainer = new BSGrid("teste");
        TestPanel testPanel = buildTestPanel(rootContainer);

        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer());
        IModel<MTipoComposto<MIComposto>> tCurriculo = new LoadableDetachableModel<MTipoComposto<MIComposto>>() {
            @Override
            @SuppressWarnings("unchecked")
            protected MTipoComposto<MIComposto> load() {
                MDicionario dicionario = MDicionario.create();
                dicionario.carregarPacote(MPacoteCurriculo.class);
                return (MTipoComposto<MIComposto>) dicionario.getTipo(MPacoteCurriculo.TIPO_CURRICULO);
            }
        };
        IModel<MIComposto> mCurriculo = new MInstanceRootModel<MIComposto>(tCurriculo.getObject().novaInstancia());
//        UIBuilderWicket.buildForEdit(ctx, mCurriculo);


        tester.startComponentInPage(testPanel);
        FormTester formTester = tester.newFormTester("body-child:container:form");
        formTester.submit();
    }

    private TestPanel buildTestPanel(BSGrid rootContainer){
        Form<Object> form = new Form<>("form");

        TestPanel testPanel = new TestPanel("body-child"){
            @Override
            public Component buildContainer(String id) {
                return new FormPanel(id, form) {
                    @Override
                    protected Component newFormBody(String id) {
                        return new BSContainer<>(id).appendTag("div", rootContainer);
                    }
                };
            }
        };
        return testPanel;
    }
}
