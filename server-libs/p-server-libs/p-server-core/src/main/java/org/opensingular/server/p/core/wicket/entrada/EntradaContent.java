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

package org.opensingular.server.p.core.wicket.entrada;


import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.server.commons.form.FormActions;
import org.opensingular.server.commons.persistence.dto.PeticaoDTO;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.opensingular.server.p.core.wicket.view.AbstractPeticaoCaixaContent;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.column.BSActionColumn;
import org.opensingular.lib.wicket.util.resource.Icone;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;

import static org.opensingular.server.commons.util.DispatcherPageParameters.FORM_NAME;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class EntradaContent extends AbstractPeticaoCaixaContent<PeticaoDTO> {

    @Inject
    protected PetitionService<PetitionEntity> peticaoService;

    public EntradaContent(String id, String processGroupCod, String siglaProcesso) {
        super(id, processGroupCod, siglaProcesso);
    }

    @Override
    public QuickFilter montarFiltroBasico() {
        return new QuickFilter()
                .withFilter(getFiltroRapidoModelObject())
                .withRascunho(false);
    }

    @Override
    protected long countQuickSearch(QuickFilter filter, List<String> processesNames, List<String> formNames) {
        return peticaoService.countQuickSearch(filter, processesNames, formNames);
    }

    @Override
    protected List<PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso, List<String> formNames) {
        return peticaoService.quickSearch(filtro, siglasProcesso, formNames);
    }

    @Override
    protected void appendPropertyColumns(BSDataTableBuilder<PeticaoDTO, String, IColumn<PeticaoDTO, String>> builder) {
//        builder.appendPropertyColumn(getMessage("label.table.column.process.number"), "t.numeroProcesso", IPetitionDTO::getProcessNumber);
        builder.appendPropertyColumn(getMessage("label.table.column.process"), "p.processName", PeticaoDTO::getProcessName);
        builder.appendPropertyColumn(getMessage("label.table.column.in.date"), "pie.beginDate", PeticaoDTO::getProcessBeginDate);
        builder.appendPropertyColumn(getMessage("label.table.column.situation"), "task.name", PeticaoDTO::getSituation);
        builder.appendPropertyColumn(getMessage("label.table.column.situation.date"), "ct.beginDate", PeticaoDTO::getSituationBeginDate);
    }

    @Override
    protected Pair<String, SortOrder> getSortProperty() {
        return Pair.of("pie.beginDate", SortOrder.DESCENDING);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("Exigência");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Petições pendentes de ajuste");
    }

    @Override
    protected void appendActionColumns(BSDataTableBuilder<PeticaoDTO, String, IColumn<PeticaoDTO, String>> builder) {
        BSActionColumn<PeticaoDTO, String> actionColumn = new BSActionColumn<>(getMessage("label.table.column.actions"));
        appendCumprirExigenciaAction(actionColumn);
        appendViewAction(actionColumn);
        builder.appendColumn(actionColumn);
    }

    private void appendCumprirExigenciaAction(BSActionColumn<PeticaoDTO, String> actionColumn) {
        actionColumn
                .appendStaticAction(getMessage("label.table.column.requirement"),
                        Icone.PENCIL, (id, pet) -> criarLink(id, pet, ViewMode.EDIT, AnnotationMode.READ_ONLY));
    }

    private WebMarkupContainer criarLink(String id, IModel<PeticaoDTO> peticaoModel, ViewMode vm, AnnotationMode annotationMode) {
        String href = DispatcherPageUtil.
                baseURL(getBaseUrl())
                .formAction(FormActions.FORM_FILL_WITH_ANALYSIS.getId())
                .petitionId(peticaoModel.getObject().getCodPeticao())
                .param(FORM_NAME, peticaoModel.getObject().getType())
                .build();
        WebMarkupContainer link = new WebMarkupContainer(id);
        link.add($b.attr("target", "_blank"));
        link.add($b.attr("href", href));
        return link;
    }
}
