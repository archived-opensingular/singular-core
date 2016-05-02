/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;

public class SPackagePrototype extends SPackage {

    public static final String PACOTE = "mform.prototype",
            META_FORM = "MetaForm",
            META_FORM_COMPLETE = PACOTE + "." + META_FORM,
            CHILDREN = "children",
            NAME = "name",
            TYPE = "type",
            IS_LIST = "isList",
            TAMANHO_CAMPO = "tamanhoCampo",
            OBRIGATORIO = "obrigatorio",
            TAMANHO_MAXIMO = "tamanhoMaximo",
            TAMANHO_INTEIRO_MAXIMO = "tamanhoInteiroMaximo",
            TAMANHO_DECIMAL_MAXIMO = "tamanhoDecimalMaximo",
            FIELDS = "fields";
    public static final String NAME_FIELD = "name";

    private STypeInteger tamanhoCampo;
    private STypeBoolean obrigatorio;

    public SPackagePrototype() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> meta = pb.createCompositeType(META_FORM);
        meta.addFieldString(NAME_FIELD).asAtr().label("Nome")
                .asAtr().required();

        STypeList<STypeComposite<SIComposite>, SIComposite> childFields =
                meta.addFieldListOfComposite(CHILDREN, "field");

        childFields.asAtr().label("Campos");

        STypeComposite<SIComposite> fieldType = childFields.getElementsType();

        STypeString nome = fieldType.addFieldString(NAME);
        nome.asAtr().label("Nome")
                .asAtr().required()
                .asAtrBootstrap().colPreference(3);

        STypeString type = fieldType.addFieldString(TYPE);
        type.asAtr().label("Tipo")
                .asAtr().required()
                .asAtrBootstrap().colPreference(2);
        //TODO DANILO
//        populateOptions(pb, type.withSelection());

        fieldType.addFieldBoolean(IS_LIST)
                .withRadioView()
                .withDefaultValueIfNull(false)
                .asAtr().label("Múltiplo").getTipo().asAtrBootstrap().colPreference(2);

        addAttributeFields(pb, fieldType, type);

        childFields.withView(new SViewListByMasterDetail()
                .col(nome)
                .col(type)
                .col(tamanhoCampo)
                .col(obrigatorio)
        );

        addFields(pb, fieldType, type);

    }

//    private void populateOptions(PackageBuilder pb, SFixedOptionsSimpleProvider provider) {
//        provider.add(typeName(pb, STypeAttachment.class), "Anexo");
//        provider.add(typeName(pb, STypeYearMonth.class), "Ano/Mês");
//        provider.add(typeName(pb, STypeBoolean.class), "Booleano");
//        provider.add(typeName(pb, STypeComposite.class), "Composto");
//        provider.add(typeName(pb, STypeCEP.class), "CEP");
//        provider.add(typeName(pb, STypeCPF.class), "CPF");
//        provider.add(typeName(pb, STypeCNPJ.class), "CNPJ");
//        provider.add(typeName(pb, STypeDate.class), "Data");
//        provider.add(typeName(pb, STypeDateTime.class), "Data/Hora");
//        provider.add(typeName(pb, STypeEMail.class), "Email");
//        provider.add(typeName(pb, STypeLatitudeLongitude.class), "Latitude/Longitude");
//        provider.add(typeName(pb, STypeMonetary.class), "Monetário");
//        provider.add(typeName(pb, STypePersonName.class), "Nome Pessoa");
//        provider.add(typeName(pb, STypeInteger.class), "Número");
//        provider.add(typeName(pb, STypeDecimal.class), "Número Decimal");
//        provider.add(typeName(pb, STypeString.class), "Texto");
//        provider.add(typeName(pb, STypeTelefoneNacional.class), "Telefone Nacional");
//    }

    private String typeName(PackageBuilder pb, Class<? extends SType> typeClass) {
        return pb.getDictionary().getType(typeClass).getName();
    }

    private void addAttributeFields(PackageBuilder pb, STypeComposite<SIComposite> fieldType, STypeString type) {
        tamanhoCampo = fieldType.addFieldInteger(TAMANHO_CAMPO);
        tamanhoCampo.asAtr().label("Colunas").tamanhoMaximo(12)
                .getTipo().asAtrBootstrap().colPreference(2);

        obrigatorio = fieldType.addFieldBoolean(OBRIGATORIO);
        obrigatorio.withRadioView().asAtr().label("Obrigatório").getTipo().asAtrBootstrap().colPreference(2);

        fieldType.addFieldInteger(TAMANHO_MAXIMO)
                .asAtrBootstrap().colPreference(2)
                .getTipo().asAtr().label("Tamanho Máximo")
                .visible(
                        (instance) -> {
                            Optional<String> optType = instance.findNearestValue(type, String.class);
                            if (!optType.isPresent()) return false;
                            return optType.get().equals(typeName(pb, STypeInteger.class));
                        }
                );

        Predicate<SInstance> ifDecimalPredicate = (instance) -> {
            Optional<String> optType = instance.findNearestValue(type, String.class);
            if (!optType.isPresent()) return false;
            return optType.get().equals(typeName(pb, STypeDecimal.class));
        };

        fieldType.addFieldInteger(TAMANHO_INTEIRO_MAXIMO)
                .asAtrBootstrap().colPreference(2)
                .getTipo().asAtr()
                .label("Tamanho Inteiro")
                .visible(ifDecimalPredicate);

        fieldType.addFieldInteger(TAMANHO_DECIMAL_MAXIMO)
                .asAtrBootstrap().colPreference(2)
                .getTipo().asAtr()
                .label("Tamanho Decimal")
                .visible(ifDecimalPredicate);
    }

    private void addFields(PackageBuilder pb, STypeComposite<SIComposite> fieldType, STypeString type) {
        STypeList<STypeComposite<SIComposite>, SIComposite> fields =
                fieldType.addFieldListOf(FIELDS, fieldType);
        fields.asAtr().label("Campos")
                .getTipo().withView(SViewListByMasterDetail::new)
                .withExists(
                        (instance) -> {
                            Optional<String> optType = instance.findNearestValue(type, String.class);
                            if (!optType.isPresent()) return false;
                            return optType.get().equals(typeName(pb, STypeComposite.class));
                        })
                .asAtr().dependsOn(() -> {
            return newArrayList(type);
        })
        ;
    }
}
