package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MBooleanRadioView;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;
import br.net.mirante.singular.form.mform.util.comuns.MTipoEMail;
import br.net.mirante.singular.form.mform.util.comuns.MTipoNomePessoa;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;
import br.net.mirante.singular.form.wicket.AtrBootstrap;

public class MPacoteCurriculo extends MPacote {

    public static final String PACOTE         = "mform.exemplo.curriculo";
    public static final String TIPO_CURRICULO = PACOTE + ".Curriculo";

    public MPacoteCurriculo() {
        super("mform.exemplo.curriculo");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        final MTipoComposto<?> curriculo = pb.createTipoComposto("Curriculo");
        {
            curriculo
                .as(AtrBasic::new).label("Currículo");
        }

        final MTipoComposto<?> informacoesPessoais = curriculo.addCampoComposto("informacoesPessoais");
        final MTipoNomePessoa nome = informacoesPessoais.addCampo("nome", MTipoNomePessoa.class, true);
        final MTipoCPF cpf = informacoesPessoais.addCampo("cpf", MTipoCPF.class, true);
        final MTipoData dtNasc = informacoesPessoais.addCampoData("dataNascimento", true);
        final MTipoString estadoCivil = informacoesPessoais.addCampoString("estadoCivil", true)
            .withSelectionOf("Solteiro", "Casado", "Separado", "Divorciado", "Viúvo");
        
        final MTipoLista<MTipoString, MIString> infoPub = informacoesPessoais.addCampoListaOf("infoPub",
                pb.createTipo("tipoContato", MTipoString.class)
                        .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax"));
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
                .withSelectView()
                .as(AtrBasic::new).label("Estado Civil")
                .as(AtrBootstrap::new).colPreference(2);
            infoPub
                .withObrigatorio(true)
                .withView(MSelecaoMultiplaPorSelectView::new)
                .as(AtrBasic::new).label("Informação Pública")
                .as(AtrBootstrap::new).colPreference(2);
        }

        final MTipoComposto<?> contatos = informacoesPessoais.addCampoComposto("contatos");
        final MTipoEMail email = contatos.addCampo("email", MTipoEMail.class, true);
        final MTipoTelefoneNacional telFixo = contatos.addCampo("telefoneFixo", MTipoTelefoneNacional.class);
        final MTipoTelefoneNacional telFixo2 = contatos.addCampo("telefoneFixo2", MTipoTelefoneNacional.class);
        final MTipoTelefoneNacional telCel = contatos.addCampo("telefoneCelular", MTipoTelefoneNacional.class);
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

        final MTipoComposto<?> referencia = curriculo.addCampoComposto("referencia");
        final MTipoBoolean foiIndicado = referencia.addCampoBoolean("foiIndicado");
        final MTipoBoolean refTemNaEmpresa = referencia.addCampoBoolean("conheceColaboradorNaEmpresa");
        final MTipoString refQuemNaEmpresa = referencia.addCampoString("colaboradorContato");
        {
            referencia
                .as(AtrBasic::new).label("Referência");
            refTemNaEmpresa
                .as(AtrBasic::new).label("Conhece colaborador na empresa")
                .as(AtrBootstrap::new).colPreference(4);
            foiIndicado.setView(MBooleanRadioView::new);
            foiIndicado
                .as(AtrBasic::new).label("Foi indicado")
                .as(AtrBootstrap::new).colPreference(3);
            refQuemNaEmpresa
                .as(AtrBasic::new).label("Colaborador")
                .as(AtrBootstrap::new).colPreference(5);
        }

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> formacao = curriculo.addCampoListaOfComposto("formacaoAcademica", "cursoAcademico");
        final MTipoComposto<?> cursoAcademico = formacao.getTipoElementos();
        final MTipoString academicoTipo = cursoAcademico.addCampoString("tipo", true)
            .withSelectionOf("Graduação", "Pós-Graduação", "Mestrado", "Doutorado");
        final MTipoString academicoNomeCurso = cursoAcademico.addCampoString("nomeCurso", true);
        final MTipoString academicoInstituicao = cursoAcademico.addCampoString("instituicao", true);
        final MTipoCNPJ academicoCNPJ = cursoAcademico.addCampo("cnpj", MTipoCNPJ.class, false);
        final MTipoInteger academicoCargaHoraria = cursoAcademico.addCampo("cargaHoraria", MTipoInteger.class, true);
        final MTipoAnoMes academicoMesConclusao = cursoAcademico.addCampo("mesConclusao", MTipoAnoMes.class, true);
        {
            formacao
                .as(AtrBasic::new).label("Formação Acadêmica");
            academicoTipo
                .withView(MSelecaoPorRadioView::new)
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

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> experiencias = curriculo.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        final MTipoComposto<?> experiencia = experiencias.getTipoElementos();
        final MTipoAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", MTipoAnoMes.class, true);
        final MTipoAnoMes dtFimExperiencia = experiencia.addCampo("fim", MTipoAnoMes.class);
        final MTipoString empresa = experiencia.addCampoString("empresa", true);
        final MTipoString cargo = experiencia.addCampoString("cargo", true);
        final MTipoString atividades = experiencia.addCampoString("atividades");
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

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> certificacoes = curriculo.addCampoListaOfComposto("certificacoes", "certificacao");
        final MTipoComposto<?> certificacao = certificacoes.getTipoElementos();
        final MTipoAnoMes dataCertificacao = certificacao.addCampo("data", MTipoAnoMes.class, true);
        final MTipoString entidadeCertificacao = certificacao.addCampoString("entidade", true);
        final MTipoData validadeCertificacao = certificacao.addCampoData("validade");
        final MTipoString nomeCertificacao = certificacao.addCampoString("nome", true);
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

        final MTipoString informacoesAdicionais = curriculo.addCampoString("informacoesAdicionais");
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
        //                MIData dt = mesConclusao.getPai(curriculo).getFilho(dtNasc);
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
