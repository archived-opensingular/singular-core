package br.net.mirante.singular.pet.module.wicket.view.template;

import br.net.mirante.singular.pet.module.wicket.view.skin.SkinOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class Header extends Panel {

    private boolean withTogglerButton;
    private boolean withSideBar;
    private SkinOptions option;

    public Header(String id) {
        super(id);
        this.withTogglerButton = true;
        this.withSideBar = false;
    }

    public Header(String id, boolean withTogglerButton, boolean withTopAction, boolean withSideBar, SkinOptions option) {
        super(id);
        this.withTogglerButton = withTogglerButton;
        this.withSideBar = withSideBar;
        this.option = option;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new WebMarkupContainer("togglerButton")
                .add($b.attrAppender("class", "hide", " ", $m.ofValue(!withTogglerButton))));
        add(new WebMarkupContainer("_TopAction"));
        add(configureTopMenu("_TopMenu"));
        add(new WebMarkupContainer("brandLogo"));
    }

    protected TopMenu configureTopMenu(String id) {
        return new TopMenu(id, withSideBar, option);
    }
}
