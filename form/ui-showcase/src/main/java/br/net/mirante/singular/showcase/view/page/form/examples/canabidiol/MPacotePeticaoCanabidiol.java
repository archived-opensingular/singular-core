package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import org.apache.commons.lang3.BooleanUtils;

public class MPacotePeticaoCanabidiol extends MPacote implements CanabidiolUtil {

    public static final String PACOTE = "mform.peticao.canabidiol";
    public static final String TIPO = "PeticionamentoCanabidiol";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public MPacotePeticaoCanabidiol() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        //Para que anotar com MTIPOInfo se depois tem que fazer isso:
        pb.createTipo(MTipoImportacao.class);
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
                    .visivel(instancia -> BooleanUtils.isTrue(getValue(instancia, possuiResponsavelLegal)))
                    .dependsOn(possuiResponsavelLegal);

            final MTipoComposto<?> anexos = canabis
                    .addCampoComposto("anexos");
            anexos
                    .as(AtrBasic::new)
                    .label("Anexos")
                    .dependsOn(paciente.getTipoDocumento(), responsavelLegal.getTipoDocumento())
                    .visivel(instancia -> hasValue(instancia, responsavelLegal.getTipoDocumento()) || hasValue(instancia, paciente.getTipoDocumento()));

            MTipoAttachment anexoPaciente = anexos
                    .addCampo("documentoPaciente", MTipoAttachment.class);
            anexoPaciente
                    .as(AtrBasic::new)
                    .label("Documento do Paciente")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\" do paciente", MTipoPessoa.LABEL_TIPO_DOCUMENTO))
                    .dependsOn(anexos, paciente.getTipoDocumento())
                    .visivel(instancia -> hasValue(instancia, paciente.getTipoDocumento()));

            MTipoAttachment anexoResponsavelLegal = anexos
                    .addCampo("documentoResponsavel", MTipoAttachment.class);
            anexoResponsavelLegal
                    .as(AtrBasic::new)
                    .label("Documento do Responsável Legal")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\" do responsável legal", MTipoPessoa.LABEL_TIPO_DOCUMENTO))
                    .dependsOn(anexos, responsavelLegal.getTipoDocumento())
                    .visivel(instancia -> hasValue(instancia, responsavelLegal.getTipoDocumento()));

            MTipoImportacao modalidadeImportacao = canabis
                    .addCampo("importacao", MTipoImportacao.class);

            modalidadeImportacao
                    .as(AtrBasic::new)
                    .label("Importação");

            // config tabs
            MTabView tabbed = canabis.setView(MTabView::new);
            tabbed.addTab("dados", "Solicitante")
                    .add(paciente)
                    .add(possuiResponsavelLegal)
                    .add(responsavelLegal)
                    .add(anexos);
            tabbed.addTab("solicitação", "Medicação")
                    .add(modalidadeImportacao);


        }
    }


}

