package br.net.mirante.singular.form.wicket.test.base;

import br.net.mirante.singular.form.curriculo.mform.SPackageCurriculo;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
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

        IModel<STypeString> tCidade = new LoadableDetachableModel<STypeString>() {
            @Override
            protected STypeString load() {
                SDictionary dicionario = SDictionary.create();
                PacoteBuilder pb = dicionario.criarNovoPacote("teste");
                STypeString tipoCidade = pb.createTipo("cidade", STypeString.class);
                tipoCidade.as(AtrBasic.class).label("Cidade").tamanhoEdicao(21);
                return tipoCidade;
            }
        };
        IModel<SIString> mCidade = new MInstanceRootModel<SIString>(tCidade.getObject().novaInstancia());
        mCidade.getObject().setValor("Brasilia");
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCidade);
        singularFormContext.getUIBuilder().build(ctx, ViewMode.EDITION);

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

        IModel<STypeComposite<SIComposite>> tCurriculo = new LoadableDetachableModel<STypeComposite<SIComposite>>() {
            @Override
            @SuppressWarnings("unchecked")
            protected STypeComposite<SIComposite> load() {
                SDictionary dicionario = SDictionary.create();
                dicionario.carregarPacote(SPackageCurriculo.class);
                return (STypeComposite<SIComposite>) dicionario.getTipo(SPackageCurriculo.TIPO_CURRICULO);
            }
        };
        IModel<SIComposite> mCurriculo = new MInstanceRootModel<SIComposite>(tCurriculo.getObject().novaInstancia());
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCurriculo);
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
