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

package org.opensingular.singular.form.showcase.view.page.form.examples;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.country.brazil.STypeTelefoneNacional;
import org.opensingular.form.type.util.STypePersonName;
import org.opensingular.form.validation.validator.InstanceValidators;
import org.opensingular.form.view.SViewListByForm;
import org.opensingular.form.view.SViewSelectionBySelect;

@SInfoType(spackage = ExamplePackage.class, name = "STypeExample")
public class STypeExample extends STypeComposite<SIComposite> {


    public STypeInteger                                        orderNumber;
    public STypeComposite<?>                                   buyer;
    public STypePersonName                                     buyerNome;
    public STypeCPF                                            buyerCpf;
    public STypeTelefoneNacional                               buyerTelephone;
    public STypeAttachment                                     buyerAvatar;
    public STypeComposite<SIComposite>                         address;
    public STypeString                                         addressStreet;
    public STypeString                                         addressCity;
    public STypeString                                         addressState;
    public STypeCEP                                            addressZipcode;
    public STypeList<STypeComposite<SIComposite>, SIComposite> originCountry;
    public STypeComposite<SIComposite>                         country;
    public STypeString                                         name;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        buildOrderType(tb);
    }

    private void buildOrderType(TypeBuilder tb) {

        this.asAtr().label("Pedido");

        this.orderNumber = addField(this, "orderNumber", "Número do Pedido", STypeInteger.class);
        this.orderNumber.withRequired(true);

        buildBuyerField();
        buildAddressField();

        this.originCountry = this.addFieldListOfComposite("originCountry", "country");
        this.country = originCountry.getElementsType();
        this.originCountry.withView(SViewListByForm::new);
        this.originCountry.asAtr().label("Países");

        this.name = country.addFieldString("name");
        this.name.asAtr().label("Nome");
        this.country.addFieldInteger("population").asAtr().label("População");
//        country.withSelectionFromProvider(name,(instance, lb) -> {
//            lb.add().set(name,"Brazil")
//                    .add().set(name,"USA")
//                    .add().set(name,"Canada")
//                    .add().set(name,"Bosnia")
//                    .add().set(name,"Argentina")
//                    .add().set(name,"Chile");
//        });
        this.country.withView(SViewSelectionBySelect::new);
    }

    private void buildBuyerField() {
        this.buyer = addFieldComposite("buyer");
        this.buyer.asAtr().label("Comprador");
        this.buyerNome = addField(buyer, "buyerNome", "Nome", STypePersonName.class);
        this.buyerCpf = addField(buyer, "buyerCpf", "CPF", STypeCPF.class);
        this.buyerTelephone = addField(buyer, "buyerTelephone", "Telefone", STypeTelefoneNacional.class);
        this.buyerAvatar = addField(buyer, "buyerAvatar", "Imagem", STypeAttachment.class);

        this.buyerNome.asAtr().required();

        this.buyerCpf
                .asAtr()
                .dependsOn(this.buyerNome)
        ;
    }

    private void buildAddressField() {
        this.address = addFieldComposite("address");
        this.address.asAtr().label("Endereço");
        this.addressStreet = addField(address, "addressStreet", "Logradouro", STypeString.class);
        this.addressCity = addField(address, "addressCity", "Cidade", STypeString.class);
        this.addressState = addField(address, "addressState", "Estado", STypeString.class);
        this.addressZipcode = addField(address, "addressZipcode", "CEP", STypeCEP.class);

        this.address.addInstanceValidator(InstanceValidators.allOrNothing());
    }

    private <I extends SInstance, T extends SType<I>> T addField(STypeComposite<?> root, String name, String label,
                                                                 Class<T> type) {
        T campo = root.addField(name, type);
        campo.asAtr().label(label);
        return campo;
    }

}
