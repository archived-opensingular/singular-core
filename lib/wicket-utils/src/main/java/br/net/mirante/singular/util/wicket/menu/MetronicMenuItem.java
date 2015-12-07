package br.net.mirante.singular.util.wicket.menu;

import br.net.mirante.singular.util.wicket.resource.Icone;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class MetronicMenuItem extends AbstractMenuItem {

    private WebMarkupContainer menuItem = new WebMarkupContainer("menu-item");
    private PageParameters parameters;
    private Class<? extends IRequestablePage> responsePageClass;

    public MetronicMenuItem(String title, Class<? extends IRequestablePage> responsePageClass) {
        this(null, title, responsePageClass, null);
    }

    public MetronicMenuItem(String title, Class<? extends IRequestablePage> responsePageClass,
                            PageParameters parameters) {
        this(null, title, responsePageClass, parameters);
    }

    public MetronicMenuItem(Icone icon, String title, Class<? extends IRequestablePage> responsePageClass) {
        this(icon, title, responsePageClass, null);
    }

    public MetronicMenuItem(Icone icon, String title, Class<? extends IRequestablePage> responsePageClass,
                            PageParameters parameters) {
        super("menu-item");
        this.icon = icon;
        this.title = title;
        this.responsePageClass = responsePageClass;
        this.parameters = parameters;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Link anchor = new Link("anchor") {
            @Override
            public void onClick() {
                getWebSession().setAttribute(ATTR_ACTIVE_ITEM, itemId);
                setResponsePage(responsePageClass, parameters);
            }
        };

        WebMarkupContainer iconMarkup = new WebMarkupContainer("icon");

        if (icon != null) {
            iconMarkup.add($b.classAppender(icon.getCssClass()));
        } else {
            iconMarkup.setVisible(false);
        }

        anchor.add(new Label("title", title));
        anchor.add(iconMarkup);

        menuItem.add(anchor);
        add(menuItem);
    }

    @Override
    protected void configureActiveItem(String activeItemId) {
        if (getItemId().equals(activeItemId)) {
            menuItem.add($b.classAppender("active"));
        }
    }

    public WebMarkupContainer getMenuItem() {
        return menuItem;
    }

    public MetronicMenuItem setMenuItem(WebMarkupContainer menuItem) {
        this.menuItem = menuItem;
        return this;
    }

}

