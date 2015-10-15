package br.net.mirante.singular.form.wicket;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.exemplo.curriculo.MPacoteCurriculo;
import br.net.mirante.singular.form.wicket.model.instancia.MInstanciaRaizModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.panel.FormPanel;
import junit.framework.TestCase;

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

    public void testBasic() {
        BSGrid rootContainer = new BSGrid("teste");
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow());
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
        IModel<MIString> mCidade = new MInstanciaRaizModel<MIString>() {
            @Override
            protected MTipoString getTipoRaiz() {
                return tCidade.getObject();
            }
        };
        mCidade.getObject().setValor("Brasilia");
        UIBuilderWicket.buildForEdit(ctx, mCidade);

        Form<Object> form = new Form<>("form");
        tester.startComponentInPage(new FormPanel("panel", form) {
            @Override
            protected Component newFormBody(String id) {
                return new BSContainer<>(id).appendTag("div", rootContainer);
            }
        });
        assertEquals("Brasilia", mCidade.getObject().getValor());

        FormTester formTester = tester.newFormTester("panel:form");
        formTester.setValue(findContainerRelativePath(formTester.getForm(), "cidade").get(), "Guará");
        formTester.submit();

        assertEquals("Guará", mCidade.getObject().getValor());
    }

    public void testCurriculo() {
        BSGrid rootContainer = new BSGrid("teste");
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow());
        IModel<MTipoComposto<MIComposto>> tCurriculo = new LoadableDetachableModel<MTipoComposto<MIComposto>>() {
            @Override
            @SuppressWarnings("unchecked")
            protected MTipoComposto<MIComposto> load() {
                MDicionario dicionario = MDicionario.create();
                dicionario.carregarPacote(MPacoteCurriculo.class);
                return (MTipoComposto<MIComposto>) dicionario.getTipo(MPacoteCurriculo.TIPO_CURRICULO);
            }
        };
        IModel<MIComposto> mCurriculo = new MInstanciaRaizModel<MIComposto>() {
            @Override
            protected MTipoComposto<MIComposto> getTipoRaiz() {
                return tCurriculo.getObject();
            }
        };
        UIBuilderWicket.buildForEdit(ctx, mCurriculo);

        Form<Object> form = new Form<>("form");
        tester.startComponentInPage(new FormPanel("panel", form) {
            @Override
            protected Component newFormBody(String id) {
                return new BSContainer<>(id).appendTag("div", rootContainer);
            }
        });

        FormTester formTester = tester.newFormTester("panel:form");
        formTester.submit();
    }
}
