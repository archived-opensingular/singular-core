package br.net.mirante.singular.view.page.form;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.wicket.MInstanciaRaizModel;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

public class FormContent extends Content implements SingularWicketContainer<FormContent, Void> {

    public FormContent(String id, boolean withSideBar) {
        super(id, false, withSideBar, true);
    }

    static MDicionario dicionario = MDicionario.create();
    static {
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<? extends MIComposto> tContato = pb.createTipoComposto("contato");
        tContato.addCampoString("nome").as(MPacoteBasic.aspect()).label("Nome");

        MTipoComposto<? extends MIComposto> tEndereco = tContato.addCampoComposto("endereco");
        tEndereco.as(MPacoteBasic.aspect()).label("Endereço residencial");
        tEndereco.addCampoString("logradouro").as(MPacoteBasic.aspect()).label("Logradouro");
        tEndereco.addCampoInteger("numero").as(MPacoteBasic.aspect()).label("Número");
        tEndereco.addCampoString("complemento").as(MPacoteBasic.aspect()).label("Complemento");
        tEndereco.addCampoString("cidade").as(MPacoteBasic.aspect()).label("Cidade");
        tEndereco.addCampoString("uf").as(MPacoteBasic.aspect()).label("UF");
        tEndereco.addCampoInteger("cep").as(MPacoteBasic.aspect()).label("CEP");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onInitialize() {
        super.onInitialize();
        MTipoComposto<? extends MIComposto> tContato =
            (MTipoComposto<? extends MIComposto>) dicionario.getTipo("teste.contato");

        MIComposto iContato = tContato.novaInstancia();
        iContato.setValor("nome", "Fulano de Tal");
        MIComposto iEndereco = (MIComposto) iContato.getCampo("endereco");
        iEndereco.setValor("logradouro", "QNA 44");
        iEndereco.setValor("numero", 17);
        iEndereco.setValor("complemento", "Taguatinga");
        iEndereco.setValor("cidade", "Brasília");
        iEndereco.setValor("uf", "DF");
        iEndereco.setValor("cep", "72110440");

        IModel<MIComposto> mEndereco = new MInstanciaRaizModel<MIComposto>(iContato) {
            protected MTipo<MIComposto> getTipoRaiz() {
                return (MTipo<MIComposto>) dicionario.getTipo("teste.contato");
            }
        };

        WicketBuildContext ctx = new WicketBuildContext();

        add(new BSFeedbackPanel("feedback"));
        add(new Form<>("form")
            .add(UIBuilderWicket.createForEdit("generated", ctx, mEndereco))
            .add(new Button("enviar") {
                @Override
                public void onSubmit() {
                    MIComposto iEndereco = mEndereco.getObject();
                    StringWriter buffer = new StringWriter();
                    MformPersistenciaXML.toXML(iEndereco).printTabulado(new PrintWriter(buffer));
                    info(buffer.toString());
                }
            }));
    }
    @Override
    protected String getContentTitlelKey() {
        return "label.content.title";
    }

    @Override
    protected String getContentSubtitlelKey() {
        return "label.content.subtitle";
    }
}
