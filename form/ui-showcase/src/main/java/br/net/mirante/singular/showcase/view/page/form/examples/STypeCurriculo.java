package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.type.core.*;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.type.core.SIString;
import org.opensingular.singular.form.type.core.STypeBoolean;
import org.opensingular.singular.form.type.core.STypeDate;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.singular.form.type.country.brazil.STypeCPF;
import org.opensingular.singular.form.type.country.brazil.STypeTelefoneNacional;
import org.opensingular.singular.form.type.util.STypeEMail;
import org.opensingular.singular.form.type.util.STypePersonName;
import org.opensingular.singular.form.type.util.STypeYearMonth;
import org.opensingular.singular.form.view.SMultiSelectionBySelectView;
import org.opensingular.singular.form.view.SViewListByForm;
import org.opensingular.singular.form.view.SViewListByTable;
import org.opensingular.singular.form.view.SViewTab;

@SInfoType(spackage = SPackageCurriculo.class, name = "STypeCurriculo")
public class STypeCurriculo extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        {
            asAtr().label("Currículo");
        }

        STypeComposite<?> informacoesPessoais = addFieldComposite("informacoesPessoais");
        STypePersonName   nome                = informacoesPessoais.addField("nome", STypePersonName.class, true);
        STypeCPF          cpf                 = informacoesPessoais.addField("cpf", STypeCPF.class, true);
        STypeDate   dtNasc              = informacoesPessoais.addFieldDate("dataNascimento", true);
        STypeString estadoCivil         = informacoesPessoais.addFieldString("estadoCivil", true);
        estadoCivil.selectionOf("Solteiro", "Casado", "Separado", "Divorciado", "Viúvo");

        STypeString tipoContato = addField("tipoContato", STypeString.class)
                .selectionOf("Endereço", "Email", "Telefone", "Celular", "Fax").cast();
        final STypeList<STypeString, SIString> infoPub = informacoesPessoais.addFieldListOf("infoPub",
                tipoContato);
        {
            informacoesPessoais
                    .asAtr().label("Informações Pessoais");
            nome
                    .asAtr().label("Nome").subtitle("nome completo").maxLength(50)
                    .asAtrBootstrap().colPreference(7);
            cpf
                    .asAtr().subtitle("cadastro de pessoa física")
                    .asAtrBootstrap().colPreference(3);
            dtNasc
                    .asAtr().label("Dt.Nasc.").subtitle("dd/mm/aaaa")
                    .asAtrBootstrap().colPreference(2);
            estadoCivil
                    .withRadioView()
                    .asAtr().label("Estado Civil")
                    .asAtrBootstrap().colPreference(2);
            infoPub
                    .withRequired(true)
                    .withView(SMultiSelectionBySelectView::new)
                    .asAtr().label("Informação Pública")
                    .asAtrBootstrap().colPreference(2);
        }

        final STypeComposite<?>     contatos = informacoesPessoais.addFieldComposite("contatos");
        final STypeEMail            email    = contatos.addField("email", STypeEMail.class, true);
        final STypeTelefoneNacional telFixo  = contatos.addField("telefoneFixo", STypeTelefoneNacional.class);
        final STypeTelefoneNacional telFixo2 = contatos.addField("telefoneFixo2", STypeTelefoneNacional.class);
        final STypeTelefoneNacional telCel   = contatos.addField("telefoneCelular", STypeTelefoneNacional.class);
        {
            contatos
                    .asAtr().label("Contatos");
            email
                    .asAtr().label("e-Mail")
                    .asAtrBootstrap().colPreference(6);
            telFixo
                    .asAtr().label("Tel.Fixo")
                    .asAtrBootstrap().colPreference(2);
            telFixo2
                    .asAtr().label("Tel.Fixo")
                    .asAtrBootstrap().colPreference(2);
            telCel
                    .asAtr().label("Tel.Celular")
                    .asAtrBootstrap().colPreference(2);
        }

        final STypeComposite<?> referencia       = addFieldComposite("referencia");
        final STypeBoolean foiIndicado      = referencia.addFieldBoolean("foiIndicado");
        final STypeBoolean      refTemNaEmpresa  = referencia.addFieldBoolean("conheceColaboradorNaEmpresa");
        final STypeString       refQuemNaEmpresa = referencia.addFieldString("colaboradorContato");
        {
            referencia
                    .asAtr().label("Referência");
            refTemNaEmpresa
                    .asAtr().label("Conhece colaborador na empresa")
                    .asAtrBootstrap().colPreference(4);
            foiIndicado.withRadioView();
            foiIndicado
                    .asAtr().label("Foi indicado")
                    .asAtrBootstrap().colPreference(3);
            refQuemNaEmpresa
                    .asAtr().label("Colaborador")
                    .asAtrBootstrap().colPreference(5);
        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> formacao       = addFieldListOfComposite("formacaoAcademica", "cursoAcademico");
        final STypeComposite<?>                                   cursoAcademico = formacao.getElementsType();
        final STypeString academicoTipo = cursoAcademico.addFieldString("tipo", true)
                .selectionOf("Graduação", "Pós-Graduação", "Mestrado", "Doutorado").cast();
        final STypeString    academicoNomeCurso    = cursoAcademico.addFieldString("nomeCurso", true);
        final STypeString    academicoInstituicao  = cursoAcademico.addFieldString("instituicao", true);
        final STypeCNPJ      academicoCNPJ         = cursoAcademico.addField("cnpj", STypeCNPJ.class, false);
        final STypeInteger academicoCargaHoraria = cursoAcademico.addField("cargaHoraria", STypeInteger.class, true);
        final STypeYearMonth academicoMesConclusao = cursoAcademico.addField("mesConclusao", STypeYearMonth.class, true);
        {
            formacao
                    .asAtr().label("Formação Acadêmica");
            academicoTipo
                    .withRadioView()
                    .asAtr().label("Tipo");
            academicoNomeCurso
                    .asAtr().label("Nome");
            academicoInstituicao
                    .asAtr().label("Instituição")
                    .asAtrBootstrap().colPreference(3);
            academicoCNPJ
                    .asAtrBootstrap().colPreference(3);
            academicoCargaHoraria
                    .asAtr().label("Carga Horária (h)")
                    .asAtr().maxLength(5)
                    .asAtrBootstrap().colPreference(2);
            academicoMesConclusao
                    .asAtr().label("Mês de Conclusão")
                    .asAtrBootstrap().colPreference(2);
        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> experiencias = addFieldListOfComposite("experienciasProfissionais", "experiencia");
        final STypeComposite<?> experiencia = experiencias.getElementsType();
        final STypeYearMonth dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        final STypeYearMonth dtFimExperiencia = experiencia.addField("fim", STypeYearMonth.class);
        final STypeString empresa = experiencia.addFieldString("empresa", true);
        final STypeString cargo = experiencia.addFieldString("cargo", true);
        final STypeString atividades = experiencia.addFieldString("atividades");
        {
            experiencias
                    .withView(SViewListByForm::new)
                    .asAtr().label("Experiências profissionais");
            dtInicioExperiencia
                    .asAtr().label("Data inicial")
                    .asAtrBootstrap().colPreference(2);
            dtFimExperiencia
                    .asAtr().label("Data final")
                    .asAtrBootstrap().colPreference(2);
            empresa
                    .asAtr().label("Empresa")
                    .asAtrBootstrap().colPreference(8);
            cargo
                    .asAtr().label("Cargo");
            atividades
                    .withTextAreaView()
                    .asAtr().label("Atividades Desenvolvidas");
        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> certificacoes = addFieldListOfComposite("certificacoes", "certificacao");
        final STypeComposite<?> certificacao = certificacoes.getElementsType();
        final STypeYearMonth dataCertificacao = certificacao.addField("data", STypeYearMonth.class, true);
        final STypeString entidadeCertificacao = certificacao.addFieldString("entidade", true);
        final STypeDate validadeCertificacao = certificacao.addFieldDate("validade");
        final STypeString nomeCertificacao = certificacao.addFieldString("nome", true);
        {
            certificacoes
                    .withView(SViewListByTable::new)
                    .asAtr().label("Certificações");
            certificacao
                    .asAtr().label("Certificação");
            dataCertificacao
                    .asAtr().label("Data")
                    .asAtrBootstrap().colPreference(2);
            entidadeCertificacao
                    .asAtr().label("Entidade")
                    .asAtrBootstrap().colPreference(10);
            validadeCertificacao
                    .asAtr().label("Validade")
                    .asAtrBootstrap().colPreference(2);
            nomeCertificacao
                    .asAtr().label("Nome")
                    .asAtrBootstrap().colPreference(10);
        }

        final STypeString informacoesAdicionais = addFieldString("informacoesAdicionais");
        {
            informacoesAdicionais
                    .withTextAreaView()
                    .asAtr().label("Informações adicionais");
        }

//        pb.debug();

        // Formatação
        // ---------------------------------------------------------------------------------------------
        SViewTab tabbed = setView(SViewTab::new);
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
        //            temRefNaMepresa.getIrmao(refQuemNaEmpresa).asAtrBasic().visible(isTrue(temRefNaMepresa));
        //        });

        // Ou

        //        refTemNaEmpresa.withOnChange((temRefNaMepresa) -> {
        //            temRefNaMepresa.getIrmao(refQuemNaEmpresa).asAtrBasic().visible(isTrue(temRefNaMepresa));
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
