package br.net.mirante.singular.pet.module.wicket.view.util.dispatcher;

import java.lang.reflect.Constructor;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskUserExecutable;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.pet.commons.exception.SingularServerException;
import br.net.mirante.singular.pet.commons.flow.PetServerTaskPageStrategy;
import br.net.mirante.singular.pet.commons.flow.SingularWebRef;
import br.net.mirante.singular.pet.commons.wicket.view.SingularHeaderResponseDecorator;
import br.net.mirante.singular.pet.commons.wicket.view.behavior.SingularJSBehavior;
import br.net.mirante.singular.pet.commons.wicket.view.form.AbstractFormPage;
import br.net.mirante.singular.pet.commons.wicket.view.template.Template;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

@SuppressWarnings("serial")
public abstract class AbstractDispatcherPage extends WebPage {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractDispatcherPage.class);

    private final WebMarkupContainer bodyContainer = new WebMarkupContainer("body");

    public AbstractDispatcherPage() {
        this.add(bodyContainer);
        getApplication().setHeaderResponseDecorator(new SingularHeaderResponseDecorator());
        bodyContainer.add(new HeaderResponseContainer("scripts", "scripts"));
        add(new SingularJSBehavior());
        AbstractFormPage.FormPageConfig config = parseParameters(getRequest());
        if (config != null) {
            dispatch(config);
        } else {
            closeAndReloadParent();
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(new PackageResourceReference(Template.class, "singular.js")));
    }

    protected abstract AbstractFormPage.FormPageConfig parseParameters(Request request);

    protected void dispatch(AbstractFormPage.FormPageConfig config) {
        try {
            WebPage destination = null;
            SingularWebRef ref = null;
            TaskInstance ti = loadCurrentTaskByFormId(config.formId);
            if (ti != null) {
                MTask task = ti.getFlowTask();
                if (task instanceof MTaskUserExecutable) {
                    ITaskPageStrategy pageStrategy = ((MTaskUserExecutable) task).getExecutionPage();
                    if (pageStrategy instanceof PetServerTaskPageStrategy) {
                        ref = (SingularWebRef) pageStrategy.getPageFor(ti, null);
                    } else {
                        logger.warn("Atividade atual possui uma estratégia de página não suportada. A página default será utilizada.");
                    }
                } else if (!ViewMode.VISUALIZATION.equals(config.viewMode)) {
                    throw new SingularServerException("Página invocada para uma atividade que não é do tipo MTaskUserExecutable");
                }
            }
            if (ref == null || ref.getPageClass() == null) {
                Constructor c = getDefaultFormPageClass().getConstructor(AbstractFormPage.FormPageConfig.class);
                destination = (WebPage) c.newInstance(config);
            } else if (AbstractFormPage.class.isAssignableFrom(ref.getPageClass())) {
                Constructor c = ref.getPageClass().getConstructor(AbstractFormPage.FormPageConfig.class);
                destination = (WebPage) c.newInstance(config);
            } else {
                destination = ref.getPageClass().newInstance();
            }
            configureReload(destination);
            setResponsePage(destination);
        } catch (Exception e) {
            closeAndReloadParent();
            logger.error(e.getMessage(), e);

        }
    }

    protected void configureReload(WebPage destination) {
        destination.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                response.render(JavaScriptReferenceHeaderItem.forReference(new PackageResourceReference(Template.class, "singular.js")));
            }
        });
        destination.add($b.onReadyScript(() -> " Singular.atualizarContentWorklist(); "));
    }

    private void closeAndReloadParent() {
        add($b.onReadyScript(() ->
                " Singular.atualizarContentWorklist(); " +
                        " window.close(); "));
    }

    protected abstract TaskInstance loadCurrentTaskByFormId(String formID);

    protected abstract Class<? extends AbstractFormPage> getDefaultFormPageClass();

}
