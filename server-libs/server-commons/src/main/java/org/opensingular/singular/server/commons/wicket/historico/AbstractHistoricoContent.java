package org.opensingular.singular.server.commons.wicket.historico;

import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.singular.server.commons.exception.SingularServerException;
import org.opensingular.singular.server.commons.form.FormActions;
import org.opensingular.singular.server.commons.persistence.entity.form.FormVersionHistoryEntity;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import org.opensingular.singular.server.commons.service.PetitionService;
import org.opensingular.singular.server.commons.util.Parameters;
import org.opensingular.singular.server.commons.wicket.SingularSession;
import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.opensingular.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.opensingular.singular.support.persistence.enums.SimNao;
import org.opensingular.singular.util.wicket.datatable.BSDataTable;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
import org.opensingular.singular.util.wicket.datatable.BaseDataProvider;
import org.opensingular.singular.util.wicket.resource.Icone;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;

public abstract class AbstractHistoricoContent extends Content {

    private static final long serialVersionUID = 8587873133590041152L;

    @Inject
    private PetitionService<?> petitionService;

    private int    instancePK;
    private String processGroupPK;

    public AbstractHistoricoContent(String id) {
        super(id);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return new ResourceModel("label.historico.title");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return null;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        instancePK = getPage().getPageParameters().get(Parameters.INSTANCE_ID).toInt();
        processGroupPK = getPage().getPageParameters().get(Parameters.PROCESS_GROUP_PARAM_NAME).toString();
        queue(setupDataTable());
        queue(getBtnCancelar());
    }

    protected AjaxLink<?> getBtnCancelar() {
        return new AjaxLink<Void>("btnCancelar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancelar(target);
            }
        };
    }

    protected abstract void onCancelar(AjaxRequestTarget t);

    private BSDataTable<PetitionContentHistoryEntity, String> setupDataTable() {
        return new BSDataTableBuilder<>(createDataProvider())
                .appendPropertyColumn(
                        getMessage("label.table.column.task.name"),
                        p -> p.getTaskInstanceEntity().getTask().getName()
                )
                .appendPropertyColumn(
                        getMessage("label.table.column.begin.date"),
                        p -> p.getTaskInstanceEntity().getBeginDate()
                )
                .appendPropertyColumn(
                        getMessage("label.table.column.end.date"),
                        p -> p.getTaskInstanceEntity().getEndDate()
                )
                .appendPropertyColumn(
                        getMessage("label.table.column.allocated.user"),
                        p -> Optional.ofNullable(p.getActor()).map(Actor::getNome).orElse("")
                )
                .appendActionColumn(
                        Model.of(""),
                        column -> column.appendStaticAction(Model.of("Visualizar"), Icone.EYE, (id, model) -> {
                            final String url = DispatcherPageUtil.baseURL(getBaseUrl())
                                    .formAction(FormActions.FORM_VIEW.getId())
                                    .formId(null)
                                    .params(buildViewFormParameters(model))
                                    .build();
                            final WebMarkupContainer link = new WebMarkupContainer(id);
                            link.add($b.attr("target", String.format("_%s", model.getObject().getCod())));
                            link.add($b.attr("href", url));
                            return link;
                        })
                ).build("tabela");
    }

    protected Map<String, String> buildViewFormParameters(IModel<PetitionContentHistoryEntity> model) {
        final Map<String, String> params = new HashMap<>();
        params.put(Parameters.FORM_VERSION_KEY, model
                .getObject()
                .getFormVersionHistoryEntities()
                .stream()
                .filter(f -> SimNao.SIM.equals(f.getMainForm()))
                .findFirst()
                .map(FormVersionHistoryEntity::getCodFormVersion)
                .map(Object::toString)
                .orElse(null));
        return params;
    }

    private BaseDataProvider<PetitionContentHistoryEntity, String> createDataProvider() {
        return new BaseDataProvider<PetitionContentHistoryEntity, String>() {
            @Override
            public long size() {
                return petitionService.listPetitionContentHistoryByCodInstancePK(instancePK).size();
            }

            @Override
            public Iterator<? extends PetitionContentHistoryEntity> iterator(int first, int count, String sortProperty, boolean ascending) {
                return petitionService.listPetitionContentHistoryByCodInstancePK(instancePK).iterator();
            }
        };
    }

    protected String getBaseUrl() {
        return getModuleContext() + SingularSession.get().getServerContext().getUrlPath();
    }

    public String getModuleContext() {
        final String groupConnectionURL = petitionService.findByProcessGroupCod(processGroupPK).getConnectionURL();
        try {
            final String path = new URL(groupConnectionURL).getPath();
            return path.substring(0, path.indexOf("/", 1));
        } catch (Exception e) {
            throw new SingularServerException(String.format("Erro ao tentar fazer o parse da URL: %s", groupConnectionURL), e);
        }
    }

}
