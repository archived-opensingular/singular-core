package org.opensingular.lib.wicket.util.template.admin;

import org.apache.wicket.Application;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.wicket.util.template.SingularTemplate;

import java.util.Optional;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public abstract class SingularAdminTemplate extends SingularTemplate {
    private MarkupContainer pageBody;
    private MarkupContainer pageHeader;
    private MarkupContainer pageFooter;
    private MarkupContainer pageContent;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addPageBody();
        addHeader();
        addPageContent();
        addPageContentTitle();
        addPageContentSubtitle();
        addFooter();
    }

    private void addPageBody() {
        pageBody = getSingularAdminApp()
                .map(app -> app.buildPageBody("app-body"))
                .orElseThrow(this::makeNotSingularAppError);
        add(pageBody);
    }

    private SingularException makeNotSingularAppError() {
        return SingularException.rethrow("A Applicacao não implementa SingularAdminApp");
    }

    private void addHeader() {
        pageHeader = getSingularAdminApp()
                .map(app -> app.buildPageHeader("app-header"))
                .orElseThrow(this::makeNotSingularAppError);
        pageBody.add(pageHeader);
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
        Label subttile = new Label("content-subtitle", $m.get(this::getContentSubtitle));
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

    protected abstract String getContentTitle();
    protected abstract String getContentSubtitle();
}