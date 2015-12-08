package br.net.mirante.singular.util.wicket.menu;

import br.net.mirante.singular.util.wicket.resource.Icone;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class MetronicMenuItem extends AbstractMenuItem {

    private WebMarkupContainer menuItem;
    private PageParameters parameters;
    private Class<? extends IRequestablePage> responsePageClass;
    private Optional<String> menuItemUrl;
    private String href;

    public MetronicMenuItem(Icone icon, String title, Class<? extends IRequestablePage> responsePageClass,
                            PageParameters parameters) {
        this(icon, title);
        this.responsePageClass = responsePageClass;
        this.parameters = parameters;
        add(buildMenuItem());
    }

    public MetronicMenuItem(Icone icon, String title, String href) {
        this(icon, title);
        this.href = href;
        add(buildMenuItem());
    }

    public MetronicMenuItem(Icone icon, String title) {
        super("menu-item");
        this.icon = icon;
        this.title = title;
    }

    private WebMarkupContainer buildMenuItem() {

        menuItem = new WebMarkupContainer("menu-item");

        MarkupContainer anchor = null;

        if (href != null) {
            anchor = new WebMarkupContainer("anchor");
            anchor.add($b.attr("href", href));
            this.menuItemUrl = Optional.ofNullable(href);
        } else if (responsePageClass != null) {
            anchor = new BookmarkablePageLink("anchor", responsePageClass, parameters) {
                {
                    menuItemUrl = Optional.ofNullable(getURL().toString());
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
        anchor.add(iconMarkup);

        menuItem.add(anchor);
        return menuItem;

    }

    @Override
    protected boolean configureActiveItem() {

        Pattern onlyLetters = Pattern.compile("[^a-z]");
        String url = onlyLetters.matcher(getRequest().getUrl().toString()).replaceAll("");
        String thisUrl = onlyLetters.matcher(menuItemUrl.orElse("")).replaceAll("");

        if (url.contains(thisUrl)) {
            menuItem.add($b.classAppender("active"));
            return true;
        }

        return false;
    }

}

