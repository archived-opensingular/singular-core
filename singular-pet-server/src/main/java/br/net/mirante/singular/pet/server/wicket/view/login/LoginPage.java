package br.net.mirante.singular.pet.server.wicket.view.login;

import br.net.mirante.singular.pet.module.wicket.PetApplication;
import br.net.mirante.singular.pet.server.spring.security.SecurityUtil;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.wicketstuff.annotation.mount.MountPath;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

@StatelessComponent
@MountPath("login")
public class LoginPage extends WebPage {

    public LoginPage(PageParameters pageParameters) {
        super(pageParameters);
        setStatelessHint(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(LoginPage.class, "LoginPage.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(LoginPage.class, "LoginPage.js")));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new WebMarkupContainer("brandLogo").add($b.attr("src", "/singular-static/resources/singular/img/brand.png")));

        WebMarkupContainer loginForm = new WebMarkupContainer("form");
        loginForm.add($b.attr("action", SecurityUtil.getLoginPath()));
        add(loginForm);

        WebMarkupContainer loginError = new WebMarkupContainer("loginErrorC");
        loginError.setVisible(getPageParameters().get("error").toBoolean(false));
        loginError.add(new Label("loginError", getString("label.login.error")));
        loginForm.add(loginError);

        loginForm.add(new WebMarkupContainer("username").add($b.attr("placeholder", getString("label.login.page.username"))));

        loginForm.add(new WebMarkupContainer("password").add($b.attr("placeholder", getString("label.login.page.password"))));

        WebMarkupContainer ownerLink = new WebMarkupContainer("ownerLink");
        ownerLink.add(new AttributeModifier("href", new ResourceModel("footer.product.owner.addr")));
        ownerLink.add(new AttributeModifier("title", new ResourceModel("footer.product.owner.title")));
        add(ownerLink);
    }


}
