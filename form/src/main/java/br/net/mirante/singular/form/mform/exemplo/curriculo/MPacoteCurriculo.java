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

public class MPacoteCurriculo extends MPacote {

    public MPacoteCurriculo() {
        super("mform.exemplo.curriculo");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        final MTipoComposto<?> curriculo = pb.createTipoComposto("Curriculo");
        {
            curriculo.as(AtrBasic.class).label("Currículo");
        }

        final MTipoComposto<?> informacoesPessoais = curriculo.addCampoComposto("informacoesPessoais");
        final MTipoNomePessoa nome = informacoesPessoais.addCampo("nome", MTipoNomePessoa.class, true);
        final MTipoCPF cpf = informacoesPessoais.addCampo(MTipoCPF.class);
        final MTipoData dtNasc = informacoesPessoais.addCampoData("dataNascimento", true);
        {
            informacoesPessoais.as(AtrBasic.class).label("Informações Pessoais");
            nome.as(AtrBasic.class).label("Nome").tamanhoMaximo(50);
            cpf.as(AtrBasic.class).label("CPF");
            dtNasc.as(AtrBasic.class).label("Dt. Nasc.");
        }

        final MTipoComposto<?> contatos = informacoesPessoais.addCampoComposto("contatos");
        final MTipoEMail email = contatos.addCampo("email", MTipoEMail.class, true);
        final MTipoTelefoneNacional telFixo = contatos.addCampo("telefoneFixo", MTipoTelefoneNacional.class);
        final MTipoTelefoneNacional telFixo2 = contatos.addCampo("telefoneFixo2", MTipoTelefoneNacional.class);
        final MTipoTelefoneNacional telCel = contatos.addCampo("telefoneCelular", MTipoTelefoneNacional.class);
        {
            contatos.as(AtrBasic.class).label("Contatos");
            email.as(AtrBasic.class).label("e-Mail");
            telFixo.as(AtrBasic.class).label("Tel.Fixo");
            telFixo2.as(AtrBasic.class).label("Tel.Fixo");
            telCel.as(AtrBasic.class).label("Tel.Celular");
        }

        final MTipoComposto<?> referencia = curriculo.addCampoComposto("referencia");
        final MTipoBoolean refTemNaEmpresa = referencia.addCampoBoolean("conheceColaboradorNaEmpresa");
        final MTipoString refQuemNaEmpresa = referencia.addCampoString("colaboradorContato");
        {
            referencia.as(AtrBasic.class).label("Referência");
            refTemNaEmpresa.as(AtrBasic.class).label("Conhece colaborador na empresa");
            refQuemNaEmpresa.as(AtrBasic.class).label("Colaborador");
        }

        final MTipoLista<MTipoComposto<?>> formacao = curriculo.addCampoListaOfComposto("formacaoAcademica", "cursoAcademico");
        final MTipoComposto<?> cursoAcademico = formacao.getTipoElementos();
        final MTipoString academicoTipo = cursoAcademico.addCampoString("tipo", true)
            .withSelectionOf("Graduação", "Pós-Graduação", "Mestrado", "Doutorado");
        final MTipoString academicoNome = cursoAcademico.addCampoString("nomeCurso", true);
        final MTipoString academicoInstituicao = cursoAcademico.addCampoString("instituicao", true);
        final MTipoAnoMes academicoMesConclusao = cursoAcademico.addCampo("mesConclusao", MTipoAnoMes.class, true);
        {
            formacao.withView(MListaSimpleTableView::new)
                .as(AtrBasic.class).label("Formação Acadêmica");
            academicoTipo.withView(MSelecaoPorRadioView::new)
                .as(AtrBasic.class).label("Tipo");
            academicoNome.as(AtrBasic.class).label("Nome");
            academicoInstituicao.as(AtrBasic.class).label("Instituição");
            academicoMesConclusao.as(AtrBasic.class).label("Mês conclusão");

        }

        final MTipoLista<MTipoComposto<?>> experiencias = curriculo.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        final MTipoComposto<?> experiencia = experiencias.getTipoElementos();
        final MTipoAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", MTipoAnoMes.class, true);
        final MTipoAnoMes dtFimExperiencia = experiencia.addCampo("fim", MTipoAnoMes.class);
        final MTipoString empresa = experiencia.addCampoString("empresa", true);
        final MTipoString cargo = experiencia.addCampoString("cargo", true);
        final MTipoString atividades = experiencia.addCampoString("atividades");
        {
            experiencias.withView(MListaMultiPanelView::new).as(AtrBasic.class).label("Experiências profissionais");
            dtInicioExperiencia.as(AtrBasic.class).label("Data inicial");
            dtFimExperiencia.as(AtrBasic.class).label("Data final");
            empresa.as(AtrBasic.class).label("Empresa");
            cargo.as(AtrBasic.class).label("Cargo");
            atividades.as(AtrBasic.class).label("Atividades");
        }

        final MTipoLista<MTipoComposto<?>> certificacoes = curriculo.addCampoListaOfComposto("certificacoes", "certificacao");
        final MTipoComposto<?> certificacao = certificacoes.getTipoElementos();
        final MTipoAnoMes dataCertificacao = certificacao.addCampo("data", MTipoAnoMes.class, true);
        final MTipoString entidadeCertificacao = certificacao.addCampoString("entidade", true);
        final MTipoString nomeCertificacao = certificacao.addCampoString("nome", true);
        final MTipoData validadeCertificacao = certificacao.addCampoData("validade");
        {
            certificacoes.as(AtrBasic.class).label("Certificações");
            certificacao.as(AtrBasic.class);
            dataCertificacao.as(AtrBasic.class).label("Data");
            entidadeCertificacao.as(AtrBasic.class).label("Entidade");
            nomeCertificacao.as(AtrBasic.class).label("Nome");
            validadeCertificacao.as(AtrBasic.class).label("Validade");
        }

        final MTipoString informacoesAdicionais = curriculo.addCampoString("informacoesAdicionais");

        pb.debug();

        // Formatação
        // ---------------------------------------------------------------------------------------------
        MTabView tabbed = curriculo.setView(MTabView::new);
        tabbed.addTab("Dados");
        tabbed.addTab("Formacção e Curso")
            .add(formacao)
            .add(certificacoes);
        tabbed.addTab(experiencias);

        atividades.as(AtrBasic.class).label("Atividades Desenvolvidas");

        informacoesAdicionais.as(AtrBasic.class).label("Informações adicionais");

        // Comportamentos
        // ---------------------------------------------------------------------------------------------
        //        refTemNaEmpresa.withCode("onChange", (temRefNaMepresa) -> {
        //            temRefNaMepresa.getIrmao(refQuemNaEmpresa).as(AtrBasic.class).visivel(isTrue(temRefNaMepresa));
        //        });

        // Ou

        //        refTemNaEmpresa.withOnChange((temRefNaMepresa) -> {
        //            temRefNaMepresa.getIrmao(refQuemNaEmpresa).as(AtrBasic.class).visivel(isTrue(temRefNaMepresa));
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
