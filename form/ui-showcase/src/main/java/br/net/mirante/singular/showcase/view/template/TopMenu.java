package br.net.mirante.singular.showcase.view.template;

import java.io.Serializable;
import java.util.Optional;

import br.net.mirante.singular.showcase.view.skin.SkinOptions;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.showcase.wicket.UIAdminSession;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class TopMenu extends Panel {

    private boolean withSideBar;
    private SkinOptions option;

    public TopMenu(String id, boolean withSideBar, SkinOptions option) {
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

        queue(buildDefaultSkinLink());
        queue(buildSkinOptions());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    private StatelessLink buildDefaultSkinLink() {
        return new StatelessLink("clear_skin") {
            public void onClick() {
                option.clearSelection();
                refreshPage();
            }
        };
    }

    private ListView buildSkinOptions() {
        return new ListView("skin_options", SkinOptions.options()) {
            @Override
            protected void populateItem(ListItem item) {
                final SkinOptions.Skin skin = (SkinOptions.Skin) item.getModel().getObject();
                item.add(buildSelectSkinLink(skin));
                item.queue(new Label("label", skin.name));
            }
        };
    }

    private StatelessLink buildSelectSkinLink(final SkinOptions.Skin skin) {
        return new StatelessLink("change_action") {
            public void onClick() {
                option.selectSkin( skin);
                refreshPage();
            }
        };
    }

    private void refreshPage() {    setResponsePage(getPage()); }
}
