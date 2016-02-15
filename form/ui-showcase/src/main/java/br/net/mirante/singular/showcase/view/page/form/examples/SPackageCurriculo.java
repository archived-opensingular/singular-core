package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;
import br.net.mirante.singular.form.mform.util.comuns.STypeNomePessoa;
import br.net.mirante.singular.form.mform.util.comuns.STypeTelefoneNacional;

public class SPackageCurriculo extends SPackage {

    public static final String PACOTE         = "mform.exemplo.curriculo";
    public static final String TIPO_CURRICULO = PACOTE + ".Curriculo";

    public SPackageCurriculo() {
        super("mform.exemplo.curriculo");
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> curriculo = pb.createTipoComposto("Curriculo");
        {
            curriculo
                .as(AtrBasic::new).label("Currículo");
        }

        final STypeComposite<?> informacoesPessoais = curriculo.addCampoComposto("informacoesPessoais");
        final STypeNomePessoa nome = informacoesPessoais.addCampo("nome", STypeNomePessoa.class, true);
        final STypeCPF cpf = informacoesPessoais.addCampo("cpf", STypeCPF.class, true);
        final STypeData dtNasc = informacoesPessoais.addCampoData("dataNascimento", true);
        final STypeString estadoCivil = informacoesPessoais.addCampoString("estadoCivil", true)
            .withSelectionOf("Solteiro", "Casado", "Separado", "Divorciado", "Viúvo");

        STypeString tipoContato = pb.createTipo("tipoContato", STypeString.class)
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        final STypeLista<STypeString, SIString> infoPub = informacoesPessoais.addCampoListaOf("infoPub",
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
                .withObrigatorio(true)
                .withView(MSelecaoMultiplaPorSelectView::new)
                .as(AtrBasic::new).label("Informação Pública")
                .as(AtrBootstrap::new).colPreference(2);
        }

        final STypeComposite<?> contatos = informacoesPessoais.addCampoComposto("contatos");
        final STypeEMail email = contatos.addCampo("email", STypeEMail.class, true);
        final STypeTelefoneNacional telFixo = contatos.addCampo("telefoneFixo", STypeTelefoneNacional.class);
        final STypeTelefoneNacional telFixo2 = contatos.addCampo("telefoneFixo2", STypeTelefoneNacional.class);
        final STypeTelefoneNacional telCel = contatos.addCampo("telefoneCelular", STypeTelefoneNacional.class);
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

        final STypeComposite<?> referencia = curriculo.addCampoComposto("referencia");
        final STypeBoolean foiIndicado = referencia.addCampoBoolean("foiIndicado");
        final STypeBoolean refTemNaEmpresa = referencia.addCampoBoolean("conheceColaboradorNaEmpresa");
        final STypeString refQuemNaEmpresa = referencia.addCampoString("colaboradorContato");
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

        final STypeLista<STypeComposite<SIComposite>, SIComposite> formacao = curriculo.addCampoListaOfComposto("formacaoAcademica", "cursoAcademico");
        final STypeComposite<?> cursoAcademico = formacao.getTipoElementos();
        final STypeString academicoTipo = cursoAcademico.addCampoString("tipo", true)
            .withSelectionOf("Graduação", "Pós-Graduação", "Mestrado", "Doutorado");
        final STypeString academicoNomeCurso = cursoAcademico.addCampoString("nomeCurso", true);
        final STypeString academicoInstituicao = cursoAcademico.addCampoString("instituicao", true);
        final STypeCNPJ academicoCNPJ = cursoAcademico.addCampo("cnpj", STypeCNPJ.class, false);
        final STypeInteger academicoCargaHoraria = cursoAcademico.addCampo("cargaHoraria", STypeInteger.class, true);
        final STypeAnoMes academicoMesConclusao = cursoAcademico.addCampo("mesConclusao", STypeAnoMes.class, true);
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

        final STypeLista<STypeComposite<SIComposite>, SIComposite> experiencias = curriculo.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        final STypeComposite<?> experiencia = experiencias.getTipoElementos();
        final STypeAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", STypeAnoMes.class, true);
        final STypeAnoMes dtFimExperiencia = experiencia.addCampo("fim", STypeAnoMes.class);
        final STypeString empresa = experiencia.addCampoString("empresa", true);
        final STypeString cargo = experiencia.addCampoString("cargo", true);
        final STypeString atividades = experiencia.addCampoString("atividades");
        {
            experiencias
                .withView(MPanelListaView::new)
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

        final STypeLista<STypeComposite<SIComposite>, SIComposite> certificacoes = curriculo.addCampoListaOfComposto("certificacoes", "certificacao");
        final STypeComposite<?> certificacao = certificacoes.getTipoElementos();
        final STypeAnoMes dataCertificacao = certificacao.addCampo("data", STypeAnoMes.class, true);
        final STypeString entidadeCertificacao = certificacao.addCampoString("entidade", true);
        final STypeData validadeCertificacao = certificacao.addCampoData("validade");
        final STypeString nomeCertificacao = certificacao.addCampoString("nome", true);
        {
            certificacoes
                .withView(MTableListaView::new)
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

        final STypeString informacoesAdicionais = curriculo.addCampoString("informacoesAdicionais");
        {
            informacoesAdicionais
                    .withTextAreaView()
                    .as(AtrBasic::new).label("Informações adicionais");
        }

//        pb.debug();

        // Formatação
        // ---------------------------------------------------------------------------------------------
        MTabView tabbed = curriculo.setView(MTabView::new);
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
