package br.net.mirante.singular.server.module.wicket.view.util.dispatcher;

import br.net.mirante.singular.flow.core.*;
import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.flow.SingularServerTaskPageStrategy;
import br.net.mirante.singular.server.commons.flow.SingularWebRef;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.spring.security.SingularUserDetails;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.error.AccessDeniedPage;
import br.net.mirante.singular.server.commons.wicket.view.SingularHeaderResponseDecorator;
import br.net.mirante.singular.server.commons.wicket.view.behavior.SingularJSBehavior;
import br.net.mirante.singular.server.commons.wicket.view.form.AbstractFormPage;
import br.net.mirante.singular.server.commons.wicket.view.form.FormPageConfig;
import br.net.mirante.singular.server.commons.wicket.view.form.ReadOnlyFormPage;
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
import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.util.Optional;

import static br.net.mirante.singular.server.commons.util.Parameters.*;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

@SuppressWarnings("serial")
@MountPath(DispatcherPageUtil.DISPATCHER_PAGE_PATH)
public abstract class DispatcherPage extends WebPage {

    protected static final Logger logger = LoggerFactory.getLogger(DispatcherPage.class);

    private final WebMarkupContainer bodyContainer = new WebMarkupContainer("body");

    @Inject
    private PetitionService<?> petitionService;

    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String> singularFormConfig;

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
        final TaskInstance ti = findCurrentTaskByPetitionId(cfg.getPetitionId());
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
        if (config.getViewMode().isVisualization() && config.getFormVersionPK() != null) {
            return new ReadOnlyFormPage($m.ofValue(config.getFormVersionPK()));
        } else {
            return retrieveDestinationUsingSingularWebRef(config, retrieveSingularWebRef(config));
        }
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
        if (config != null && !hasAccess(config)) {
            redirectForbidden();
        } else if (config != null) {
            dispatchForDestination(config, retrieveDestination(config));
        } else {
            closeAndReloadParent();
        }
    }

    protected boolean hasAccess(FormPageConfig config) {
        SingularUserDetails       userDetails    = SingularSession.get().getUserDetails();
        SType<?>                  sType          = loadType(config);
        Class<? extends SType<?>> sTypeClass     = (Class<? extends SType<?>>) sType.getClass();
        String                    typeSimpleName = SFormUtil.getTypeSimpleName(sTypeClass);

        String permissionsNeeded = config.getFormAction().toString() + "_" + typeSimpleName.toUpperCase();

        return userDetails.getPermissionsSingular().contains(permissionsNeeded);
    }

    private SType<?> loadType(FormPageConfig cfg) {
        return singularFormConfig.getTypeLoader().loadTypeOrException(
                Optional.ofNullable(cfg.getFormType()).orElseGet(() -> loadTypeNameFormFormVersionPK(cfg))
        );
    }

    private String loadTypeNameFormFormVersionPK(FormPageConfig cfg) {
        return Optional
                .ofNullable(cfg.getFormVersionPK())
                .map(petitionService::findFormTypeFromVersion)
                .map(FormTypeEntity::getAbbreviation)
                .orElseThrow(() -> new SingularServerException("Não possivel idenfiticar o tipo"));
    }

    protected void redirectForbidden() {
        setResponsePage(AccessDeniedPage.class);
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

    protected FormPageConfig parseParameters(Request r) {

        final StringValue action            = getParam(r, ACTION);
        final StringValue petitionId        = getParam(r, PETITION_ID);
        final StringValue formName          = getParam(r, SIGLA_FORM_NAME);
        final StringValue formVersionPK     = getParam(r, FORM_VERSION_KEY);

        if (action.isEmpty()) {
            throw new RedirectToUrlException(getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getUrl()) + "/singular");
        }

        final FormActions formAction = resolveFormAction(action);

        final String pi  = petitionId.toString("");
        final String fn  = formName.toString();
        final Long   fvk = formVersionPK.isEmpty() ? null : formVersionPK.toLong();

        final FormPageConfig cfg = buildConfig(r, pi, formAction, fn, fvk);

        if (cfg != null) {
            if (!(cfg.containsProcessDefinition() || cfg.isWithLazyProcessResolver())) {
                throw new SingularServerException("Nenhum fluxo está configurado");
            }
            return cfg;
        } else {
            return null;
        }

    }

    protected abstract FormPageConfig buildConfig(Request r, String petitionId, FormActions formAction, String formType, Long fvk);

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
