package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;

public class MPacotePeticaoCanabidiol extends MPacote {

    public static final String PACOTE = "mform.peticao.canabidiol";
    public static final String TIPO = "PeticionamentoCanabidiol";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public MPacotePeticaoCanabidiol() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        //Para que anotar com MTIPOInfo se depois tem que fazer isso:
        pb.createTipo(MTipoContato.class);
        pb.createTipo(MTipoDocumentoSelect.class);
        pb.createTipo(MTipoEndereco.class);
        pb.createTipo(MTipoPessoa.class);

        final MTipoComposto<?> canabis = pb.createTipoComposto(TIPO);
        {
            final MTipoPessoa paciente = canabis.addCampo("paciente", MTipoPessoa.class);
            paciente
                    .as(AtrBasic::new)
                    .label("Dados do Paciente");

            final MTipoBoolean possuiResponsavelLegal = canabis.addCampoBoolean("possuiResponsavelLegal");
            possuiResponsavelLegal
                    .as(AtrBasic::new)
                    .label("Possui Responsável Legal?");

            final MTipoPessoa responsavelLegal = canabis.addCampo("responsavelLegal", MTipoPessoa.class);
            responsavelLegal
                    .as(AtrBasic::new)
                    .label("Responsável Legal")
                    .visivel(instancia -> instancia.findNearestValue(possuiResponsavelLegal, Boolean.class).orElse(false))
                    .dependsOn(possuiResponsavelLegal);

            MTipoComposto<?> anexos = responsavelLegal
                    .addCampoComposto("anexos");
            anexos
                    .as(AtrBasic::new)
                    .label("Anexos");

            //Não temo como configurar tamanho inicial? antes tinha
            MTipoAttachment anexo = responsavelLegal
                    .addCampo("documentoResponsavel", MTipoAttachment.class);

            anexo
                    .as(AtrBasic::new)
                    .label("Documento do Responsável Legal")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\"", MTipoPessoa.LABEL_TIPO_DOCUMENTO));

            MTabView tabbed = canabis.setView(MTabView::new);
            tabbed.addTab("dados", "Solicitante")
                    .add(paciente)
                    .add(possuiResponsavelLegal)
                    .add(responsavelLegal);
            tabbed.addTab("solicitação", "Medicação");




        }
    }

}

