/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.view.template;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import javax.inject.Inject;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.studio.wicket.StudioWicketFilterContext;
import br.net.mirante.singular.util.wicket.toastr.ToastrHelper;
import de.alpharogroup.wicket.js.addon.toastr.ToastrType;


public abstract class Content extends Panel {

    @Inject
    private StudioWicketFilterContext studioWicketFilterContext;

    private boolean withBreadcrumb;
    private boolean withInfoLink;

    public Content(String id) {
        this(id, false, false);
    }

    public Content(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id);
        this.withBreadcrumb = withBreadcrumb;
        this.withInfoLink = withInfoLink;
    }

    public Content addSideBar() {
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("contentTitle", getContentTitleModel()));
        add(new Label("contentSubtitle", getContentSubtitleModel()));
        WebMarkupContainer breadcrumb = new WebMarkupContainer("breadcrumb");
        add(breadcrumb);
        breadcrumb.add(new WebMarkupContainer("breadcrumbDashboard").add(
                $b.attr("href", studioWicketFilterContext.getRelativeContext())));
        breadcrumb.add(getBreadcrumbLinks("_BreadcrumbLinks"));
        if (!withBreadcrumb) {
            breadcrumb.add(new AttributeAppender("class", "hide", " "));
        }
        WebMarkupContainer infoLink = new WebMarkupContainer("_Info");
        add(infoLink.setVisible(withInfoLink));
        if (withInfoLink) {
            infoLink.add(getInfoLink("_InfoLink"));
        }
    }

    public void addToastrSuccessMessage(String messageKey, String... args) {
        new ToastrHelper(this).
                addToastrMessage(ToastrType.SUCCESS, messageKey, args);
    }

    public void addToastrErrorMessage(String messageKey, String... args) {
        new ToastrHelper(this).
                addToastrMessage(ToastrType.ERROR, messageKey, args);
    }

    public void addToastrWarningMessage(String messageKey, String... args) {
        new ToastrHelper(this).
                addToastrMessage(ToastrType.WARNING, messageKey, args);
    }

    public void addToastrInfoMessage(String messageKey, String... args) {
        new ToastrHelper(this).
                addToastrMessage(ToastrType.INFO, messageKey, args);
    }

    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return new WebMarkupContainer(id);
    }

    protected WebMarkupContainer getInfoLink(String id) {
        return new WebMarkupContainer(id);
    }

    protected abstract IModel<?> getContentTitleModel();

    protected abstract IModel<?> getContentSubtitleModel();
}
