/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.singular.form.showcase.view.page.prototype;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.country.brazil.STypeTelefoneNacional;
import org.opensingular.form.type.util.STypeEMail;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.type.util.STypePersonName;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;

public class SPackagePrototype extends SPackage {

    public static final String PACOTE = "mform.prototype",
            META_FORM                 = "MetaForm",
            META_FORM_COMPLETE        = PACOTE + "." + META_FORM,
            CHILDREN                  = "children",
            NAME                      = "name",
            TYPE                      = "type",
            IS_LIST                   = "isList",
            TAMANHO_CAMPO             = "tamanhoCampo",
            CAMPO_OBRIGATORIO         = "obrigatorio",
            TAMANHO_MAXIMO            = "tamanhoMaximo",
            TAMANHO_INTEIRO_MAXIMO    = "tamanhoInteiroMaximo",
            TAMANHO_DECIMAL_MAXIMO    = "tamanhoDecimalMaximo",
            FIELDS                    = "fields";
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
                .withDefaultValueIfNull(Boolean.FALSE)
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
        tamanhoCampo.asAtr().label("Colunas").maxLength(12)
                .getTipo().asAtrBootstrap().colPreference(2);

        obrigatorio = fieldType.addFieldBoolean(CAMPO_OBRIGATORIO);
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
