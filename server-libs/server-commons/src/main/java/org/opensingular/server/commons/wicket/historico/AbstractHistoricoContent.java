/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.wicket.historico;

import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.server.commons.exception.SingularServerException;
import org.opensingular.server.commons.form.FormActions;
import org.opensingular.server.commons.persistence.entity.form.FormVersionHistoryEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.util.Parameters;
import org.opensingular.server.commons.wicket.SingularSession;
import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.resource.Icone;
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

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public abstract class AbstractHistoricoContent extends Content {

    private static final long serialVersionUID = 8587873133590041152L;

    @Inject
    private PetitionService<?> petitionService;

    private int    instancePK;
    private long   petitionPK;
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
        petitionPK = getPage().getPageParameters().get(Parameters.PETITION_ID).toLong();
        instancePK = getPage().getPageParameters().get(Parameters.INSTANCE_ID).toInt();
        processGroupPK = getPage().getPageParameters().get(Parameters.PROCESS_GROUP_PARAM_NAME).toString();
        queue(setupDataTable(createDataProvider()));
        queue(getBtnCancelar());
    }

    protected AjaxLink<?> getBtnCancelar() {
        return new AjaxLink<Void>("btnVoltar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancelar(target);
            }
        };
    }

    protected abstract void onCancelar(AjaxRequestTarget t);

    protected BSDataTable<PetitionContentHistoryEntity, String> setupDataTable(BaseDataProvider<PetitionContentHistoryEntity, String> dataProvider) {
        return new BSDataTableBuilder<>(dataProvider)
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

    protected BaseDataProvider<PetitionContentHistoryEntity, String> createDataProvider() {
        return new BaseDataProvider<PetitionContentHistoryEntity, String>() {
            @Override
            public long size() {
                return petitionService.listPetitionContentHistoryByPetitionCod(petitionPK).size();
            }

            @Override
            public Iterator<? extends PetitionContentHistoryEntity> iterator(int first, int count, String sortProperty, boolean ascending) {
                return petitionService.listPetitionContentHistoryByPetitionCod(petitionPK).iterator();
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
