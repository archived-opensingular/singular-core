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
import org.apache.wicket.markup.head.CssHeaderItem;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.lib.wicket.SingularWebResourcesFactory;
import org.opensingular.lib.wicket.util.behavior.KeepSessionAliveBehavior;
import org.opensingular.lib.wicket.util.model.SingularPropertyModel;

import javax.inject.Inject;
import java.util.Collections;

public abstract class SingularTemplate extends WebPage {
    public static final String                   JAVASCRIPT_CONTAINER = "javascript-container";
    public static final IHeaderResponseDecorator JAVASCRIPT_DECORATOR = (response) -> new JavaScriptFilteredIntoFooterHeaderResponse(response, SingularTemplate.JAVASCRIPT_CONTAINER);

    @Inject
    private SingularWebResourcesFactory singularWebResourcesFactory;

    public SingularTemplate() {
    }

    public SingularTemplate(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
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
                if (StringUtils.isNotEmpty(val)) {
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
        return new SingularPropertyModel("singular.template.favicon", urlFor(new SharedResourceReference("favicon"), null).toString());
    }

    /**
     * Override this to include new rules to fetch de page title
     */
    protected IModel<String> createPageTitleModel() {
        return new SingularPropertyModel("singular.application.name", getString("label.page.title.global"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        singularWebResourcesFactory.getStyleHeaders().forEach(response::render);
        singularWebResourcesFactory.getScriptHeaders().forEach(response::render);
        final PackageTextTemplate singularTemplateCssTemplate =
                new PackageTextTemplate(SingularTemplate.class, "SingularTemplate.css");
        final String singularTemplateCss = singularTemplateCssTemplate.interpolate(
                Collections.singletonMap("logo", getRequestCycle().urlFor(new SharedResourceReference("logo"), null))).getString();
        response.render(CssHeaderItem.forCSS(singularTemplateCss, null));
    }

    protected IModel<String> getPageTitleModel() {
        return new StringResourceModel("label.page.title.local").setDefaultValue("");
    }
}