package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.SelectBuilder;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.provider.SSimpleProvider;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.country.brazil.STypeCNPJ;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.util.transformer.Value;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeEntidade extends STypeComposite<SIComposite> {

    public static final String TIPO_PESSOA         = "tipoPessoa";
    public static final String CNPJ                = "cnpj";
    public static final String CPF                 = "cpf";
    public static final String NOME                = "nome";
    public static final String ENDERECO_ELETRONICO = "enderecoEletronico";
    public static final String CEP                 = "cep";
    public static final String ENDERECO            = "endereco";
    public static final String ESTADO              = "estado";
    public static final String SIGLA_UF            = "sigla";
    public static final String NOME_UF             = "nome";
    public static final String CIDADE              = "cidade";
    public static final String ID_CIDADE           = "id";
    public static final String NOME_CIDADE         = "nome";
    public static final String UF_CIDADE           = "UF";
    public static final String BAIRRO              = "bairro";
    public static final String TELEFONE            = "telefone";
    public static final String FAX                 = "fax";
    public static final String CELULAR             = "celular";

    public STypeString           tipoPessoa;
    public STypeCNPJ             cnpj;
    public STypeCPF              cpf;
    public STypeString           nome;
    public STypeEMail            enderecoEletronico;
    public STypeCEP              cep;
    public STypeComposite<?>     estado;
    public STypeString           sigla;
    public STypeString           nomeUF;
    public STypeComposite<?>     cidade;
    public STypeString           idCidade;
    public STypeString           nomeCidade;
    public STypeString           ufCidade;
    public STypeString           bairro;
    public STypeString           endereco;
    public STypeTelefoneNacional telefone;
    public STypeTelefoneNacional fax;
    public STypeTelefoneNacional celular;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        super.onLoadType(tb);

        tipoPessoa = addField(TIPO_PESSOA, STypeString.class);
        cnpj = addField(CNPJ, STypeCNPJ.class);
        cpf = addField(CPF, STypeCPF.class);
        nome = addField(NOME, STypeString.class);
        enderecoEletronico = addField(ENDERECO_ELETRONICO, STypeEMail.class);
        cep = addField(CEP, STypeCEP.class);
        estado = addFieldComposite(ESTADO);
        sigla = estado.addFieldString(SIGLA_UF);
        nomeUF = estado.addFieldString(NOME_UF);
        cidade = addFieldComposite(CIDADE);
        idCidade = cidade.addFieldString(ID_CIDADE);
        nomeCidade = cidade.addFieldString(NOME_CIDADE);
        ufCidade = cidade.addFieldString(UF_CIDADE);
        bairro = addField(BAIRRO, STypeString.class);
        endereco = addField(ENDERECO, STypeString.class);
        telefone = addField(TELEFONE, STypeTelefoneNacional.class);
        fax = addField(FAX, STypeTelefoneNacional.class);
        celular = addField(CELULAR, STypeTelefoneNacional.class);

        tipoPessoa
                .selectionOf("Fisica", "Juridica")
                .withRadioView()
                .asAtr()
                .label("Tipo de Pessoa")
                .asAtrBootstrap()
                .colPreference(3);

        cnpj
                .asAtr()
                .label("CNPJ")
                .dependsOn(tipoPessoa)
                .exists(i -> i.findNearestValue(tipoPessoa).orElse("").equals("Juridica"))
                .asAtrBootstrap()
                .colPreference(3);

        cpf
                .asAtr()
                .label("CPF")
                .dependsOn(tipoPessoa)
                .exists(i -> i.findNearestValue(tipoPessoa).orElse("").equals("Fisica"))
                .asAtrBootstrap()
                .colPreference(3);

        nome
                .asAtr()
                .label("Nome")
                .asAtrBootstrap()
                .newRow()
                .colPreference(8);

        enderecoEletronico
                .asAtr()
                .label("Endereço Eletronico")
                .asAtrBootstrap()
                .colPreference(4);

        cep
                .asAtr()
                .label("CEP")
                .asAtrBootstrap()
                .colPreference(3);


        estado
                .asAtr()
                .required()
                .displayString("${nome}")
                .label("Estado")
                .asAtrBootstrap()
                .colPreference(3);

        estado
                .selection()
                .id(sigla)
                .display(nomeUF)
                .simpleProvider((SSimpleProvider) builder -> {
                    SelectBuilder
                            .buildEstados()
                            .forEach(estadoDTO -> {
                                builder.add()
                                        .set(sigla, estadoDTO.getSigla())
                                        .set(nomeUF, estadoDTO.getNome());
                            });
                });


        estado.selectionOf(SelectBuilder.EstadoDTO.class)
                .id(SelectBuilder.EstadoDTO::getSigla)
                .display("${nome} - ${sigla}")
                .autoConverterOf(SelectBuilder.EstadoDTO.class)
                .simpleProvider(ins -> SelectBuilder.buildEstados());


        cidade
                .asAtr()
                .label("Cidade")
                .asAtrBootstrap()
                .colPreference(3);


        cidade
                .asAtr()
                .required(inst -> Value.notNull(inst, (STypeSimple) estado.getField(sigla.getNameSimple())))
                .displayString("${nome}")
                .label("Cidade")
                .enabled(inst -> inst.findNearest(estado).get().asAtr().isEnabled()
                        && Value.notNull(inst, (STypeSimple) estado.getField(sigla.getNameSimple())))
                .dependsOn(estado)
                .asAtrBootstrap()
                .colPreference(3);

        cidade
                .selection()
                .id(idCidade)
                .display(nomeCidade)
                .simpleProvider((SSimpleProvider) builder -> {
                    SelectBuilder
                            .buildMunicipiosFiltrado(Value.of(builder.getCurrentInstance(), sigla))
                            .forEach(cidadeDTO -> {
                                builder.add()
                                        .set(idCidade, cidadeDTO.getId())
                                        .set(nomeCidade, cidadeDTO.getNome())
                                        .set(ufCidade, cidadeDTO.getUF());
                            });
                });

        bairro
                .asAtr()
                .label("Bairro")
                .asAtrBootstrap()
                .colPreference(3);

        endereco
                .asAtr()
                .label("Endereco")
                .asAtrBootstrap()
                .colPreference(6);

        telefone
                .asAtr()
                .label("Telefone")
                .asAtrBootstrap()
                .colPreference(2);

        fax
                .asAtr()
                .label("Fax")
                .asAtrBootstrap()
                .colPreference(2);

        celular
                .asAtr()
                .label("Celular")
                .asAtrBootstrap()
                .colPreference(2);

    }
}