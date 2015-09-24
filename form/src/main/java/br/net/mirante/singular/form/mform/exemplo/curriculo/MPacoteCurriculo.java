package br.net.mirante.singular.form.mform.exemplo.curriculo;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MListaMultiPanelView;
import br.net.mirante.singular.form.mform.basic.view.MListaSimpleTableView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;
import br.net.mirante.singular.form.mform.util.comuns.MTipoEMail;
import br.net.mirante.singular.form.mform.util.comuns.MTipoNomePessoa;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;
import br.net.mirante.singular.form.wicket.AtrWicket;

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
        final MTipoCPF cpf = informacoesPessoais.addCampo("cpf", MTipoCPF.class);
        final MTipoData dtNasc = informacoesPessoais.addCampoData("dataNascimento", true);
        {
            informacoesPessoais.as(AtrBasic::new).label("Informações Pessoais");
            nome
                .as(AtrBasic::new).label("Nome").tamanhoMaximo(50)
                .as(AtrWicket::new).larguraPref(7);
            cpf
                .as(AtrBasic::new).label("CPF")
                .as(AtrWicket::new).larguraPref(3);
            dtNasc
                .as(AtrBasic::new).label("Dt.Nasc.")
                .as(AtrWicket::new).larguraPref(2);
        }

        final MTipoComposto<?> contatos = informacoesPessoais.addCampoComposto("contatos");
        final MTipoEMail email = contatos.addCampo("email", MTipoEMail.class, true);
        final MTipoTelefoneNacional telFixo = contatos.addCampo("telefoneFixo", MTipoTelefoneNacional.class);
        final MTipoTelefoneNacional telFixo2 = contatos.addCampo("telefoneFixo2", MTipoTelefoneNacional.class);
        final MTipoTelefoneNacional telCel = contatos.addCampo("telefoneCelular", MTipoTelefoneNacional.class);
        {
            contatos.as(AtrBasic::new).label("Contatos");
            email.as(AtrBasic::new).label("e-Mail")
                .as(AtrWicket::new).larguraPref(6);
            telFixo.as(AtrBasic::new).label("Tel.Fixo")
                .as(AtrWicket::new).larguraPref(2);
            telFixo2.as(AtrBasic::new).label("Tel.Fixo")
                .as(AtrWicket::new).larguraPref(2);
            telCel.as(AtrBasic::new).label("Tel.Celular")
                .as(AtrWicket::new).larguraPref(2);
        }

        final MTipoComposto<?> referencia = curriculo.addCampoComposto("referencia");
        final MTipoBoolean refTemNaEmpresa = referencia.addCampoBoolean("conheceColaboradorNaEmpresa");
        final MTipoString refQuemNaEmpresa = referencia.addCampoString("colaboradorContato");
        {
            referencia
                .as(AtrBasic::new).label("Referência");
            refTemNaEmpresa
                .as(AtrBasic::new).label("Conhece colaborador na empresa")
                .as(AtrWicket::new).larguraPref(4);
            refQuemNaEmpresa
                .as(AtrBasic::new).label("Colaborador")
                .as(AtrWicket::new).larguraPref(8);
        }

        final MTipoLista<MTipoComposto<?>> formacao = curriculo.addCampoListaOfComposto("formacaoAcademica", "cursoAcademico");
        final MTipoComposto<?> cursoAcademico = formacao.getTipoElementos();
        final MTipoString academicoTipo = cursoAcademico.addCampoString("tipo", true)
            .withSelectionOf("Graduação", "Pós-Graduação", "Mestrado", "Doutorado");
        final MTipoString academicoNomeCurso = cursoAcademico.addCampoString("nomeCurso", true);
        final MTipoString academicoInstituicao = cursoAcademico.addCampoString("instituicao", true);
        final MTipoAnoMes academicoMesConclusao = cursoAcademico.addCampo("mesConclusao", MTipoAnoMes.class, true);
        {
            formacao.withView(MListaSimpleTableView::new)
                .as(AtrBasic::new).label("Formação Acadêmica").tamanhoInicial(1);
            academicoTipo.withView(MSelecaoPorRadioView::new)
                .as(AtrBasic::new).label("Tipo")
                .as(AtrWicket::new).larguraPref(2);
            academicoNomeCurso
                .as(AtrBasic::new).label("Nome");
            academicoInstituicao
                .as(AtrBasic::new).label("Instituição")
                .as(AtrWicket::new).larguraPref(3);
            academicoMesConclusao
                .as(AtrBasic::new).label("Mês conclusão")
                .as(AtrWicket::new).larguraPref(2);
        }

        final MTipoLista<MTipoComposto<?>> experiencias = curriculo.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        final MTipoComposto<?> experiencia = experiencias.getTipoElementos();
        final MTipoAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", MTipoAnoMes.class, true);
        final MTipoAnoMes dtFimExperiencia = experiencia.addCampo("fim", MTipoAnoMes.class);
        final MTipoString empresa = experiencia.addCampoString("empresa", true);
        final MTipoString cargo = experiencia.addCampoString("cargo", true);
        final MTipoString atividades = experiencia.addCampoString("atividades");
        {
            experiencias.withView(MListaMultiPanelView::new)
                .as(AtrBasic::new).label("Experiências profissionais").tamanhoInicial(2);
            dtInicioExperiencia
                .as(AtrBasic::new).label("Data inicial")
                .as(AtrWicket::new).larguraPref(2);
            dtFimExperiencia
                .as(AtrBasic::new).label("Data final")
                .as(AtrWicket::new).larguraPref(2);
            empresa
                .as(AtrBasic::new).label("Empresa")
                .as(AtrWicket::new).larguraPref(8);
            cargo
                .as(AtrBasic::new).label("Cargo");
            atividades
                .as(AtrBasic::new).label("Atividades Desenvolvidas").multiLinha(true);
        }

        final MTipoLista<MTipoComposto<?>> certificacoes = curriculo.addCampoListaOfComposto("certificacoes", "certificacao");
        final MTipoComposto<?> certificacao = certificacoes.getTipoElementos();
        final MTipoAnoMes dataCertificacao = certificacao.addCampo("data", MTipoAnoMes.class, true);
        final MTipoString entidadeCertificacao = certificacao.addCampoString("entidade", true);
        final MTipoData validadeCertificacao = certificacao.addCampoData("validade");
        final MTipoString nomeCertificacao = certificacao.addCampoString("nome", true);
        {
            certificacoes
                .as(AtrBasic::new).label("Certificações").tamanhoInicial(3);
            certificacao
                .as(AtrBasic::new).label("Certificação");
            dataCertificacao
                .as(AtrBasic::new).label("Data")
                .as(AtrWicket::new).larguraPref(2);
            entidadeCertificacao
                .as(AtrBasic::new).label("Entidade")
                .as(AtrWicket::new).larguraPref(10);
            validadeCertificacao
                .as(AtrBasic::new).label("Validade")
                .as(AtrWicket::new).larguraPref(2);
            nomeCertificacao
                .as(AtrBasic::new).label("Nome")
                .as(AtrWicket::new).larguraPref(10);
        }

        final MTipoString informacoesAdicionais = curriculo.addCampoString("informacoesAdicionais");
        {
            informacoesAdicionais
                .as(AtrBasic::new).label("Informações adicionais").multiLinha(true);
        }

        pb.debug();

        // Formatação
        // ---------------------------------------------------------------------------------------------
        MTabView tabbed = curriculo.setView(MTabView::new);
        tabbed.addTab("Dados");
        tabbed.addTab("Formacção e Curso")
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
        //        dtNasc.addValidacao(MTipoData.validadorBuilder().entre(new Period().withYears(-100), new Period().withYears(-14)).build());

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
