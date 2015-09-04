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
import br.net.mirante.singular.form.mform.exemplo.curriculo.MPacoteCurriculo;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.wicket.MInstanciaRaizModel;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

public class FormContent extends Content implements SingularWicketContainer<FormContent, Void> {

    public FormContent(String id, boolean withSideBar) {
        super(id, false, withSideBar, true);
    }

    static MDicionario dicionario = MDicionario.create();
    static {
        dicionario.carregarPacote(MPacoteCurriculo.class);
        //        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        //
        //        MTipoComposto<? extends MIComposto> tContato = pb.createTipoComposto("contato");
        //        tContato.addCampoString("nome").as(MPacoteBasic.aspect()).label("Nome");
        //
        //        MTipoComposto<? extends MIComposto> tEndereco = tContato.addCampoComposto("endereco");
        //        tEndereco.as(MPacoteBasic.aspect()).label("Endereço residencial");
        //        tEndereco.addCampoString("logradouro").as(MPacoteBasic.aspect()).label("Logradouro");
        //        tEndereco.addCampoInteger("numero").as(MPacoteBasic.aspect()).label("Número");
        //        tEndereco.addCampoString("complemento").as(MPacoteBasic.aspect()).label("Complemento");
        //        tEndereco.addCampoString("cidade").as(MPacoteBasic.aspect()).label("Cidade");
        //        tEndereco.addCampoString("uf").as(MPacoteBasic.aspect()).label("UF");
        //        tEndereco.addCampoInteger("cep").as(MPacoteBasic.aspect()).label("CEP");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onInitialize() {
        super.onInitialize();
        MTipoComposto<? extends MIComposto> tCurriculo = (MTipoComposto<? extends MIComposto>)
            dicionario.getTipo("mform.exemplo.curriculo.Curriculo");
        //            dicionario.getTipo("teste.contato");
        //

        MIComposto iCurriculo = tCurriculo.novaInstancia();

        IModel<MIComposto> mCurriculo = new MInstanciaRaizModel<MIComposto>(iCurriculo) {
            @Override
            protected MTipo<MIComposto> getTipoRaiz(String nomeTipo) {
                return (MTipo<MIComposto>) dicionario.getTipo(nomeTipo);
            }
        };

        BSGrid container = new BSGrid("generated");
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow());
        UIBuilderWicket.buildForEdit(ctx, mCurriculo);
        add(new BSFeedbackPanel("feedback"));
        add(new Form<>("form")
            .add(container)
            .add(new Button("enviar") {
                @Override
                public void onSubmit() {
                    MIComposto iEndereco = mCurriculo.getObject();
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
