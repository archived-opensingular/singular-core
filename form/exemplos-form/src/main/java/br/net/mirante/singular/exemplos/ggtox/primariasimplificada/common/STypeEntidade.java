package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.SelectBuilder;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.provider.SSimpleProvider;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.country.brazil.STypeCNPJ;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.util.transformer.Value;

import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.STypePeticaoPrimariaSimplificada.OBRIGATORIO;
import static br.net.mirante.singular.form.util.SingularPredicates.*;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeEntidade extends STypePersistentComposite {

    public static final String TIPO_ENTIDADE       = "tipoEntidade";
    public static final String TIPO_PESSOA         = "tipoPessoa";
    public static final String CNPJ                = "cnpj";
    public static final String CPF                 = "cpf";
    public static final String NOME                = "nome";
    public static final String ENDERECO_ELETRONICO = "enderecoEletronico";
    public static final String CEP                 = "cep";
    public static final String ZIPCODE             = "zipcode";
    public static final String PAIS                = "pais";
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

    public STypeString           tipoEntidade;
    public STypeString           tipoPessoa;
    public STypeCNPJ             cnpj;
    public STypeCPF              cpf;
    public STypeString           nome;
    public STypeEMail            enderecoEletronico;
    public STypeCEP              cep;
    public STypeString           pais;
    public STypeInteger          zipcode;
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

        tipoEntidade = addField(TIPO_ENTIDADE, STypeString.class);
        tipoPessoa = addField(TIPO_PESSOA, STypeString.class);
        cnpj = addField(CNPJ, STypeCNPJ.class);
        cpf = addField(CPF, STypeCPF.class);
        nome = addField(NOME, STypeString.class);
        enderecoEletronico = addField(ENDERECO_ELETRONICO, STypeEMail.class);
        pais = addField(PAIS, STypeString.class);
        zipcode = addField(ZIPCODE, STypeInteger.class);
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

        tipoEntidade
                .selectionOf("Nacional", "Internacional")
                .withRadioView()
                .asAtr()
                .required(OBRIGATORIO)
                .label("Tipo de Entidade")
                .asAtrBootstrap()
                .colPreference(12);

        nacional(tipoPessoa)
                .selectionOf("Física", "Jurídica")
                .withRadioView()
                .asAtr()
                .required(OBRIGATORIO)
                .label("Tipo de Pessoa")
                .asAtrBootstrap()
                .colPreference(3);

        cnpj
                .asAtr()
                .required(OBRIGATORIO)
                .label("CNPJ")
                .dependsOn(tipoPessoa, tipoEntidade)
                .exists(allMatches(typeValueIsEqualsTo(tipoPessoa, "Jurídica"), typeValueIsEqualsTo(tipoEntidade, "Nacional")))
                .asAtrBootstrap()
                .colPreference(3);

        cpf
                .asAtr()
                .required(OBRIGATORIO)
                .label("CPF")
                .dependsOn(tipoPessoa, tipoEntidade)
                .exists(allMatches(typeValueIsEqualsTo(tipoPessoa, "Física"), typeValueIsEqualsTo(tipoEntidade, "Nacional")))
                .asAtrBootstrap()
                .colPreference(3);

        comum(nome)
                .asAtr()
                .required(OBRIGATORIO)
                .label("Nome")
                .asAtrBootstrap()
                .newRow()
                .colPreference(8);

        comum(enderecoEletronico)
                .asAtr()
                .required(OBRIGATORIO)
                .label("Endereço Eletrônico")
                .asAtrBootstrap()
                .colPreference(4);

        internacional(zipcode)
                .asAtr()
                .required(OBRIGATORIO)
                .label("Zipcode")
                .asAtrBootstrap()
                .colPreference(3);

        internacional(pais)
                .asAtr()
                .required(OBRIGATORIO)
                .label("Pais")
                .asAtrBootstrap()
                .colPreference(3);

        nacional(cep)
                .asAtr()
                .required(OBRIGATORIO)
                .label("CEP")
                .asAtrBootstrap()
                .colPreference(3);

        nacional(estado)
                .asAtr()
                .required(OBRIGATORIO)
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

        nacional(cidade)
                .asAtr()
                .required(OBRIGATORIO)
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

        nacional(bairro)
                .asAtr()
                .required(OBRIGATORIO)
                .label("Bairro")
                .asAtrBootstrap()
                .colPreference(3);

        nacional(endereco)
                .asAtr()
                .required(OBRIGATORIO)
                .label("Endereço")
                .asAtrBootstrap()
                .colPreference(6);

        comum(telefone)
                .asAtr()
                .required(OBRIGATORIO)
                .label("Telefone")
                .asAtrBootstrap()
                .colPreference(2);

        nacional(fax)
                .asAtr()
                .label("Fax")
                .asAtrBootstrap()
                .colPreference(2);

        comum(celular)
                .asAtr()
                .label("Celular")
                .asAtrBootstrap()
                .colPreference(2);

    }

    public <T extends SType> T nacional(T tipo) {
        tipo.asAtr().dependsOn(tipoEntidade).visible(typeValueIsEqualsTo(tipoEntidade, "Nacional"));
        return tipo;
    }

    public <T extends SType> T internacional(T tipo) {
        tipo.asAtr().dependsOn(tipoEntidade).visible(typeValueIsEqualsTo(tipoEntidade, "Internacional"));
        return tipo;
    }

    public <T extends SType> T comum(T tipo) {
        tipo.asAtr().dependsOn(tipoEntidade).visible(typeValueIsNotNull(tipoEntidade));
        return tipo;
    }

}