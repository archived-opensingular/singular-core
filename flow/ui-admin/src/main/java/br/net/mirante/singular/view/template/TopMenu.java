package br.net.mirante.singular.view.template;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.wicket.UIAdminSession;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

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
        queue(new WebMarkupContainer("codrh").add($b.attr("src", "/alocpro/wicket/imagemPessoa?codRh="
                + UIAdminSession.get().getCodRh())));
    }
}
