/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.SViewListByForm;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;
import br.net.mirante.singular.form.mform.util.brasil.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.brasil.STypeCPF;
import br.net.mirante.singular.form.mform.util.brasil.STypeTelefoneNacional;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;
import br.net.mirante.singular.form.mform.util.comuns.STypePersonName;

public class SPackageCurriculo extends SPackage {

    public static final String PACOTE         = "mform.exemplo.curriculo";
    public static final String TIPO_CURRICULO = PACOTE + ".Curriculo";

    public SPackageCurriculo() {
        super("mform.exemplo.curriculo");
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> curriculo = pb.createCompositeType("Curriculo");
        {
            curriculo
                .as(AtrBasic::new).label("Currículo");
        }

        final STypeComposite<?> informacoesPessoais = curriculo.addFieldComposite("informacoesPessoais");
        final STypePersonName nome = informacoesPessoais.addField("nome", STypePersonName.class, true);
        final STypeCPF cpf = informacoesPessoais.addField("cpf", STypeCPF.class, true);
        final STypeDate dtNasc = informacoesPessoais.addFieldDate("dataNascimento", true);
        final STypeString estadoCivil = informacoesPessoais.addFieldString("estadoCivil", true)
            .withSelectionOf("Solteiro", "Casado", "Separado", "Divorciado", "Viúvo");

        STypeString tipoContato = pb.createType("tipoContato", STypeString.class)
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        final STypeList<STypeString, SIString> infoPub = informacoesPessoais.addFieldListOf("infoPub",
                tipoContato);
        {
            informacoesPessoais
                .as(AtrBasic::new).label("Informações Pessoais");
            nome
                .as(AtrBasic::new).label("Nome").subtitle("nome completo").tamanhoMaximo(50)
                .as(AtrBootstrap::new).colPreference(7);
            cpf
                .as(AtrBasic::new).subtitle("cadastro de pessoa física")
                .as(AtrBootstrap::new).colPreference(3);
            dtNasc
                .as(AtrBasic::new).label("Dt.Nasc.").subtitle("dd/mm/aaaa")
                .as(AtrBootstrap::new).colPreference(2);
            estadoCivil
                .withRadioView()
                .as(AtrBasic::new).label("Estado Civil")
                .as(AtrBootstrap::new).colPreference(2);
            infoPub
                .withRequired(true)
                .withView(SMultiSelectionBySelectView::new)
                .as(AtrBasic::new).label("Informação Pública")
                .as(AtrBootstrap::new).colPreference(2);
        }

        final STypeComposite<?> contatos = informacoesPessoais.addFieldComposite("contatos");
        final STypeEMail email = contatos.addField("email", STypeEMail.class, true);
        final STypeTelefoneNacional telFixo = contatos.addField("telefoneFixo", STypeTelefoneNacional.class);
        final STypeTelefoneNacional telFixo2 = contatos.addField("telefoneFixo2", STypeTelefoneNacional.class);
        final STypeTelefoneNacional telCel = contatos.addField("telefoneCelular", STypeTelefoneNacional.class);
        {
            contatos
                .as(AtrBasic::new).label("Contatos");
            email
                .as(AtrBasic::new).label("e-Mail")
                .as(AtrBootstrap::new).colPreference(6);
            telFixo
                .as(AtrBasic::new).label("Tel.Fixo")
                .as(AtrBootstrap::new).colPreference(2);
            telFixo2
                .as(AtrBasic::new).label("Tel.Fixo")
                .as(AtrBootstrap::new).colPreference(2);
            telCel
                .as(AtrBasic::new).label("Tel.Celular")
                .as(AtrBootstrap::new).colPreference(2);
        }

        final STypeComposite<?> referencia = curriculo.addFieldComposite("referencia");
        final STypeBoolean foiIndicado = referencia.addFieldBoolean("foiIndicado");
        final STypeBoolean refTemNaEmpresa = referencia.addFieldBoolean("conheceColaboradorNaEmpresa");
        final STypeString refQuemNaEmpresa = referencia.addFieldString("colaboradorContato");
        {
            referencia
                .as(AtrBasic::new).label("Referência");
            refTemNaEmpresa
                .as(AtrBasic::new).label("Conhece colaborador na empresa")
                .as(AtrBootstrap::new).colPreference(4);
            foiIndicado.withRadioView();
            foiIndicado
                .as(AtrBasic::new).label("Foi indicado")
                .as(AtrBootstrap::new).colPreference(3);
            refQuemNaEmpresa
                .as(AtrBasic::new).label("Colaborador")
                .as(AtrBootstrap::new).colPreference(5);
        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> formacao = curriculo.addFieldListOfComposite("formacaoAcademica", "cursoAcademico");
        final STypeComposite<?> cursoAcademico = formacao.getElementsType();
        final STypeString academicoTipo = cursoAcademico.addFieldString("tipo", true)
            .withSelectionOf("Graduação", "Pós-Graduação", "Mestrado", "Doutorado");
        final STypeString academicoNomeCurso = cursoAcademico.addFieldString("nomeCurso", true);
        final STypeString academicoInstituicao = cursoAcademico.addFieldString("instituicao", true);
        final STypeCNPJ academicoCNPJ = cursoAcademico.addField("cnpj", STypeCNPJ.class, false);
        final STypeInteger academicoCargaHoraria = cursoAcademico.addField("cargaHoraria", STypeInteger.class, true);
        final STypeYearMonth academicoMesConclusao = cursoAcademico.addField("mesConclusao", STypeYearMonth.class, true);
        {
            formacao
                .as(AtrBasic::new).label("Formação Acadêmica");
            academicoTipo
                .withRadioView()
                .as(AtrBasic::new).label("Tipo");
            academicoNomeCurso
                .as(AtrBasic::new).label("Nome");
            academicoInstituicao
                .as(AtrBasic::new).label("Instituição")
                .as(AtrBootstrap::new).colPreference(3);
            academicoCNPJ
                .as(AtrBootstrap::new).colPreference(3);
            academicoCargaHoraria
                .as(AtrBasic::new).label("Carga Horária (h)")
                .as(AtrBasic::new).tamanhoMaximo(5)
                .as(AtrBootstrap::new).colPreference(2);
            academicoMesConclusao
                .as(AtrBasic::new).label("Mês de Conclusão")
                .as(AtrBootstrap::new).colPreference(2);
        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> experiencias = curriculo.addFieldListOfComposite("experienciasProfissionais", "experiencia");
        final STypeComposite<?> experiencia = experiencias.getElementsType();
        final STypeYearMonth dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        final STypeYearMonth dtFimExperiencia = experiencia.addField("fim", STypeYearMonth.class);
        final STypeString empresa = experiencia.addFieldString("empresa", true);
        final STypeString cargo = experiencia.addFieldString("cargo", true);
        final STypeString atividades = experiencia.addFieldString("atividades");
        {
            experiencias
                .withView(SViewListByForm::new)
                .as(AtrBasic::new).label("Experiências profissionais");
            dtInicioExperiencia
                .as(AtrBasic::new).label("Data inicial")
                .as(AtrBootstrap::new).colPreference(2);
            dtFimExperiencia
                .as(AtrBasic::new).label("Data final")
                .as(AtrBootstrap::new).colPreference(2);
            empresa
                .as(AtrBasic::new).label("Empresa")
                .as(AtrBootstrap::new).colPreference(8);
            cargo
                .as(AtrBasic::new).label("Cargo");
            atividades
                .withTextAreaView()
                .as(AtrBasic::new).label("Atividades Desenvolvidas");
        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> certificacoes = curriculo.addFieldListOfComposite("certificacoes", "certificacao");
        final STypeComposite<?> certificacao = certificacoes.getElementsType();
        final STypeYearMonth dataCertificacao = certificacao.addField("data", STypeYearMonth.class, true);
        final STypeString entidadeCertificacao = certificacao.addFieldString("entidade", true);
        final STypeDate validadeCertificacao = certificacao.addFieldDate("validade");
        final STypeString nomeCertificacao = certificacao.addFieldString("nome", true);
        {
            certificacoes
                .withView(SViewListByTable::new)
                .as(AtrBasic::new).label("Certificações");
            certificacao
                .as(AtrBasic::new).label("Certificação");
            dataCertificacao
                .as(AtrBasic::new).label("Data")
                .as(AtrBootstrap::new).colPreference(2);
            entidadeCertificacao
                .as(AtrBasic::new).label("Entidade")
                .as(AtrBootstrap::new).colPreference(10);
            validadeCertificacao
                .as(AtrBasic::new).label("Validade")
                .as(AtrBootstrap::new).colPreference(2);
            nomeCertificacao
                .as(AtrBasic::new).label("Nome")
                .as(AtrBootstrap::new).colPreference(10);
        }

        final STypeString informacoesAdicionais = curriculo.addFieldString("informacoesAdicionais");
        {
            informacoesAdicionais
                    .withTextAreaView()
                    .as(AtrBasic::new).label("Informações adicionais");
        }

//        pb.debug();

        // Formatação
        // ---------------------------------------------------------------------------------------------
        SViewTab tabbed = curriculo.setView(SViewTab::new);
        tabbed.addTab("dados", "Dados")
            .add(informacoesPessoais)
            .add(referencia)
            .add(informacoesAdicionais);
        tabbed.addTab("formacaoCurso", "Formacção e Curso")
            .add(formacao)
            .add(certificacoes);
        tabbed.addTab(experiencias);

        // Comportamentos
        // ---------------------------------------------------------------------------------------------
        //        refTemNaEmpresa.withCode("onChange", (temRefNaMepresa) -> {
        //            temRefNaMepresa.getIrmao(refQuemNaEmpresa).as(AtrBasic::new).visivel(isTrue(temRefNaMepresa));
        //        });

        // Ou

        //        refTemNaEmpresa.withOnChange((temRefNaMepresa) -> {
        //            temRefNaMepresa.getIrmao(refQuemNaEmpresa).as(AtrBasic::new).visivel(isTrue(temRefNaMepresa));
        //        });

        // Ou

        //        refQuemNaEmpresa.withFunction("visivelControle", (MIString refQuem) -> isTrue(refQuem.getIrmao(refTemNaEmpresa)));

        // Validacoes
        // ---------------------------------------------------------------------------------------------
        // dtNasc.addValidacao(MTipoData.validadorBuilder().entre(Period.ofYears(-100), Period.ofYears(-14)).build());

        //        informacoesPessoais.addValidacao((IValidatable<MIComposto> validatable) -> {
        //            if (validatable.getValue().isCampoNull("telefoneFixo") && validatable.getValue().isCampoNull("telefoneCelular")) {
        //                validatable.error(new ValidationError("Ao menos um telefone deve ser preenchido."));
        //            }
        //        });

        //        academicoMesConclusao.addValidacao((IValidatable<MInstancia> validatable) -> {
        //            MIAnoMes mesConclusao = academicoMesConclusao.castInstancia(validatable.getValue());
        //            YearMonth conclusao = mesConclusao.getJavaYearMonth();
        //            YearMonth maximo = YearMonth.now().plus(4, ChronoUnit.YEARS);
        //            if (conclusao.isAfter(maximo)) {
        //                validatable.error(new ValidationError("O mês de conclusão pode ser no máximpo até " + maximo));
        //            } else {
        //                MIData dt = mesConclusao.getParent(curriculo).getFilho(dtNasc);
        //                if (isNotNull(dt)) {
        //                    YearMonth minimo = dt.getJavaYearMonth().plus(14, ChronoUnit.YEARS);
        //                    if (conclusao.isBefore(minimo)) {
        //                        validatable.error(new ValidationError("O mês de conclusão deve ser posterior a " + minimo));
        //                    }
        //                }
        //            }
        //        });

        //        dtFimExperiencia.addValidacao((IValidatable<MIAnoMes> validatable) -> {
        //            MIAnoMes fim = validatable.getValue();
        //            MIAnoMes inicio = fim.getIrmao(dtInicioExperiencia);
        //            if (isNotNull(inicio) && isNotNull(fim) && inicio.isAfter(fim)) {
        //                validatable.error(new ValidationError("O mês de conclusão não pode ser anterior ao início"));
        //            }
        //        });
    }
}
