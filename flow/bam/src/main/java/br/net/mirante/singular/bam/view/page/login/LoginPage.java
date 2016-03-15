/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.view.page.login;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

@StatelessComponent
@MountPath("login")
public class LoginPage extends WebPage{

    public LoginPage(PageParameters pageParameters) {
        super(pageParameters);
        setStatelessHint(true);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer loginError = new WebMarkupContainer("loginErrorC");
        loginError.setVisible(getPageParameters().get("error").toBoolean(false));
        loginError.add(new Label("loginError", getString("label.login.error")));
        add(loginError);
        add(new WebMarkupContainer("brandLogo").add($b.attr("src", "/singular-static/resources/singular/img/brand.png")));
        
        add(new WebMarkupContainer("username").add($b.attr("placeholder", getString("label.login.page.username"))));
        add(new WebMarkupContainer("password").add($b.attr("placeholder", getString("label.login.page.password"))));
        
        WebMarkupContainer ownerLink = new WebMarkupContainer("ownerLink");
        ownerLink.add(new AttributeModifier("href", new ResourceModel("footer.product.owner.addr")));
        ownerLink.add(new AttributeModifier("title", new ResourceModel("footer.product.owner.title")));
        add(ownerLink);
    }
}
