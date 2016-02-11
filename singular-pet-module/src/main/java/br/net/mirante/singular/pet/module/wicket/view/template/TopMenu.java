package br.net.mirante.singular.pet.module.wicket.view.template;

import br.net.mirante.singular.pet.module.spring.security.SecurityUtil;
import br.net.mirante.singular.pet.module.wicket.PetSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Optional;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class TopMenu extends Panel {

    private boolean withSideBar;

    public TopMenu(String id, boolean withSideBar) {
        super(id);
        this.withSideBar = withSideBar;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        buildContent();
    }

    protected void buildContent() {
        queue(new WebMarkupContainer("sideBarToggle").setVisible(withSideBar));
        queue(new Label("nome", $m.ofValue(PetSession.get().getName())));

        WebMarkupContainer avatar = new WebMarkupContainer("codrh");
        Optional<String> avatarSrc = Optional.ofNullable(null);
        avatarSrc.ifPresent(src -> avatar.add($b.attr("src", src)));
        queue(avatar);

        WebMarkupContainer logout = new WebMarkupContainer("logout");
        logout.add($b.attr("href", SecurityUtil.getLogoutPath()));
        queue(logout);
    }
}
