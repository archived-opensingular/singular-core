/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.template;

import com.opensingular.bam.service.UIAdminFacade;
import com.opensingular.bam.wicket.UIAdminSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.UrlUtils;

import javax.inject.Inject;
import java.util.Optional;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class TopMenu extends Panel {

    @Inject
    private UIAdminFacade uiAdminFacade;

    private boolean withSideBar;

    public TopMenu(String id, boolean withSideBar) {
        super(id);
        this.withSideBar = withSideBar;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(new WebMarkupContainer("sideBarToggle").setVisible(withSideBar));
        queue(new Label("nome", $m.ofValue(UIAdminSession.get().getUser().getSimpleName())));

        WebMarkupContainer avatar = new WebMarkupContainer("codrh");
        avatar.add($b.attr("src", Optional.ofNullable(StringUtils.trimToNull(uiAdminFacade.getUserAvatar()))
                .orElse(UrlUtils.rewriteToContextRelative("/singular-static/resources/singular/layout4/img/avatar.png", getRequestCycle())).replace("{0}", UIAdminSession.get().getUserId())));
        queue(avatar);

        WebMarkupContainer logout     = new WebMarkupContainer("logout");
        Optional<String>   logoutHref = Optional.ofNullable(StringUtils.trimToNull(uiAdminFacade.getLogoutUrl()));
        logoutHref.ifPresent(href -> logout.add($b.attr("href", WebApplication.get().getServletContext().getContextPath()+href)));
        queue(logout);
    }
}
