/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.wicket;

import java.util.Locale;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.lang.Bytes;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import br.net.mirante.singular.showcase.view.page.form.ListPage;
import br.net.mirante.singular.util.wicket.page.error.Error403Page;
import br.net.mirante.singular.util.wicket.template.SingularTemplate;

public class ShowcaseApplication extends AuthenticatedWebApplication
        implements ApplicationContextAware {

    public static final String BASE_FOLDER = "/tmp/fileUploader";

    private ApplicationContext ctx;

    public static ShowcaseApplication get() {
        return (ShowcaseApplication) WebApplication.get();
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return ListPage.class;
    }

    @Override
    public void init() {
        super.init();

        Locale.setDefault(new Locale("pt", "BR"));

        getApplicationSettings().setAccessDeniedPage(Error403Page.class);

        getApplicationSettings().setDefaultMaximumUploadSize(Bytes.MAX);

        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getComponentInitializationListeners().add(c -> {
            if (!c.getRenderBodyOnly())
                c.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        });

        if (ctx != null) {
            getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx, true));
        } else {
            getComponentInstantiationListeners().add(new SpringComponentInjector(this));
            ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        }
        new AnnotatedMountScanner().scanPackage("br.net.mirante.singular.showcase.view.page.**").mount(this);

        setHeaderResponseDecorator(r -> new JavaScriptFilteredIntoFooterHeaderResponse(r, SingularTemplate.JAVASCRIPT_CONTAINER));
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new UIAdminSession(request, response);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return UIAdminSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return ListPage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if ("false".equals(System.getProperty("singular.development"))) {
            return RuntimeConfigurationType.DEPLOYMENT;
        } else {
            return RuntimeConfigurationType.DEVELOPMENT;
        }
    }

    public ApplicationContext getApplicationContext() {
        return ctx;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

}
