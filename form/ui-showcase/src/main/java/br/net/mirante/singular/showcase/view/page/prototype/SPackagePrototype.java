/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.type.core.*;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.country.brazil.STypeCNPJ;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.type.util.STypeLatitudeLongitude;
import br.net.mirante.singular.form.type.util.STypePersonName;
import br.net.mirante.singular.form.type.util.STypeYearMonth;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
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
    protected void onLoadPackage(PackageBuilder pb) {
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

        List<Pair> typesList = new ArrayList<>();
        populateOptions(pb, typesList);

        type.selectionOf(Pair.class)
                .id("${left}")
                .display("${right}")
                .converter(new SInstanceConverter<Pair, SIString>() {
                    @Override
                    public void fillInstance(SIString ins, Pair obj) {
                        ins.setValue(obj.getLeft());
                    }

                    @Override
                    public Pair toObject(SIString ins) {
                        return typesList
                                .stream()
                                .filter(p -> p.getLeft().equals(ins.getValue()))
                                .findFirst().orElse(null);
                    }
                })
                .simpleProviderOf(typesList.toArray(new Pair[]{}));

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

    private void populateOptions(PackageBuilder pb, List<Pair> list) {
        list.add(Pair.of(typeName(pb, STypeAttachment.class), "Anexo"));
        list.add(Pair.of(typeName(pb, STypeYearMonth.class), "Ano/Mês"));
        list.add(Pair.of(typeName(pb, STypeBoolean.class), "Booleano"));
        list.add(Pair.of(typeName(pb, STypeComposite.class), "Composto"));
        list.add(Pair.of(typeName(pb, STypeCEP.class), "CEP"));
        list.add(Pair.of(typeName(pb, STypeCPF.class), "CPF"));
        list.add(Pair.of(typeName(pb, STypeCNPJ.class), "CNPJ"));
        list.add(Pair.of(typeName(pb, STypeDate.class), "Data"));
        list.add(Pair.of(typeName(pb, STypeDateTime.class), "Data/Hora"));
        list.add(Pair.of(typeName(pb, STypeEMail.class), "Email"));
        list.add(Pair.of(typeName(pb, STypeLatitudeLongitude.class), "Latitude/Longitude"));
        list.add(Pair.of(typeName(pb, STypeMonetary.class), "Monetário"));
        list.add(Pair.of(typeName(pb, STypePersonName.class), "Nome Pessoa"));
        list.add(Pair.of(typeName(pb, STypeInteger.class), "Número"));
        list.add(Pair.of(typeName(pb, STypeDecimal.class), "Número Decimal"));
        list.add(Pair.of(typeName(pb, STypeString.class), "Texto"));
        list.add(Pair.of(typeName(pb, STypeTelefoneNacional.class), "Telefone Nacional"));
    }

    private String typeName(PackageBuilder pb, Class<? extends SType> typeClass) {
        return pb.getType(typeClass).getName();
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
