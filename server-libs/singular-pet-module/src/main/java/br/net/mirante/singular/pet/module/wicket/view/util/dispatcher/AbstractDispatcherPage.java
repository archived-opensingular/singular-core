package br.net.mirante.singular.pet.module.wicket.view.util.dispatcher;

import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskUserExecutable;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.pet.module.exception.SingularServerException;
import br.net.mirante.singular.pet.module.flow.PetServerTaskPageStrategy;
import br.net.mirante.singular.pet.module.flow.SingularWebRef;
import br.net.mirante.singular.pet.module.wicket.view.form.AbstractFormPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public abstract class AbstractDispatcherPage extends WebPage {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractDispatcherPage.class);

    public AbstractDispatcherPage() {
        AbstractFormPage.FormPageConfig config = parseParameters(getRequest());
        dispatch(config);
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
                        if (pageStrategy != null) {
                            ref = (SingularWebRef) pageStrategy.getPageFor(ti, null);
                        }
                    } else {
                        logger.warn("Atividade atual possui uma estratégia de página não suportada. A página default será utilizada.");
                    }
                } else {
                    throw new SingularServerException("Página invocada para uma atividade que não é do tipo MTaskUserExecutable");
                }
            }
            if (ref == null || ref.getPageClass() == null) {
                Constructor c = getDefaultFormPageClass().getConstructor(AbstractFormPage.FormPageConfig.class);
                destination = (WebPage) c.newInstance(config);
            } else if (ref.getPageClass().isAssignableFrom(AbstractFormPage.class)) {
                Constructor c = ref.getPageClass().getConstructor(AbstractFormPage.FormPageConfig.class);
                destination = (WebPage) c.newInstance(config);
            } else {
                destination = ref.getPageClass().newInstance();

            }
            setResponsePage(destination);
        } catch (Exception e) {
            closeAndReloadParent();
            logger.error(e.getMessage(), e);

        }
    }

    private void closeAndReloadParent() {
        //TODO
    }

    protected abstract TaskInstance loadCurrentTaskByFormId(String formID);

    protected abstract Class<? extends AbstractFormPage> getDefaultFormPageClass();

}
