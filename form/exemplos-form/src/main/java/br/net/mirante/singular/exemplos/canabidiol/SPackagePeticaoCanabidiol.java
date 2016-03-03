package br.net.mirante.singular.exemplos.canabidiol;

import org.apache.commons.lang3.BooleanUtils;

import br.net.mirante.singular.exemplos.canabidiol.custom.AceitoTudoMapper;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Value;

public class SPackagePeticaoCanabidiol extends SPackage {

    public static final String PACOTE = "mform.peticao.canabidiol";
    public static final String TIPO = "PeticionamentoCanabidiol";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackagePeticaoCanabidiol() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        //Para que anotar com MTIPOInfo se depois tem que fazer isso:
        pb.createTipo(STypeContato.class);
        pb.createTipo(STypeDocumentoSelect.class);
        pb.createTipo(STypeEndereco.class);
        pb.createTipo(STypeImportacao.class);
        pb.createTipo(STypeMedico.class);
        pb.createTipo(STypeCID.class);
        pb.createTipo(STypePessoa.class);
        pb.createTipo(STypePrescricao.class);
        pb.createTipo(STypeDescricaoProduto.class);
        pb.createTipo(STypeProdutos.class);

        final STypeComposite<?> canabis = pb.createTipoComposto(TIPO);
        canabis.as(AtrBasic::new).label("Peticionamento de Canabidiol");

        {
            final STypePessoa paciente = canabis.addCampo("paciente", STypePessoa.class);
            paciente
                    .as(AtrBasic::new)
                    .label("Dados do Paciente")
                    .as(AtrAnnotation::new).setAnnotated();

            final STypeBoolean possuiResponsavelLegal = canabis.addCampoBoolean("possuiResponsavelLegal");
            possuiResponsavelLegal
                    .as(AtrCore::new)
                    .obrigatorio()
                    .as(AtrBasic::new)
                    .label("Possui Responsável Legal?");

            final STypePessoa responsavelLegal = canabis.addCampo("responsavelLegal", STypePessoa.class);
            responsavelLegal
                    .as(AtrBasic::new)
                    .label("Responsável Legal")
                    .visivel(instancia -> BooleanUtils.isTrue(Value.of(instancia, possuiResponsavelLegal)))
                    .dependsOn(possuiResponsavelLegal);

            final STypeComposite<?> anexos = canabis
                    .addCampoComposto("anexos");
            anexos
                    .as(AtrBasic::new)
                    .label("Anexos")
                    .dependsOn(paciente.tipoDocumento, responsavelLegal.tipoDocumento)
                    .visivel(instancia -> Value.notNull(instancia, responsavelLegal.tipoDocumento) || Value.notNull(instancia, paciente.tipoDocumento));

            STypeAttachment anexoPaciente = anexos
                    .addCampo("documentoPaciente", STypeAttachment.class);
            anexoPaciente
                    .as(AtrCore::new)
                    .obrigatorio()
                    .as(AtrBasic::new)
                    .label("Documento do Paciente")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\" do paciente", STypePessoa.LABEL_TIPO_DOCUMENTO))
                    .dependsOn(anexos, paciente.tipoDocumento)
                    .visivel(instancia -> Value.notNull(instancia, paciente.tipoDocumento));

            STypeAttachment anexoResponsavelLegal = anexos
                    .addCampo("documentoResponsavel", STypeAttachment.class);
            anexoResponsavelLegal
                    .as(AtrCore::new)
                    .obrigatorio(instancia -> BooleanUtils.isTrue(Value.of(instancia, possuiResponsavelLegal)))
                    .as(AtrBasic::new)
                    .label("Documento do Responsável Legal")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\" do responsável legal", STypePessoa.LABEL_TIPO_DOCUMENTO))
                    .dependsOn(anexos, responsavelLegal.tipoDocumento)
                    .visivel(instancia -> Value.notNull(instancia, responsavelLegal.tipoDocumento));

            STypeImportacao modalidadeImportacao = canabis
                    .addCampo("importacao", STypeImportacao.class);
            modalidadeImportacao
                    .as(AtrBasic::new)
                    .label("Importação")
                    .as(AtrAnnotation::new).setAnnotated();


            STypeProdutos produtos = canabis
                    .addCampo("produtos", STypeProdutos.class);
            produtos
                    .as(AtrBasic::new)
                    .label("Produtos")
                    .as(AtrAnnotation::new).setAnnotated();


            STypePrescricao prescricao = canabis
                    .addCampo("prescricao", STypePrescricao.class);
            prescricao
                    .as(AtrBasic::new)
                    .label("Prescrição Médica");


            STypeComposite<?> termoResponsabilidade = canabis
                    .addCampoComposto("termoResponsabilidade");

            termoResponsabilidade
                    .as(AtrAnnotation::new).setAnnotated();

            STypeBoolean aceitoTudo = termoResponsabilidade.addCampoBoolean("aceitoTudo");

            aceitoTudo
                    .withCustomMapper(AceitoTudoMapper::new)
                    .as(AtrCore::new)
                    .obrigatorio()
                    .as(AtrBasic::new)
                    .label("Eu, paciente/responsável legal, informo que estou ciente que:\n\n" +
                            "1- Este produto não possui registro no Brasil, portanto não possui a sua segurança e eficácia avaliada e comprovada pela Anvisa, podendo causar reações adversas inesperadas ao paciente.\n" +
                            "2- Este produto é de uso estritamente pessoal e intransferível, sendo proibida a sua entrega a terceiros, doação, venda ou qualquer outra utilização diferente da indicada.\n" +
                            "3- Que a cópia do Ofício emitido pela Anvisa deve ser mantida junto ao PRODUTO, sempre que em trânsito, dentro ou fora do Brasil. ");

            STypeAttachment anexoTermoResponsabilidade = termoResponsabilidade
                    .addCampo("anexoTermoResponsabilidade", STypeAttachment.class);

            anexoTermoResponsabilidade
                    .as(AtrCore::new)
                    .obrigatorio()
                    .as(AtrBasic::new)
                    .label("Termo de Responsabilidade (Prescritor/Paciente/Responsável Legal)")
                    .subtitle("Deve ser anexado o termo preenchido e assinado pelo prescritor e paciente/responsável legal")
                    .as(AtrAnnotation::new).setAnnotated();

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
                    .add(termoResponsabilidade);
        }
    }


}

