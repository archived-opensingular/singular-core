package br.net.mirante.singular.server.commons.wicket.view.template;

import java.util.Optional;

import br.net.mirante.singular.server.commons.spring.security.SecurityUtil;
import br.net.mirante.singular.server.commons.wicket.PetSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.util.wicket.template.SkinOptions;
import br.net.mirante.singular.util.wicket.template.SkinOptions.Skin;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class TopMenu extends Panel {

    private boolean withSideBar;
    protected SkinOptions option;

    public TopMenu(String id, boolean withSideBar, SkinOptions option) {
        super(id);
        this.withSideBar = withSideBar;
        this.option = option;
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

        queue(buildSkinOptions());
    }

    protected ListView buildSkinOptions() {
        return new ListView<Skin>("skin_options", option.options()) {
            @Override
            protected void populateItem(ListItem item) {
                final Skin skin = (Skin) item.getModel().getObject();
                item.add(buildSelectSkinLink(skin));
                item.queue(new Label("label", skin.getName()));
            }
        };
    }

    private StatelessLink buildSelectSkinLink(final Skin skin) {
        return new StatelessLink("change_action") {
            public void onClick() {
                option.selectSkin(skin);
                refreshPage();
            }
        };
    }

    private void refreshPage() {
        setResponsePage(getPage());
    }
}
