package br.net.mirante.singular.server.module.wicket.view.util.dispatcher;

import static br.net.mirante.singular.server.commons.util.Parameters.ACTION;
import static br.net.mirante.singular.server.commons.util.Parameters.FORM_ID;
import static br.net.mirante.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.lang.reflect.Constructor;

import javax.inject.Inject;

import br.net.mirante.singular.server.commons.wicket.view.form.FormPageConfig;
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

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskUserExecutable;
import br.net.mirante.singular.flow.core.TaskInstance;
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
import br.net.mirante.singular.server.commons.wicket.view.template.Template;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.server.module.wicket.view.util.form.FormPage;

@SuppressWarnings("serial")
@MountPath(DispatcherPageUtil.DISPATCHER_PAGE_PATH)
public class DispatcherPage extends WebPage {

    protected static final Logger logger = LoggerFactory.getLogger(DispatcherPage.class);

    private final WebMarkupContainer bodyContainer = new WebMarkupContainer("body");

    @Inject
    private PetitionService<?> petitionService;

    public DispatcherPage() {
        this.add(bodyContainer);
        getApplication().setHeaderResponseDecorator(new SingularHeaderResponseDecorator());
        bodyContainer.add(new HeaderResponseContainer("scripts", "scripts"));
        add(new SingularJSBehavior());
        FormPageConfig config = parseParameters(getRequest());
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

    protected FormPageConfig parseParameters(Request request) {
        return buildConfig(request);
    }

    protected void dispatch(FormPageConfig config) {
        try {
            WebPage        destination = null;
            SingularWebRef ref         = null;
            TaskInstance   ti          = findCurrentTaskByPetitionId(config.getFormId());
            if (ti != null) {
                MTask task = ti.getFlowTask();
                if (task instanceof MTaskUserExecutable) {
                    ITaskPageStrategy pageStrategy = ((MTaskUserExecutable) task).getExecutionPage();
                    if (pageStrategy instanceof SingularServerTaskPageStrategy) {
                        ref = (SingularWebRef) pageStrategy.getPageFor(ti, null);
                    } else {
                        logger.warn("Atividade atual possui uma estratégia de página não suportada. A página default será utilizada.");
                    }
                } else if (!ViewMode.VISUALIZATION.equals(config.getViewMode())) {
                    throw new SingularServerException("Página invocada para uma atividade que não é do tipo MTaskUserExecutable");
                }
            }
            if (ref == null || ref.getPageClass() == null) {
                Constructor c = getDefaultFormPageClass().getConstructor(FormPageConfig.class);
                destination = (WebPage) c.newInstance(config);
            } else if (AbstractFormPage.class.isAssignableFrom(ref.getPageClass())) {
                Constructor c = ref.getPageClass().getConstructor(FormPageConfig.class);
                destination = (WebPage) c.newInstance(config);
            } else {
                destination = ref.getPageClass().newInstance();
            }
            configureReload(destination);
            onDispatch(destination, config);
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

    private StringValue getParam(Request r, String key) {
        return r.getRequestParameters().getParameterValue(key);
    }

    private FormActions resolveFormAction(StringValue action) {
        return FormActions.getById(Integer.parseInt(action.toString("0")));
    }

    private FormPageConfig buildConfig(Request r) {

        final StringValue action   = getParam(r, ACTION);
        final StringValue formId   = getParam(r, FORM_ID);
        final StringValue formName = getParam(r, SIGLA_FORM_NAME);

        if (action.isEmpty()) {
            throw new RedirectToUrlException(getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getUrl()) + "/singular");
        }

        final FormActions formActions = resolveFormAction(action);

        final FormPageConfig cfg = new FormPageConfig();

        cfg.setFormId(formId.toString(""));
        cfg.setAnnotationMode(formActions.getAnnotationMode() == null ? AnnotationMode.NONE : formActions.getAnnotationMode());
        cfg.setViewMode(formActions.getViewMode());
        cfg.setFormType(formName.toString());

        addFlowDefinitionConfigs(r, cfg);

        if (!(cfg.containsProcessDefinition() || cfg.isWithLazyProcessResolver())) {
            throw new SingularServerException("Nenhum fluxo está configurado");
        }

        return cfg;
    }

    protected void addFlowDefinitionConfigs(Request r, FormPageConfig cfg) {
    }

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
            return Flow.getTaskInstance(petitionService.findCurrentTaskByPetitionId(petitionId));
        }
    }

    protected Class<? extends AbstractFormPage> getDefaultFormPageClass() {
        return FormPage.class;
    }

}
