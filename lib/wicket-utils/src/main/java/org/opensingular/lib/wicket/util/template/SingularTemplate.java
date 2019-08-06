/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.template;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.opensingular.lib.wicket.util.application.SkinnableApplication;
import org.opensingular.lib.wicket.util.behavior.KeepSessionAliveBehavior;
import org.opensingular.lib.wicket.util.model.SingularPropertyModel;

public abstract class SingularTemplate extends WebPage {

    public static final String                   JAVASCRIPT_CONTAINER = "javascript-container";
    public static final IHeaderResponseDecorator JAVASCRIPT_DECORATOR = (response) -> new JavaScriptFilteredIntoFooterHeaderResponse(response, SingularTemplate.JAVASCRIPT_CONTAINER);

    protected String skinnableResource(String uri) {
        return "/singular-static/resources/" + getCurrentSkinFolder() + uri;
    }

    public final SkinOptions skinOptions = new SkinOptions();

    public SingularTemplate() {
        initSkins();
    }

    public SingularTemplate(PageParameters parameters) {
        super(parameters);
        initSkins();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        getApplication().setHeaderResponseDecorator(JAVASCRIPT_DECORATOR);

        /*Essa estratégia é utilizada para garantir que o jquery será sempre carregado pois está fixo no html
         * sem esse artificio páginas sem componentes ajax do wicket apresentarão erros de javascript.*/
        getApplication()
                .getJavaScriptLibrarySettings()
                .setJQueryReference(new PackageResourceReference(SingularTemplate.class, "empty.js"));
        add(createPageTitle());
        add(createPageSubtitle());
        add(new HeaderResponseContainer(JAVASCRIPT_CONTAINER, JAVASCRIPT_CONTAINER));
        add(new KeepSessionAliveBehavior());
        add(createFavicon());
    }

    private Label createPageTitle() {
        return new Label("title", createPageTitleModel());
    }

    private Label createPageSubtitle() {
        return new Label("subtitle", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                String val = getPageTitleModel().getObject();
                if (val != null) {
                    return " | " + val;
                }
                return StringUtils.EMPTY;
            }
        });
    }

    private Component createFavicon() {
        final TransparentWebMarkupContainer favicon    = new TransparentWebMarkupContainer("favicon");
        final IModel<String>                faviconUrl = createFaviconUrlModel();
        favicon.add(AttributeAppender.replace("href", faviconUrl));
        return favicon;
    }

    /**
     * Override this to include new rules to fetch de favion url
     */
    protected IModel<String> createFaviconUrlModel() {
        return new SingularPropertyModel("singular.template.favicon",
                "/singular-static/resources/singular/img/favicon.png");
    }

    /**
     * Override this to include new rules to fetch de page title
     */
    protected IModel<String> createPageTitleModel() {
        return new SingularPropertyModel("singular.application.name",
                getString("label.page.title.global", null, StringUtils.EMPTY));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        RecursosStaticosSingularTemplate.getStyles(getCurrentSkinFolder()).forEach(response::render);
        RecursosStaticosSingularTemplate.getJavaScriptsUrls().forEach(response::render);
    }

    protected IModel<String> getPageTitleModel() {
        return new StringResourceModel("label.page.title.local").setDefaultValue("");
    }

    protected void initSkins() {
        final WebApplication wa = WebApplication.get();
        if (wa instanceof SkinnableApplication) {
            ((SkinnableApplication) wa).initSkins(skinOptions);
        } else {
            skinOptions.addDefaulSkin("singular");
        }
    }

    public String getCurrentSkinFolder() {
        return skinOptions.currentSkin().getName();
    }

    public SkinOptions getSkinOptions() {
        return skinOptions;
    }
}