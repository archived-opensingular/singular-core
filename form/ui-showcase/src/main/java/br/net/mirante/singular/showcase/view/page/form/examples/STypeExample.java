package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;
import br.net.mirante.singular.form.type.util.STypePersonName;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.view.SViewSelectionBySelect;

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
