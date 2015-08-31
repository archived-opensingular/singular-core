package br.net.mirante.singular.view.template;

import br.net.mirante.singular.wicket.UIAdminSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import static br.net.mirante.singular.view.Behaviors.$b;
import static br.net.mirante.singular.view.Models.$m;

public class TopMenu extends Panel {

    private boolean withSideBar;

    public TopMenu(String id, boolean withSideBar) {
        super(id);
        this.withSideBar = withSideBar;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new WebMarkupContainer("sideBarToggle").setVisible(withSideBar));
        queue(new Label("nome", $m.ofValue(UIAdminSession.get().getNome())));
        queue(new WebMarkupContainer("codrh").add($b.attr("src", "/alocpro/wicket/imagemPessoa?codRh=" + UIAdminSession.get().getCodRh())));
    }
}
