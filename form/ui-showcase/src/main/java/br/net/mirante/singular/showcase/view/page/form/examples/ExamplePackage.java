package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.comuns.STypeCEP;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;
import br.net.mirante.singular.form.mform.util.comuns.STypeNomePessoa;
import br.net.mirante.singular.form.mform.util.comuns.STypeTelefoneNacional;
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

    public STypeComposto<? extends SIComposite> order;
    public STypeInteger orderNumber;
    public STypeComposto<?> buyer;
    public STypeNomePessoa buyerNome;
    public STypeCPF buyerCpf;
    public STypeTelefoneNacional buyerTelephone;
    public STypeAttachment buyerAvatar;

    public STypeComposto<SIComposite> address;
    public STypeString addressStreet;
    public STypeString addressCity;
    public STypeString addressState;
    public STypeCEP addressZipcode;

    public ExamplePackage() {
        super(PACKAGE);
    }

    @Override
    public void carregarDefinicoes(PacoteBuilder pb) {
        buildOrderType(pb);
    }

    private void buildOrderType(PacoteBuilder pb) {
        this.order = pb.createTipoComposto("Order");
        this.order.as(AtrBasic::new).label("Pedido");

        this.orderNumber = addField(order,
            "OrderNumber", "Número do Pedido", STypeInteger.class);
        this.orderNumber.withObrigatorio(true);

        buildBuyerField();
        buildAddressField();
    }

    private void buildBuyerField() {
        this.buyer = order.addCampoComposto("Buyer");
        this.buyer.as(AtrBasic::new).label("Comprador");
        this.buyerNome = addField(buyer, "Name", "Nome", STypeNomePessoa.class);
        this.buyerCpf = addField(buyer, "CPF", "CPF", STypeCPF.class);
        this.buyerTelephone = addField(buyer, "Telephone", "Telefone", STypeTelefoneNacional.class);
        this.buyerAvatar = addField(buyer, "Avatar", "Imagem", STypeAttachment.class);

        this.buyerNome.as(SPackageCore.aspect()).obrigatorio();

        this.buyerCpf
            .as(SPackageBasic.aspect())
            .dependsOn(this.buyerNome)
        ;
    }

    private void buildAddressField() {
        this.address = order.addCampoComposto("Address");
        this.address.as(AtrBasic::new).label("Endereço");
        this.addressStreet = addField(address, "street", "Logradouro", STypeString.class);
        this.addressCity = addField(address, "city", "Cidade", STypeString.class);
        this.addressState = addField(address, "state", "Estado", STypeString.class);
        this.addressZipcode = addField(address, "Zipcode", "CEP", STypeCEP.class);

        this.address.addInstanceValidator(InstanceValidators.allOrNothing());
    }

    private <I extends SInstance2, T extends SType<I>> T addField(STypeComposto<?> root, String name, String label,
                                                                  Class<T> type) {
        T campo = root.addCampo(name, type);
        campo.as(AtrBasic::new).label(label);
        return campo;
    }
}
