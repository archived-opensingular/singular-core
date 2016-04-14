package br.net.mirante.singular.pet.module.wicket.view.util.tab;

import br.net.mirante.singular.commons.lambda.IConsumer;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

@MountPath("newtab")
public class NewTabProcessingPage extends WebPage {

    private static final Logger logger = LoggerFactory.getLogger(NewTabProcessingPage.class);
    private final Component opener;
    private final IConsumer<NewTabProcessingHelper> action;

    private final WebMarkupContainer bodyContainer = new WebMarkupContainer("body");

    public NewTabProcessingPage(Component opener, IConsumer<NewTabProcessingHelper> action) {
        this.action = action;
        this.add(bodyContainer);
        bodyContainer.add(new HeaderResponseContainer("scripts", "scripts"));
        this.opener = opener;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(NewTabProcessingPage.class, "NewTabProcessingPage.js")));
    }

    public void closeThisAndReloadOpener() {
        bodyContainer.add($b.onReadyScript(component -> "NewTabProcessingPage.closeThisAndReloadOpener();"));
    }

    public void redirectTo(String url) {
        bodyContainer.add($b.onReadyScript(component -> String.format("NewTabProcessingPage.redirectTo('%s');", url)));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        try {
            action.accept(new NewTabProcessingHelper());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            closeThisAndReloadOpener();
        }
    }

    public class NewTabProcessingHelper {

        public NewTabProcessingPage getProcessingPage() {
            return NewTabProcessingPage.this;
        }

        public void closeThisAndReloadOpener() {
            NewTabProcessingPage.this.closeThisAndReloadOpener();
        }

        public void redirectToAndReloadOpener(String url){
            NewTabProcessingPage.this.redirectToAndReloadOpener(url);
        }

        public Component getOpener() {
            return NewTabProcessingPage.this.opener;
        }

        public <C extends IRequestablePage> void setResponsePage(final Class<C> cls) {
            NewTabProcessingPage.this.setResponsePage(cls);
        }

        public <C extends IRequestablePage> void setResponsePage(final Class<C> cls, PageParameters parameters) {
            NewTabProcessingPage.this.setResponsePage(cls, parameters);
        }

        public void setResponsePage(final IRequestablePage page) {
            NewTabProcessingPage.this.setResponsePage(page);
        }

        public void redirectTo(String url) {
            NewTabProcessingPage.this.redirectTo(url);
        }

    }

    private void redirectToAndReloadOpener(String url) {
        bodyContainer.add($b.onReadyScript(component -> String.format("NewTabProcessingPage.redirectToAndReloadOpener('%s');", url)));
    }
}
