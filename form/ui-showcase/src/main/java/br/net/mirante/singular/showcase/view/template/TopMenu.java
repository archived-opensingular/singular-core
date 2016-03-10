package br.net.mirante.singular.showcase.view.template;

import java.util.Optional;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.showcase.wicket.UIAdminSession;
import br.net.mirante.singular.util.wicket.template.SkinOptions.Skin;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class TopMenu extends Panel {

    private boolean withSideBar;
    private br.net.mirante.singular.util.wicket.template.SkinOptions option;

    public TopMenu(String id, boolean withSideBar, br.net.mirante.singular.util.wicket.template.SkinOptions option) {
        super(id);
        this.withSideBar = withSideBar;
        this.option = option;
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

        queue(buildSkinOptions());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    private ListView buildSkinOptions() {
        return new ListView<Skin>("skin_options", option.options()) {
            @Override
            protected void populateItem(ListItem<Skin> item) {
                final Skin skin = item.getModel().getObject();
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
