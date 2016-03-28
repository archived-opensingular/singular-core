/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import org.apache.commons.lang3.BooleanUtils;

import br.net.mirante.singular.exemplos.canabidiol.custom.AceitoTudoMapper;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.SIBoolean;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
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
        pb.createType(STypeContato.class);
        pb.createType(STypeDocumentoSelect.class);
        pb.createType(STypeEndereco.class);
        pb.createType(STypeImportacao.class);
        pb.createType(STypeMedico.class);
        pb.createType(STypeCID.class);
        pb.createType(STypePessoa.class);
        pb.createType(STypePrescricao.class);
        pb.createType(STypeDescricaoProduto.class);
        pb.createType(STypeProdutos.class);

        final STypeComposite<?> canabis = pb.createCompositeType(TIPO);
        canabis.asAtrBasic().label("Peticionamento de Canabidiol");
        canabis.asAtrBasic().displayString("Paciente ${paciente.nome}");
        {
            final STypePessoa paciente = canabis.addField("paciente", STypePessoa.class);
            paciente
                    .asAtrBasic()
                    .label("Dados do Paciente")
                    .asAtrAnnotation().setAnnotated();

            final STypeBoolean possuiResponsavelLegal = canabis.addFieldBoolean("possuiResponsavelLegal");
            possuiResponsavelLegal
                    .asAtrCore()
                    .required()
                    .asAtrBasic()
                    .label("Possui Responsável Legal?");

            final STypePessoa responsavelLegal = canabis.addField("responsavelLegal", STypePessoa.class);
            responsavelLegal
                    .asAtrBasic()
                    .label("Responsável Legal")
                    .visivel(instancia -> BooleanUtils.isTrue(Value.of(instancia, possuiResponsavelLegal)))
                    .dependsOn(possuiResponsavelLegal);

            final STypeComposite<?> anexos = canabis
                    .addFieldComposite("anexos");
            anexos
                    .asAtrBasic()
                    .label("Anexos")
                    .dependsOn(paciente.tipoDocumento, responsavelLegal.tipoDocumento)
                    .visivel(instancia -> Value.notNull(instancia, responsavelLegal.tipoDocumento) || Value.notNull(instancia, paciente.tipoDocumento));

            STypeAttachment anexoPaciente = anexos
                    .addField("documentoPaciente", STypeAttachment.class);
            anexoPaciente
                    .asAtrCore()
                    .required()
                    .asAtrBasic()
                    .label("Documento do Paciente")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\" do paciente", STypePessoa.LABEL_TIPO_DOCUMENTO))
                    .dependsOn(anexos, paciente.tipoDocumento)
                    .visivel(instancia -> Value.notNull(instancia, paciente.tipoDocumento));

            STypeAttachment anexoResponsavelLegal = anexos
                    .addField("documentoResponsavel", STypeAttachment.class);
            anexoResponsavelLegal
                    .asAtrCore()
                    .required(instancia -> BooleanUtils.isTrue(Value.of(instancia, possuiResponsavelLegal)))
                    .asAtrBasic()
                    .label("Documento do Responsável Legal")
                    .subtitle(String.format("Conforme documento informado no campo \"%s\" do responsável legal", STypePessoa.LABEL_TIPO_DOCUMENTO))
                    .dependsOn(anexos, responsavelLegal.tipoDocumento)
                    .visivel(instancia -> Value.notNull(instancia, responsavelLegal.tipoDocumento));

            STypeImportacao modalidadeImportacao = canabis
                    .addField("importacao", STypeImportacao.class);
            modalidadeImportacao
                    .asAtrBasic()
                    .label("Importação")
                    .asAtrAnnotation().setAnnotated();


            STypeProdutos produtos = canabis
                    .addField("produtos", STypeProdutos.class);
            produtos
                    .asAtrBasic()
                    .label("Produtos")
                    .asAtrAnnotation().setAnnotated();


            STypePrescricao prescricao = canabis
                    .addField("prescricao", STypePrescricao.class);
            prescricao
                    .asAtrBasic()
                    .label("Prescrição Médica");


            STypeComposite<?> termoResponsabilidade = canabis
                    .addFieldComposite("termoResponsabilidade");

            termoResponsabilidade
                    .asAtrAnnotation().setAnnotated();

            STypeBoolean aceitoTudo = termoResponsabilidade.addFieldBoolean("aceitoTudo");

            aceitoTudo
                    .withCustomMapper(AceitoTudoMapper::new)
                    .addInstanceValidator(validatable -> {
                        SIBoolean instance = validatable.getInstance();
                        if (!Boolean.TRUE.equals(instance.getValue())) {
                            validatable.error("Campo obrigatório");
                        }
                    })
                    .asAtrBasic()
                    .label("Eu, paciente/responsável legal, informo que estou ciente que:\n\n" +
                            "1- Este produto não possui registro no Brasil, portanto não possui a sua segurança e eficácia avaliada e comprovada pela Anvisa, podendo causar reações adversas inesperadas ao paciente.\n" +
                            "2- Este produto é de uso estritamente pessoal e intransferível, sendo proibida a sua entrega a terceiros, doação, venda ou qualquer outra utilização diferente da indicada.\n" +
                            "3- Que a cópia do Ofício emitido pela Anvisa deve ser mantida junto ao PRODUTO, sempre que em trânsito, dentro ou fora do Brasil. ");

            STypeAttachment anexoTermoResponsabilidade = termoResponsabilidade
                    .addField("anexoTermoResponsabilidade", STypeAttachment.class);

            anexoTermoResponsabilidade
                    .asAtrCore()
                    .required()
                    .asAtrBasic()
                    .label("Termo de Responsabilidade (Prescritor/Paciente/Responsável Legal)")
                    .subtitle("Deve ser anexado o termo preenchido e assinado pelo prescritor e paciente/responsável legal")
                    .asAtrAnnotation().setAnnotated();

            // config tabs
            SViewTab tabbed = canabis.setView(SViewTab::new);
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

