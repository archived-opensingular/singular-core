package br.net.mirante.singular.view.page.form;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.exemplo.curriculo.MPacoteCurriculo;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaRaizModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

public class FormContent extends Content implements SingularWicketContainer<FormContent, Void> {

    public FormContent(String id, boolean withSideBar) {
        super(id, false, withSideBar, false, true);
    }

    static MDicionario dicionario = MDicionario.create();

    static {
        dicionario.carregarPacote(MPacoteCurriculo.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onInitialize() {
        super.onInitialize();

        IModel<MIComposto> mCurriculo = new MInstanciaRaizModel<MIComposto>() {
            @Override
            protected MTipo<MIComposto> getTipoRaiz() {
                return (MTipo<MIComposto>) dicionario.getTipo(MPacoteCurriculo.TIPO_CURRICULO);
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

                        MIComposto iCurriculo = mCurriculo.getObject();
                        StringWriter buffer = new StringWriter();
                        MformPersistenciaXML.toXML(iCurriculo).printTabulado(new PrintWriter(buffer));
                        info(buffer.toString());

                        MILista<MInstancia> listaCurso = (MILista<MInstancia>) iCurriculo.getCampo("formacaoAcademica");
                        listaCurso.addNovo();
                    }
                }));
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return new Fragment(id, "breadcrumbForm", this);
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }
}
