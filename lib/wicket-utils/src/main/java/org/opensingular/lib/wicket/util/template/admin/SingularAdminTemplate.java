package org.opensingular.lib.wicket.util.template.admin;

import org.apache.wicket.Application;
import org.apache.wicket.ClassAttributeModifier;
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
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.wicket.util.template.SingularTemplate;

import javax.annotation.Nonnull;
import java.util.*;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

public abstract class SingularAdminTemplate extends SingularTemplate {
    protected MarkupContainer pageBody;
    protected MarkupContainer pageHeader;
    protected MarkupContainer pageFooter;
    protected MarkupContainer pageMenu;
    protected MarkupContainer pageContent;
    private List<String> initializerJavascripts = Collections.singletonList("App.init();");

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
        for (String script : initializerJavascripts) {
            response.render(OnDomReadyHeaderItem.forScript(script));
        }
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
        pageContent.add(title);
    }

    private void addPageContentSubtitle() {
        Label subttile = new Label("content-subtitle", getContentSubtitle());
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
                        initializerJavascripts.forEach(response::addJavaScript);
                    }
                }

                @Override
                public void updateAjaxAttributes(AbstractDefaultAjaxBehavior behavior, AjaxRequestAttributes attributes) {

                }
            });
        }
    }

    protected abstract IModel<String> getContentTitle();

    protected abstract IModel<String> getContentSubtitle();

    protected abstract boolean isWithMenu();
}