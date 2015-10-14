package br.net.mirante.singular.view.page.form.examples;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCEP;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;
import br.net.mirante.singular.form.mform.util.comuns.MTipoNomePessoa;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;

public class ExamplePackage extends MPacote {

	private static final String PACKAGE = "mform.exemplo.uiShowcase";

	public enum Types {
		ORDER(PACKAGE+".Order");
		
		public final String name;
		private Types(String name) {
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
		
		addField(order, "Name", "Nome", MTipoNomePessoa.class);
		addField(order, "CPF", "CPF", MTipoCPF.class);
		addField(order, "Telephone", "Telefone", MTipoTelefoneNacional.class);
		addField(order, "Address", "Endereço", MTipoString.class);
		addField(order, "Zipcode", "CEP", MTipoCEP.class);
		
		pb.debug();
	}


	private void addField(MTipoComposto<?> root, String name, String label, Class<? extends MTipo> type) {
		MTipo<?> number = root.addCampo(name,type);
		number.as(AtrBasic::new).label(label);
	}
	
}
