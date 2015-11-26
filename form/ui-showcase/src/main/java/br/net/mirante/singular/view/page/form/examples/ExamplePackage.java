package br.net.mirante.singular.view.page.form.examples;

import static org.apache.commons.lang3.StringUtils.*;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCEP;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;
import br.net.mirante.singular.form.mform.util.comuns.MTipoNomePessoa;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;
import br.net.mirante.singular.form.validation.validator.AllOrNothingInstanceValidator;

public class ExamplePackage extends MPacote {

    private static final String PACKAGE = "mform.exemplo.uiShowcase";

    public enum Types {
        ORDER(PACKAGE + ".Order");

        public final String name;

        Types(String name) {
            this.name = name;
        }
    }

    public MTipoComposto<? extends MIComposto> order;
    public MTipoInteger                        orderNumber;
    public MTipoComposto<?>                    buyer;
    public MTipoNomePessoa                     buyerNome;
    public MTipoCPF                            buyerCpf;
    public MTipoTelefoneNacional               buyerTelephone;
    public MTipoAttachment                     buyerAvatar;

    public MTipoComposto<MIComposto> address;
    public MTipoString               addressStreet;
    public MTipoString               addressCity;
    public MTipoString               addressState;
    public MTipoCEP                  addressZipcode;

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

        this.orderNumber = addField(order, "OrderNumber", "Número do Pedido", MTipoInteger.class);

        buildBuyerField();
        buildAddressField();
    }

    private void buildBuyerField() {
        this.buyer = order.addCampoComposto("Buyer");
        this.buyer.as(AtrBasic::new).label("Comprador");
        this.buyerNome = addField(buyer, "Name", "Nome", MTipoNomePessoa.class);
        this.buyerCpf = addField(buyer, "CPF", "CPF", MTipoCPF.class);
        this.buyerTelephone = addField(buyer, "Telephone", "Telefone", MTipoTelefoneNacional.class);
        this.buyerAvatar = addField(buyer, "Avatar", "Imagem", MTipoAttachment.class);

        this.buyerNome.as(MPacoteCore.aspect()).obrigatorio(true);
        this.buyerNome.as(MPacoteBasic.aspect()).onChange((ctx, i) -> ctx.update(buyerCpf));

        buyerCpf.as(MPacoteBasic.aspect())
            .visivel(i -> defaultString(i.findAncestor(buyer).get().findDescendant(buyerNome).get().getValor()).length() > 3)
            .enabled(i -> defaultString(i.findAncestor(buyer).get().findDescendant(buyerNome).get().getValor()).length() > 5);
    }

    private void buildAddressField() {
        this.address = order.addCampoComposto("Address");
        this.address.as(AtrBasic::new).label("Endereço");
        this.addressStreet = addField(address, "street", "Logradouro", MTipoString.class);
        this.addressCity = addField(address, "city", "Cidade", MTipoString.class);
        this.addressState = addField(address, "state", "Estado", MTipoString.class);
        this.addressZipcode = addField(address, "Zipcode", "CEP", MTipoCEP.class);

        this.address.addInstanceValidator(new AllOrNothingInstanceValidator());
    }

    private <I extends MInstancia, T extends MTipo<I>> T addField(MTipoComposto<?> root, String name, String label,
        Class<T> type) {
        T campo = root.addCampo(name, type);
        campo.as(AtrBasic::new).label(label);
        return campo;
    }
}
