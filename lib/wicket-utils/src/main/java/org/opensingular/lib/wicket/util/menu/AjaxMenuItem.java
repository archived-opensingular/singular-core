package org.opensingular.lib.wicket.util.menu;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.lib.wicket.util.resource.Icon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public abstract class AjaxMenuItem extends AbstractMenuItem {
    private final String ACTIVE = "active";

    private WebMarkupContainer menuItem;
    private AjaxLink<Void> anchor;
    private WebMarkupContainer helper;

    private boolean isActive = false;

    public AjaxMenuItem(Icon icon, String title) {
        super("menu-item");
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
                inativeAllButThis();
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

    private void inativeAllButThis() {
        List<AjaxMenuItem> ajaxMenuItems = new ArrayList<>();
        visitParents(MetronicMenu.class, (IVisitor<MetronicMenu, AjaxMenuItem>) (mm, v) -> {
            mm.visitChildren(AjaxMenuItem.class, (IVisitor<AjaxMenuItem, AjaxMenuItem>) (ajaxMenu, vv) -> {
                if (!ajaxMenu.equals(AjaxMenuItem.this)) {
                    ajaxMenu.isActive = false;
                    ajaxMenu.menuItem.add(new ClassAttributeModifier() {
                        @Override
                        protected Set<String> update(Set<String> oldClasses) {
                            oldClasses.remove(ACTIVE);
                            return oldClasses;
                        }
                    });
                }
            });
            v.stop();
        });
    }

    @Override
    protected boolean configureActiveItem() {
        if (isActive) {
            menuItem.add($b.classAppender(ACTIVE));
            return true;
        }
        return false;
    }

    protected abstract void onAjax(AjaxRequestTarget target);

}