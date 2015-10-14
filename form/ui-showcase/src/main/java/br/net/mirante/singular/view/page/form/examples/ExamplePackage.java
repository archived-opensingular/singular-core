package br.net.mirante.singular.view.page.form.examples;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.util.comuns.MTipoNomePessoa;

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
		
		MTabView tabbed = order.setView(MTabView::new);
		tabbed.addTab("Dados");
		
		MTipoComposto<?> buyer = order.addCampoComposto("Buyer");
		buyer.as(AtrBasic::new).label("Comprador");
		
		MTipoNomePessoa name = buyer.addCampo("Name",MTipoNomePessoa.class);
		name.as(AtrBasic::new).label("Nome");
		
		pb.debug();
	}
	
}
