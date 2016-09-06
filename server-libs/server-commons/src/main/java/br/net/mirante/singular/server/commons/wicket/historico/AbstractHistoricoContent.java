package br.net.mirante.singular.server.commons.wicket.historico;

import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.util.Parameters;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.resource.Icone;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

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

    protected Link<?> getBtnCancelar() {
        return new Link<Void>("btnCancelar") {
            @Override
            public void onClick() {
                setResponsePage(getBackPage());
            }
        };
    }

    protected abstract Class<? extends Page> getBackPage();

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
                                    .param(Parameters.FORM_VERSION_KEY, model.getObject().getFormVersionEntity().getCod())
                                    .build();
                            final WebMarkupContainer link = new WebMarkupContainer(id);
                            link.add($b.attr("target", String.format("_%s", model.getObject().getCod())));
                            link.add($b.attr("href", url));
                            return link;
                        })
                ).build("tabela");
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
