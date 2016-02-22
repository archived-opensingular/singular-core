package br.net.mirante.singular.pet.module.wicket.view.template;

import br.net.mirante.singular.pet.module.spring.security.SecurityUtil;
import br.net.mirante.singular.pet.module.wicket.PetSession;
import br.net.mirante.singular.pet.module.wicket.view.skin.SkinOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Optional;

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

        queue(buildDefaultSkinLink());
        queue(buildSkinOptions());
    }

    protected StatelessLink buildDefaultSkinLink() {
        return new StatelessLink("clear_skin") {
            public void onClick() {
                option.clearSelection();
                refreshPage();
            }
        };
    }

    protected ListView buildSkinOptions() {
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
