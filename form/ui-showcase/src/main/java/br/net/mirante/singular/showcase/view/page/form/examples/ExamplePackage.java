/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.brasil.STypeCEP;
import br.net.mirante.singular.form.mform.util.brasil.STypeCPF;
import br.net.mirante.singular.form.mform.util.brasil.STypeTelefoneNacional;
import br.net.mirante.singular.form.mform.util.comuns.STypePersonName;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

public class ExamplePackage extends SPackage {

    private static final String PACKAGE = "mform.exemplo.uiShowcase";

    public enum Types {
        ORDER(PACKAGE + ".Order");

        public final String name;

        Types(String name) {
            this.name = name;
        }
    }

    public STypeComposite<? extends SIComposite> order;
    public STypeInteger orderNumber;
    public STypeComposite<?> buyer;
    public STypePersonName buyerNome;
    public STypeCPF buyerCpf;
    public STypeTelefoneNacional buyerTelephone;
    public STypeAttachment buyerAvatar;

    public STypeComposite<SIComposite> address;
    public STypeString addressStreet;
    public STypeString addressCity;
    public STypeString addressState;
    public STypeCEP addressZipcode;

    public ExamplePackage() {
        super(PACKAGE);
    }

    @Override
    public void carregarDefinicoes(PackageBuilder pb) {
        buildOrderType(pb);
    }

    private void buildOrderType(PackageBuilder pb) {
        this.order = pb.createCompositeType("Order");
        this.order.asAtrBasic().label("Pedido");

        this.orderNumber = addField(order,
            "OrderNumber", "Número do Pedido", STypeInteger.class);
        this.orderNumber.withRequired(true);

        buildBuyerField();
        buildAddressField();
    }

    private void buildBuyerField() {
        this.buyer = order.addFieldComposite("Buyer");
        this.buyer.asAtrBasic().label("Comprador");
        this.buyerNome = addField(buyer, "Name", "Nome", STypePersonName.class);
        this.buyerCpf = addField(buyer, "CPF", "CPF", STypeCPF.class);
        this.buyerTelephone = addField(buyer, "Telephone", "Telefone", STypeTelefoneNacional.class);
        this.buyerAvatar = addField(buyer, "Avatar", "Imagem", STypeAttachment.class);

        this.buyerNome.as(SPackageBasic.aspect()).required();

        this.buyerCpf
            .as(SPackageBasic.aspect())
            .dependsOn(this.buyerNome)
        ;
    }

    private void buildAddressField() {
        this.address = order.addFieldComposite("Address");
        this.address.asAtrBasic().label("Endereço");
        this.addressStreet = addField(address, "street", "Logradouro", STypeString.class);
        this.addressCity = addField(address, "city", "Cidade", STypeString.class);
        this.addressState = addField(address, "state", "Estado", STypeString.class);
        this.addressZipcode = addField(address, "Zipcode", "CEP", STypeCEP.class);

        this.address.addInstanceValidator(InstanceValidators.allOrNothing());
    }

    private <I extends SInstance, T extends SType<I>> T addField(STypeComposite<?> root, String name, String label,
                                                                 Class<T> type) {
        T campo = root.addField(name, type);
        campo.asAtrBasic().label(label);
        return campo;
    }
}
