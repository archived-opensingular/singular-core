package org.opensingular.lib.wicket.util.menu;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.lib.wicket.util.resource.Icon;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public abstract class AjaxMenuItem extends AbstractMenuItem {
    private final MetronicMenu menu;

    private WebMarkupContainer menuItem;
    private AjaxLink<Void> anchor;
    private WebMarkupContainer helper;

    private boolean isActive = false;

    public AjaxMenuItem(Icon icon, String title, MetronicMenu menu) {
        super("menu-item");
        this.menu = menu;
        this.icon = icon;
        this.title = title;
        addMenuItem();
        addAjaxAnchor();
        addIcon();
        addTitle();
        addHelper();
    }

    private void addMenuItem() {
        menuItem = new WebMarkupContainer("menu-item");
        add(menuItem);
    }

    private void addAjaxAnchor() {
        anchor = new AjaxLink<Void>("anchor") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                inativeAll();
                isActive = true;
                onAjax(target);
            }
        };
        menuItem.add(anchor);
    }

    private void addIcon() {
        WebMarkupContainer iconContainer = new WebMarkupContainer("icon");
        if (icon != null) {
            iconContainer.add($b.classAppender(icon.getCssClass()));
        } else {
            iconContainer.setVisible(false);
        }
        anchor.add(iconContainer);
    }

    private void addTitle() {
        anchor.add(new Label("title", title));
    }

    private void addHelper() {
        helper = new WebMarkupContainer("helper");
        anchor.add(helper);
    }

    private void inativeAll() {
        menu.visitChildren(AjaxMenuItem.class, (IVisitor<AjaxMenuItem, Void>) (ajaxMenuItem, iVisit) -> ajaxMenuItem.isActive = false);
    }

    @Override
    protected boolean configureActiveItem() {
        return isActive;
    }

    protected abstract void onAjax(AjaxRequestTarget target);

}