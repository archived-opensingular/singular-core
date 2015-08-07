package br.net.mirante.singular.ui.mform.exemplo.curriculo;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.joda.time.Period;

import br.net.mirante.singular.ui.mform.MIComposto;
import br.net.mirante.singular.ui.mform.MInstancia;
import br.net.mirante.singular.ui.mform.MPacote;
import br.net.mirante.singular.ui.mform.MTipoComposto;
import br.net.mirante.singular.ui.mform.MTipoLista;
import br.net.mirante.singular.ui.mform.PacoteBuilder;
import br.net.mirante.singular.ui.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.ui.mform.basic.view.MListaMultiPanelView;
import br.net.mirante.singular.ui.mform.basic.view.MListaSimpleTableView;
import br.net.mirante.singular.ui.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.ui.mform.basic.view.MTabView;
import br.net.mirante.singular.ui.mform.core.MIData;
import br.net.mirante.singular.ui.mform.core.MIString;
import br.net.mirante.singular.ui.mform.core.MTipoBoolean;
import br.net.mirante.singular.ui.mform.core.MTipoData;
import br.net.mirante.singular.ui.mform.core.MTipoString;
import br.net.mirante.singular.ui.mform.util.comuns.MIAnoMes;
import br.net.mirante.singular.ui.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.ui.mform.util.comuns.MTipoCPF;
import br.net.mirante.singular.ui.mform.util.comuns.MTipoEMail;
import br.net.mirante.singular.ui.mform.util.comuns.MTipoNomePessoa;
import br.net.mirante.singular.ui.mform.util.comuns.MTipoTelefoneNacional;

public class MPacoteCurriculo extends MPacote {

    public MPacoteCurriculo() {
        super("mform.exemplo.curriculo");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> curriculo = pb.createTipoComposto("Curriculo");

        MTipoComposto<?> informacoesPessoais = curriculo.addCampoComposto("informacoesPessoais");
        MTipoNomePessoa nome = informacoesPessoais.addCampo("nome", MTipoNomePessoa.class, true);
        informacoesPessoais.addCampo(MTipoCPF.class);
        MTipoData dtNasc = informacoesPessoais.addCampoData("dataNascimento", true);

        MTipoComposto<?> contatos = informacoesPessoais.addCampoComposto("contatos");
        contatos.addCampo("email", MTipoEMail.class, true);
        MTipoTelefoneNacional telFixo = contatos.addCampo("telefoneFixo", MTipoTelefoneNacional.class);
        MTipoTelefoneNacional telFixo2 = contatos.addCampo("telefoneFixo2", MTipoTelefoneNacional.class);
        MTipoTelefoneNacional telCel = contatos.addCampo("telefoneCelular", MTipoTelefoneNacional.class);

        MTipoComposto<?> referencia = curriculo.addCampoComposto("referencia");
        MTipoBoolean refTemNaEmpresa = referencia.addCampoBoolean("conheceColaboradorNaEmpresa");
        MTipoString refQuemNaEmpresa = referencia.addCampoString("colaboradorContato");

        MTipoLista<MTipoComposto<?>> formacao = curriculo.addCampoListaOfComposto("formacaoAcademica", "cursoAcademico");
        MTipoComposto<?> cursoAcademico = formacao.getTipoElementos();
        MTipoString academicoTipo = cursoAcademico.addCampoString("tipo", true);
        academicoTipo.selectionOf("Graduação", "Pós-Graduação", "Mestrado", "Doutorado");
        MTipoString academicoNome = cursoAcademico.addCampoString("nomeCurso", true);
        MTipoString academicoInstituicao = cursoAcademico.addCampoString("instituicao", true);
        MTipoAnoMes academicoMesConclusao = cursoAcademico.addCampo("mesConclusao", MTipoAnoMes.class, true);

        MTipoLista<MTipoComposto<?>> experiencias = curriculo.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        MTipoComposto<?> experiencia = experiencias.getTipoElementos();
        MTipoAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", MTipoAnoMes.class, true);
        MTipoAnoMes dtFimExperiencia = experiencia.addCampo("fim", MTipoAnoMes.class);
        experiencia.addCampoString("empresa", true);
        experiencia.addCampoString("cargo", true);
        MTipoString atividades = experiencia.addCampoString("atividades");

        MTipoLista<MTipoComposto<?>> certificacoes = curriculo.addCampoListaOfComposto("certificacoes", "certificacao");
        MTipoComposto<?> certificacao = certificacoes.getTipoElementos();
        certificacao.addCampo("data", MTipoAnoMes.class, true);
        certificacao.addCampoString("entidade", true);
        certificacao.addCampoString("nome", true);
        certificacao.addCampoData("validade");

        curriculo.addCampoString("informacoesAdicionais");

        pb.debug();

        // Formatação
        // ---------------------------------------------------------------------------------------------
        MTabView tabbed = curriculo.setView(MTabView.class).withNaoDefinidosVaoParaTabDoIrmaoAnterior();
        tabbed.withNaoDefinidosVaoParaTabDefault();
        tabbed.addTab("Dados", true);
        tabbed.addTab("Formacção e Curso", true).add(formacao).add(certificacoes);
        tabbed.addTab(experiencias);

        informacoesPessoais.as(AtrBasic.class).label("Informações Pessoais");
        nome.as(AtrBasic.class).tamanhoMaximo(50);
        dtNasc.as(AtrBasic.class).label("Dt. Nasc.");

        telFixo.as(AtrBasic.class).label("Tel.Fixo");
        telCel.as(AtrBasic.class).label("Tel.Celular");

        formacao.as(AtrBasic.class).label("Formação Acadêmica");
        academicoTipo.withView(MSelecaoPorRadioView.class).as(AtrBasic.class).label("Tipo");
        academicoNome.as(AtrBasic.class).label("Nome");
        academicoInstituicao.as(AtrBasic.class).label("Instituição");
        academicoMesConclusao.as(AtrBasic.class).label("Mês conclusão");

        formacao.setView(MListaSimpleTableView.class);

        experiencias.setView(MListaMultiPanelView.class);

        atividades.as(AtrBasic.class).label("Atividades Desenvolvidas");

        // Comportamentos
        // ---------------------------------------------------------------------------------------------
        refTemNaEmpresa.withCode("onChange", (temRefNaMepresa) -> {
            temRefNaMepresa.getIrmao(refQuemNaEmpresa).as(AtrBasic.class).visivel(isTrue(temRefNaMepresa));
        });

        // Ou

        refTemNaEmpresa.withOnChange((temRefNaMepresa) -> {
            temRefNaMepresa.getIrmao(refQuemNaEmpresa).as(AtrBasic.class).visivel(isTrue(temRefNaMepresa));
        });

        // Ou

        refQuemNaEmpresa.withFunction("visivelControle", (MIString refQuem) -> isTrue(refQuem.getIrmao(refTemNaEmpresa)));

        // Validacoes
        // ---------------------------------------------------------------------------------------------
        dtNasc.addValidacao(MTipoData.validadorBuilder().entre(new Period().withYears(-100), new Period().withYears(-14)).build());

        informacoesPessoais.addValidacao((IValidatable<MIComposto> validatable) -> {
            if (validatable.getValue().isCampoNull("telefoneFixo") && validatable.getValue().isCampoNull("telefoneCelular")) {
                validatable.error(new ValidationError("Ao menos um telefone deve ser preenchido."));
            }
        });

        academicoMesConclusao.addValidacao((IValidatable<MInstancia> validatable) -> {
            MIAnoMes mesConclusao = academicoMesConclusao.castInstancia(validatable.getValue());
            YearMonth conclusao = mesConclusao.getJavaYearMonth();
            YearMonth maximo = YearMonth.now().plus(4, ChronoUnit.YEARS);
            if (conclusao.isAfter(maximo)) {
                validatable.error(new ValidationError("O mês de conclusão pode ser no máximpo até " + maximo));
            } else {
                MIData dt = mesConclusao.getPai(curriculo).getFilho(dtNasc);
                if (isNotNull(dt)) {
                    YearMonth minimo = dt.getJavaYearMonth().plus(14, ChronoUnit.YEARS);
                    if (conclusao.isBefore(minimo)) {
                        validatable.error(new ValidationError("O mês de conclusão deve ser posterior a " + minimo));
                    }
                }
            }
        });

        dtFimExperiencia.addValidacao((IValidatable<MIAnoMes> validatable) -> {
            MIAnoMes fim = validatable.getValue();
            MIAnoMes inicio = fim.getIrmao(dtInicioExperiencia);
            if (isNotNull(inicio) && isNotNull(fim) && inicio.isAfter(fim)) {
                validatable.error(new ValidationError("O mês de conclusão não pode ser anterior ao início"));
            }
        });

    }

}
