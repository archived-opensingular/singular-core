package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import org.apache.commons.lang3.BooleanUtils;

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
        pb.createTipo(MTipoImportacao.class);
        pb.createTipo(MTipoMedico.class);
        pb.createTipo(MTipoCID.class);
        pb.createTipo(MTipoPessoa.class);
        pb.createTipo(MTipoPrescricao.class);
        pb.createTipo(MTipoDescricaoProduto.class);
        pb.createTipo(MTipoProdutos.class);

        final MTipoComposto<?> canabis = pb.createTipoComposto(TIPO);
        {
            final MTipoPessoa paciente = canabis.addCampo("paciente", MTipoPessoa.class);
            paciente
                    .as(AtrBasic::new)
                    .label("Dados do Paciente");

            final MTipoBoolean possuiResponsavelLegal = canabis.addCampoBoolean("possuiResponsavelLegal");
            possuiResponsavelLegal
                    .as(AtrCore::new)
                    .obrigatorio()
                    .as(AtrBasic::new)
                    .label("Possui Responsável Legal?");

            final MTipoPessoa responsavelLegal = canabis.addCampo("responsavelLegal", MTipoPessoa.class);
            responsavelLegal
                    .as(AtrBasic::new)
                    .label("Responsável Legal")
                    .visivel(instancia -> BooleanUtils.isTrue(Val.of(instancia, possuiResponsavelLegal)))
                    .dependsOn(possuiResponsavelLegal);

            final MTipoComposto<?> anexos = canabis
                    .addCampoComposto("anexos");
            anexos
                    .as(AtrBasic::new)
                    .label("Anexos")
                    .dependsOn(paciente.tipoDocumento, responsavelLegal.tipoDocumento)
                    .visivel(instancia -> Val.notNull(instancia, responsavelLegal.tipoDocumento) || Val.notNull(instancia, paciente.tipoDocumento));

            MTipoAttachment anexoPaciente = anexos
                    .addCampo("documentoPaciente", MTipoAttachment.class);
            anexoPaciente
                    .as(AtrCore::new)
                    .obrigatorio()
                    .as(AtrBasic::new)
                    .label("Documento do Paciente")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\" do paciente", MTipoPessoa.LABEL_TIPO_DOCUMENTO))
                    .dependsOn(anexos, paciente.tipoDocumento)
                    .visivel(instancia -> Val.notNull(instancia, paciente.tipoDocumento));

            MTipoAttachment anexoResponsavelLegal = anexos
                    .addCampo("documentoResponsavel", MTipoAttachment.class);
            anexoResponsavelLegal
                    .as(AtrCore::new)
                    .obrigatorio(instancia -> BooleanUtils.isTrue(Val.of(instancia, possuiResponsavelLegal)))
                    .as(AtrBasic::new)
                    .label("Documento do Responsável Legal")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\" do responsável legal", MTipoPessoa.LABEL_TIPO_DOCUMENTO))
                    .dependsOn(anexos, responsavelLegal.tipoDocumento)
                    .visivel(instancia -> Val.notNull(instancia, responsavelLegal.tipoDocumento));

            MTipoImportacao modalidadeImportacao = canabis
                    .addCampo("importacao", MTipoImportacao.class);
            modalidadeImportacao
                    .as(AtrBasic::new)
                    .label("Importação");


            MTipoProdutos produtos = canabis
                    .addCampo("produtos", MTipoProdutos.class);
            produtos
                    .as(AtrBasic::new)
                    .label("Produtos");


            MTipoPrescricao prescricao = canabis
                    .addCampo("prescricao", MTipoPrescricao.class);
            prescricao
                    .as(AtrBasic::new)
                    .label("Prescrição Médica");

            MTipoBoolean aceitoTudo = canabis.addCampoBoolean("aceitoTudo");

            aceitoTudo
                    .as(AtrCore::new)
                    .obrigatorio()
                    .as(AtrBasic::new)
                    .label("Eu, paciente/responsável legal, informo que estou ciente que:\n" +
                            "1- este produto não possui registro no Brasil, portanto não possui a sua segurança e eficácia avaliada e comprovada pela Anvisa, podendo causar reações adversas inesperadas ao paciente.\n" +
                            "2- este produto é de uso estritamente pessoal e intransferível, sendo proibida a sua entrega a terceiros, doação, venda ou qualquer outra utilização diferente da indicada.\n" +
                            "3- que a cópia do Ofício emitido pela Anvisa deve ser mantida junto ao PRODUTO, sempre que em trânsito, dentro ou fora do Brasil. ");

            MTipoAttachment termoResponsabilidade = canabis
                    .addCampo("termoResponsabilidade", MTipoAttachment.class);

            termoResponsabilidade
                    .as(AtrCore::new)
                    .obrigatorio()
                    .as(AtrBasic::new)
                    .label("Prescritor/Paciente/Responsável Legal")
                    .subtitle("Deve ser anexado o termo preenchido e assinado pelo prescritor e paciente/responsável legal");

            // config tabs
            MTabView tabbed = canabis.setView(MTabView::new);
            tabbed.addTab("dados", "Solicitante")
                    .add(paciente)
                    .add(possuiResponsavelLegal)
                    .add(responsavelLegal)
                    .add(anexos);
            tabbed.addTab("importacao", "Importação")
                    .add(modalidadeImportacao);
            tabbed.addTab("produtos", "Produtos")
                    .add(produtos);
            tabbed.addTab("prescricao", "Prescrição")
                    .add(prescricao);
            tabbed.addTab("termo", "Termo de Responsabilidade")
                    .add(aceitoTudo)
                    .add(termoResponsabilidade);
        }
    }


}

