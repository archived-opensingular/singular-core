package br.net.mirante.singular.util.wicket.menu;

import java.util.regex.Pattern;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.net.mirante.singular.util.wicket.resource.Icone;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class MetronicMenuItem extends AbstractMenuItem {

    private WebMarkupContainer menuItem;
    private PageParameters parameters;
    private Class<? extends IRequestablePage> responsePageClass;
    private String menuItemUrl;
    private String href;
    private String target;
    private WebMarkupContainer helper = new WebMarkupContainer("helper");

    public MetronicMenuItem(Icone icon, String title, Class<? extends IRequestablePage> responsePageClass,
                            PageParameters parameters) {
        this(icon, title);
        this.responsePageClass = responsePageClass;
        this.parameters = parameters;
        add(buildMenuItem());
    }

    public MetronicMenuItem(Icone icon, String title, Class<? extends IRequestablePage> responsePageClass) {
        this(icon, title, responsePageClass, null);
    }

    public MetronicMenuItem(Icone icon, String title, String href) {
        this(icon, title);
        this.href = href;
        add(buildMenuItem());
    }

    public MetronicMenuItem(Icone icon, String title, String href, String target) {
        this(icon, title);
        this.href = href;
        this.target = target;
        add(buildMenuItem());
    }

    public MetronicMenuItem(Icone icon, String title) {
        super("menu-item");
        this.icon = icon;
        this.title = title;
    }

    protected WebMarkupContainer buildMenuItem() {

        menuItem = new WebMarkupContainer("menu-item");

        MarkupContainer anchor = null;

        if (href != null) {
            anchor = new WebMarkupContainer("anchor");
            anchor.add($b.attr("href", href));
            if (target != null) {
                anchor.add($b.attr("target", target));
            }
            this.menuItemUrl = href;
        } else if (responsePageClass != null) {
            anchor = new BookmarkablePageLink("anchor", responsePageClass, parameters) {
                {
                    menuItemUrl = getURL().toString();
                }
            };
        } else {
            throw new WicketRuntimeException("É necessario informar o destino do item");
        }

        WebMarkupContainer iconMarkup = new WebMarkupContainer("icon");

        if (icon != null) {
            iconMarkup.add($b.classAppender(icon.getCssClass()));
        } else {
            iconMarkup.setVisible(false);
        }

        anchor.add(new Label("title", title));
        anchor.add(helper);
        anchor.add(iconMarkup);

        menuItem.add(anchor);
        return menuItem;

    }

    @Override
    protected boolean configureActiveItem() {

        if (menuItemUrl != null) {
            Pattern onlyLetters = Pattern.compile("[^a-z]");
            String url = onlyLetters.matcher(getRequest().getUrl().toString()).replaceAll("");
            String thisUrl = onlyLetters.matcher(menuItemUrl).replaceAll("");

            if (url.endsWith(thisUrl)) {
                menuItem.add($b.classAppender("active"));
                return true;
            }
        }

        return false;
    }

    public WebMarkupContainer getHelper() {
        return helper;
    }
}

