package br.net.mirante.singular.util.wicket.menu;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class MetronicMenuItem extends AbstractMenuItem {

    private WebMarkupContainer menuItem = new WebMarkupContainer("menu-item");
    private IRequestablePage responsePageObject;
    private PageParameters parameters;
    private Class<? extends IRequestablePage> responsePageClass;
    private String href;

    public MetronicMenuItem(String title) {
        this(null, title);
    }

    public MetronicMenuItem(String icon, String title) {
        super("menu-item");
        this.icon = icon;
        this.title = title;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Link anchor = new Link("anchor") {
            @Override
            public void onClick() {
                getWebSession().setAttribute(ATTR_ACTIVE_ITEM, itemId);
                if (responsePageObject != null) {
                    setResponsePage(responsePageObject);
                } else if (responsePageClass != null) {
                    setResponsePage(responsePageClass, parameters);
                } else if (href != null) {
                    setHref(href);
                }
            }
        };

        WebMarkupContainer iconMarkup = new WebMarkupContainer("icon");

        if (icon != null) {
            iconMarkup.add($b.classAppender(icon));
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

    public IRequestablePage getResponsePageObject() {
        return responsePageObject;
    }

    public MetronicMenuItem setResponsePageObject(IRequestablePage responsePageObject) {
        this.responsePageObject = responsePageObject;
        return this;
    }

    public PageParameters getParameters() {
        return parameters;
    }

    public MetronicMenuItem setParameters(PageParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    public Class<? extends IRequestablePage> getResponsePageClass() {
        return responsePageClass;
    }

    public MetronicMenuItem setResponsePageClass(Class<? extends IRequestablePage> responsePageClass) {
        this.responsePageClass = responsePageClass;
        return this;
    }

    public String getHref() {
        return href;
    }

    public MetronicMenuItem setHref(String href) {
        this.href = href;
        return this;
    }
}

