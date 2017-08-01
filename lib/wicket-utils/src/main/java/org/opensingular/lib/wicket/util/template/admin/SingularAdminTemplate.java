package org.opensingular.lib.wicket.util.template.admin;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.wicket.util.template.SingularTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public abstract class SingularAdminTemplate extends SingularTemplate {
    private List<String> initializerJavascripts = Collections.singletonList("App.init();");

    private MarkupContainer pageBody;
    private MarkupContainer pageHeader;
    private MarkupContainer pageFooter;
    private MarkupContainer pageMenu;
    private MarkupContainer pageContent;

    public SingularAdminTemplate() {
    }

    public SingularAdminTemplate(IModel<?> model) {
        super(model);
    }

    public SingularAdminTemplate(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        for (String script : initializerJavascripts) {
            response.render(OnDomReadyHeaderItem.forScript(script));
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addPageBody();
        addHeader();
        addPageMenu();
        addPageContent();
        addPageContentTitle();
        addPageContentSubtitle();
        addFooter();
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
        pageBody.add(pageHeader);
    }

    private void addPageMenu() {
        pageMenu = buildPageMenu("menu");
        pageMenu.add($b.visibleIf(this::isWithMenu));
        pageBody.add(pageMenu);
    }

    private void addPageContent() {
        pageContent = new WebMarkupContainer("page-content");
        pageBody.add(pageContent);
    }

    private void addPageContentTitle() {
        Label title = new Label("content-title", $m.get(this::getContentTitle));
        pageContent.add(title);
    }

    private void addPageContentSubtitle() {
        Label subttile = new Label("content-subtitle",  $m.get(this::getContentSubtitle));
        pageContent.add(subttile);
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

    @NotNull
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
                        initializerJavascripts.forEach(response::addJavaScript);
                    }
                }

                @Override
                public void updateAjaxAttributes(AbstractDefaultAjaxBehavior behavior, AjaxRequestAttributes attributes) {

                }
            });
        }
    }

    protected abstract String getContentTitle();

    protected abstract String getContentSubtitle();

    protected abstract boolean isWithMenu();
}