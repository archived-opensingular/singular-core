package br.net.mirante.singular.view.page.form.examples;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCEP;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;
import br.net.mirante.singular.form.mform.util.comuns.MTipoNomePessoa;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;

public class ExamplePackage extends MPacote {

    private static final String PACKAGE = "mform.exemplo.uiShowcase";

    public enum Types {
        ORDER(PACKAGE + ".Order");

        public final String name;

        Types(String name) {
            this.name = name;
        }
    }

    public ExamplePackage() {
        super(PACKAGE);
    }

    @Override
    public void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> order = pb.createTipoComposto("Order");

        order.as(AtrBasic::new).label("Pedido");

        addField(order, "OrderNumber", "Número do Pedido", MTipoInteger.class);

        MTipoComposto<?> buyer = order.addCampoComposto("Buyer");
        buyer.as(AtrBasic::new).label("Comprador");

        addField(buyer, "Name", "Nome", MTipoNomePessoa.class);
        addField(buyer, "CPF", "CPF", MTipoCPF.class);
        addField(buyer, "Telephone", "Telefone", MTipoTelefoneNacional.class);
        addField(buyer, "Avatar", "Imagem", MTipoAttachment.class);

        MTipoComposto<?> address = order.addCampoComposto("Addresss");
        address.as(AtrBasic::new).label("Endereço");
        addField(address, "street", "Logradouro", MTipoString.class);
        addField(address, "city", "Cidade", MTipoString.class);
        addField(address, "state", "Estado", MTipoString.class);
        addField(address, "Zipcode", "CEP", MTipoCEP.class);
    }

    private <I extends MInstancia, T extends MTipo<I>> void addField(MTipoComposto<?> root, String name, String label,
            Class<T> type) {
        root.addCampo(name, type).as(AtrBasic::new).label(label);
    }
}
