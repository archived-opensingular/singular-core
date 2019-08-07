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

package org.opensingular.lib.wicket.util.template.admin;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public abstract class SingularAdminTemplate extends SingularTemplate {
    protected MarkupContainer pageBody;
    protected MarkupContainer pageHeader;
    protected MarkupContainer pageFooter;
    protected MarkupContainer pageMenu;
    protected MarkupContainer pageContent;
    private   List<String>    initializerJavascripts = Collections.singletonList("App.init();");

    public SingularAdminTemplate() {
        this(null);
    }

    public SingularAdminTemplate(PageParameters parameters) {
        super(parameters);
        addPageBody();
        addHeader();
        addPageContent();
        addPageContentTitle();
        addPageContentSubtitle();
        addPageHelpTextIcon();
        addFooter();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addPageMenu();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        for (String script : getInitializerJavascripts()) {
            response.render(OnDomReadyHeaderItem.forScript(script));
        }
        response.render(JavaScriptContentHeaderItem.forReference(new PackageResourceReference(
                SingularAdminTemplate.class, "SingularAdminTemplate.js")));
        response.render(JavaScriptContentHeaderItem.forReference(new PackageResourceReference(
                SingularAdminTemplate.class, "SingularAdminTemplate.js")));
    }

    private void addPageBody() {
        pageBody = getSingularAdminApp()
                .map(app -> app.buildPageBody("app-body", isWithMenu(), this))
                .orElseThrow(this::makeNotSingularAppError);
        add(pageBody);
    }

    private SingularException makeNotSingularAppError() {
        return SingularException.rethrow("A Applicacao não implementa SingularAdminApp");
    }

    private void addHeader() {
        pageHeader = getSingularAdminApp()
                .map(app -> app.buildPageHeader("app-header", isWithMenu(), this))
                .orElseThrow(this::makeNotSingularAppError);
        pageBody.add(getPageHeader());
    }

    private void addPageMenu() {
        pageMenu = buildPageMenu("menu");
        pageMenu.add($b.visibleIf(this::isWithMenu));
        pageBody.add(pageMenu);
    }

    private void addPageContent() {
        pageContent = new WebMarkupContainer("page-content");
        pageContent.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> set) {
                if (isWithMenu()) {
                    set.remove("zero-padding");
                } else {
                    set.add("zero-padding");
                }
                return set;
            }
        });
        pageBody.add(pageContent);
    }

    private void addPageContentTitle() {
        Label title = new Label("content-title", getContentTitle());
        title.add($b.visibleIfModelObject(StringUtils::isNotEmpty));
        pageContent.add(title);
    }

    private void addPageContentSubtitle() {
        Label subttile = new Label("content-subtitle", getContentSubtitle());
        subttile.add($b.visibleIfModelObject(StringUtils::isNotEmpty));
        pageContent.add(subttile);
    }

    private void addPageHelpTextIcon() {
        WebMarkupContainer iconMarkup = new WebMarkupContainer("help-icon") {
            @Override
            public boolean isVisible() {
                return StringUtils.isNotBlank(getHelpText().getObject());
            }
        };
        iconMarkup.add(WicketUtils.$b.classAppender(DefaultIcons.QUESTION_CIRCLE.getCssClass()));
        iconMarkup.add($b.visibleIfModelObject(StringUtils::isNotEmpty));
        pageContent.add(iconMarkup);

        iconMarkup.add(WicketUtils.$b.onReadyScript(
                component -> "var $helpIcon = " + JQuery.$(component) + ";"
                        + "$helpIcon"
                        + "  .data('content', '" + getHelpText().getObject() + "')"
                        + "  .popover({"
                        + "    'container':'body',"
                        + "    'html':true,"
                        + "    'placement':'auto right',"
                        + "    'trigger':'manual'"
                        + "  });"
                        + "$helpIcon"
                        + "  .hover("
                        + "    function(){ $helpIcon.popover('show'); },"
                        + "    function(){ $helpIcon.popover('hide'); });"
        ));
    }

    private void addFooter() {
        pageFooter = getSingularAdminApp()
                .map(app -> app.buildPageFooter("app-footer"))
                .orElseThrow(this::makeNotSingularAppError);
        pageBody.add(pageFooter);
    }

    private Optional<SingularAdminApp> getSingularAdminApp() {
        Application app = Application.get();
        if (app instanceof SingularAdminApp) {
            return Optional.of((SingularAdminApp) app);
        }
        return Optional.empty();
    }

    @Nonnull
    protected WebMarkupContainer buildPageMenu(String id) {
        return new WebMarkupContainer(id);
    }

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        Object payload = event.getPayload();
        if (payload instanceof AjaxRequestTarget) {
            AjaxRequestTarget target = (AjaxRequestTarget) payload;
            target.addListener(new AjaxRequestTarget.IListener() {
                @Override
                public void onBeforeRespond(Map<String, Component> map, AjaxRequestTarget target) {
                }

                @Override
                public void onAfterRespond(Map<String, Component> map, AjaxRequestTarget.IJavaScriptResponse response) {
                    if (!map.isEmpty()) {
                        getInitializerJavascripts().forEach(response::addJavaScript);
                    }
                }

                @Override
                public void updateAjaxAttributes(AbstractDefaultAjaxBehavior behavior, AjaxRequestAttributes attributes) {

                }
            });
        }
    }

    public MarkupContainer getPageHeader() {
        return pageHeader;
    }

    protected List<String> getInitializerJavascripts() {
        return initializerJavascripts;
    }

    protected abstract IModel<String> getContentTitle();

    protected abstract IModel<String> getContentSubtitle();

    public IModel<String> getHelpText() {
        return $m.ofValue("");
    }

    protected abstract boolean isWithMenu();
}