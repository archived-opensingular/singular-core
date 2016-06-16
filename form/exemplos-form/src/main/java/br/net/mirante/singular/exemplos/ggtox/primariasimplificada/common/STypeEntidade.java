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
import br.net.mirante.singular.form.util.transformer.SCompositeListBuilder;
import br.net.mirante.singular.form.util.transformer.Value;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeEntidade extends STypeComposite<SIComposite> {


    public static final String TIPO_PESSOA         = "tipoPessoa";
    public static final String CNPJ                = "CNPJ";
    public static final String CPF                 = "CPF";
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

    @Override
    protected void onLoadType(TypeBuilder tb) {

        super.onLoadType(tb);

        final STypeString           tipoPessoa         = addField(TIPO_PESSOA, STypeString.class);
        final STypeCPF              cnpj               = addField(CNPJ, STypeCPF.class);
        final STypeCNPJ             cpf                = addField(CPF, STypeCNPJ.class);
        final STypeString           nome               = addField(NOME, STypeString.class);
        final STypeEMail            enderecoEletronico = addField(ENDERECO_ELETRONICO, STypeEMail.class);
        final STypeCEP              cep                = addField(CEP, STypeCEP.class);
        final STypeComposite<?>     estado             = addFieldComposite(ESTADO);
        final STypeString           siglaUF            = estado.addFieldString(SIGLA_UF);
        final STypeString           nomeUF             = estado.addFieldString(NOME_UF);
        final STypeComposite<?>     cidade             = addFieldComposite(CIDADE);
        final STypeString           idCidade           = cidade.addFieldString(ID_CIDADE);
        final STypeString           nomeCidade         = cidade.addFieldString(NOME_CIDADE);
        final STypeString           ufCidade           = cidade.addFieldString(UF_CIDADE);
        final STypeString           bairro             = addField(BAIRRO, STypeString.class);
        final STypeString           endereco           = addField(ENDERECO, STypeString.class);
        final STypeTelefoneNacional telefone           = addField(TELEFONE, STypeTelefoneNacional.class);
        final STypeString           fax                = addField(FAX, STypeString.class);
        final STypeTelefoneNacional celular            = addField(CELULAR, STypeTelefoneNacional.class);

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
                .visible(i -> i.findNearestValue(tipoPessoa).orElse("").equals("Juridica"))
                .asAtrBootstrap()
                .colPreference(3);

        cpf
                .asAtr()
                .label("CPF")
                .dependsOn(tipoPessoa)
                .visible(i -> i.findNearestValue(tipoPessoa).orElse("").equals("Fisica"))
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
                .asAtr()
                .label("Estado")
                .asAtrBootstrap()
                .colPreference(3);

        estado
                .selection()
                .id(siglaUF)
                .display(nomeUF)
                .simpleProvider((SSimpleProvider) builder -> {
                    SelectBuilder
                            .buildEstados()
                            .forEach(estadoDTO -> {
                                builder.add()
                                        .set(siglaUF, estadoDTO.getSigla())
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
                .required(inst -> Value.notNull(inst, (STypeSimple) estado.getField(siglaUF)))
                .asAtr()
                .label("Cidade")
                .enabled(inst -> Value.notNull(inst, (STypeSimple) estado.getField(siglaUF)))
                .dependsOn(estado)
                .asAtrBootstrap()
                .colPreference(3);

        cidade
                .selection()
                .id(idCidade)
                .display(nomeCidade)
                .simpleProvider((SSimpleProvider) builder -> {
                    SelectBuilder
                            .buildMunicipiosFiltrado(Value.of(builder.getCurrentInstance(), siglaUF))
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