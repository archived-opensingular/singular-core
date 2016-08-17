package br.net.mirante.singular.server.module.wicket.view.util.dispatcher;

import br.net.mirante.singular.flow.core.*;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.flow.SingularServerTaskPageStrategy;
import br.net.mirante.singular.server.commons.flow.SingularWebRef;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.wicket.view.SingularHeaderResponseDecorator;
import br.net.mirante.singular.server.commons.wicket.view.behavior.SingularJSBehavior;
import br.net.mirante.singular.server.commons.wicket.view.form.AbstractFormPage;
import br.net.mirante.singular.server.commons.wicket.view.form.FormPageConfig;
import br.net.mirante.singular.server.commons.wicket.view.template.Template;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.server.module.wicket.view.util.form.FormPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.lang.reflect.Constructor;

import static br.net.mirante.singular.server.commons.util.Parameters.*;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

@SuppressWarnings("serial")
@MountPath(DispatcherPageUtil.DISPATCHER_PAGE_PATH)
public abstract class DispatcherPage extends WebPage {

    protected static final Logger logger = LoggerFactory.getLogger(DispatcherPage.class);

    private final WebMarkupContainer bodyContainer = new WebMarkupContainer("body");

    @Inject
    private PetitionService<?> petitionService;

    public DispatcherPage() {
        initPage();
        dispatch(parseParameters(getRequest()));
    }

    private void initPage() {
        getApplication().setHeaderResponseDecorator(new SingularHeaderResponseDecorator());
        bodyContainer
                .add(new HeaderResponseContainer("scripts", "scripts"));
        add(bodyContainer);
        add(new SingularJSBehavior());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(new PackageResourceReference(Template.class, "singular.js")));
    }

    private SingularWebRef retrieveSingularWebRef(FormPageConfig cfg) {
        final TaskInstance ti = findCurrentTaskByPetitionId(cfg.getFormId());
        if (ti != null) {
            final MTask task = ti.getFlowTask();
            if (task instanceof MTaskUserExecutable) {
                final ITaskPageStrategy pageStrategy = ((MTaskUserExecutable) task).getExecutionPage();
                if (pageStrategy instanceof SingularServerTaskPageStrategy) {
                    return (SingularWebRef) pageStrategy.getPageFor(ti, null);
                } else {
                    logger.warn("Atividade atual possui uma estratégia de página não suportada. A página default será utilizada.");
                }
            } else if (!ViewMode.READ_ONLY.equals(cfg.getViewMode())) {
                throw new SingularServerException("Página invocada para uma atividade que não é do tipo MTaskUserExecutable");
            }
        }
        return null;
    }

    private <T> T createNewInstanceUsingFormPageConfigConstructor(Class<T> clazz, FormPageConfig config) throws Exception {
        Constructor c = clazz.getConstructor(FormPageConfig.class);
        return (T) c.newInstance(config);
    }

    private WebPage retrieveDestination(FormPageConfig config) {
        return retrieveDestinationUsingSingularWebRef(config, retrieveSingularWebRef(config));
    }

    private WebPage retrieveDestinationUsingSingularWebRef(FormPageConfig config, SingularWebRef ref) {
        try {
            if (ref == null || ref.getPageClass() == null) {
                return createNewInstanceUsingFormPageConfigConstructor(getDefaultFormPageClass(), config);
            } else if (AbstractFormPage.class.isAssignableFrom(ref.getPageClass())) {
                return createNewInstanceUsingFormPageConfigConstructor(ref.getPageClass(), config);
            } else {
                return ref.getPageClass().newInstance();
            }
        } catch (Exception e) {
            closeAndReloadParent();
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    protected void dispatch(FormPageConfig config) {
        if (config != null) {
            dispatchForDestination(config, retrieveDestination(config));
        } else {
            closeAndReloadParent();
        }
    }

    private void dispatchForDestination(FormPageConfig config, WebPage destination) {
        if (destination != null) {
            configureReload(destination);
            onDispatch(destination, config);
            setResponsePage(destination);
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
        add($b.onReadyScript(() -> " Singular.atualizarContentWorklist(); window.close(); "));
    }

    private StringValue getParam(Request r, String key) {
        return r.getRequestParameters().getParameterValue(key);
    }

    private FormActions resolveFormAction(StringValue action) {
        return FormActions.getById(Integer.parseInt(action.toString("0")));
    }

    private FormPageConfig parseParameters(Request r) {

        final StringValue action            = getParam(r, ACTION);
        final StringValue formId            = getParam(r, FORM_ID);
        final StringValue formName          = getParam(r, SIGLA_FORM_NAME);
        final StringValue forceViewMainForm = getParam(r, FORCE_VIEW_MAIN_FORM);

        if (action.isEmpty()) {
            throw new RedirectToUrlException(getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getUrl()) + "/singular");
        }

        final FormActions formActions = resolveFormAction(action);

        final String         fi = formId.toString("");
        final AnnotationMode am = formActions.getAnnotationMode() == null ? AnnotationMode.NONE : formActions.getAnnotationMode();
        final ViewMode       vm = formActions.getViewMode();
        final String         fn = formName.toString();

        final FormPageConfig cfg = buildConfig(r, fi, am, vm, fn);


        if (cfg != null) {

            cfg.setForceViewMainForm(forceViewMainForm.toBoolean(false));

            if (!(cfg.containsProcessDefinition() || cfg.isWithLazyProcessResolver())) {
                throw new SingularServerException("Nenhum fluxo está configurado");
            }
            return cfg;
        } else {
            return null;
        }

    }

    protected abstract FormPageConfig buildConfig(Request r, String formId, AnnotationMode annotationMode, ViewMode viewMode, String formType);

    /**
     * Possibilita execução de qualquer ação antes de fazer o dispatch
     *
     * @param destination pagina destino
     * @param config      config atual
     */
    protected void onDispatch(WebPage destination, FormPageConfig config) {
    }

    protected TaskInstance findCurrentTaskByPetitionId(String petitionId) {
        if (StringUtils.isBlank(petitionId)) {
            return null;
        } else {
            return Flow.getTaskInstance(petitionService.findCurrentTaskByPetitionId(Long.valueOf(petitionId)));
        }
    }

    protected Class<? extends AbstractFormPage> getDefaultFormPageClass() {
        return FormPage.class;
    }

}
