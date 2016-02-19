package br.net.mirante.singular.showcase.view.template;

import java.util.Optional;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.showcase.wicket.UIAdminSession;

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
        queue(new WebMarkupContainer("sideBarToggle").setVisible(withSideBar));
        queue(new Label("nome", $m.ofValue(UIAdminSession.get().getName())));

        WebMarkupContainer avatar = new WebMarkupContainer("codrh");
        Optional<String> avatarSrc = Optional.ofNullable(UIAdminSession.get().getAvatar());
        avatarSrc.ifPresent(src -> avatar.add($b.attr("src", src)));
        queue(avatar);

        WebMarkupContainer logout = new WebMarkupContainer("logout");
        Optional<String> logoutHref = Optional.ofNullable(UIAdminSession.get().getLogout());
        logoutHref.ifPresent(href -> logout.add($b.attr("href", href)));
        queue(logout);
    }
}
