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

package org.opensingular.server.p.commons.flow.rest;

import org.opensingular.lib.support.spring.util.AutoScanDisabled;
import org.opensingular.lib.wicket.util.resource.Icone;
import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.flow.action.DefaultActions;
import org.opensingular.server.commons.flow.rest.DefaultServerMetadataREST;
import org.opensingular.server.commons.service.dto.ItemBox;
import org.opensingular.server.commons.service.dto.MenuGroup;
import org.opensingular.server.p.commons.config.PServerContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static org.opensingular.server.commons.flow.rest.DefaultServerREST.COUNT_PETITIONS;
import static org.opensingular.server.commons.flow.rest.DefaultServerREST.SEARCH_PETITIONS;

@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class DefaultPServerMetadataREST extends DefaultServerMetadataREST {

    @Override
    protected void customizeMenu(List<MenuGroup> groupDTOs, IServerContext menuContext, String user) {
        super.customizeMenu(groupDTOs, menuContext, user);
        if (Objects.equals(PServerContext.PETITION.getName(), menuContext.getName())) {
            for (MenuGroup menuGroup : groupDTOs) {
                List<ItemBox> itemBoxes = new ArrayList<>();
                criarItemRascunho(itemBoxes);
                criarItemAcompanhamento(itemBoxes);
                menuGroup.setItemBoxes(itemBoxes);
            }
        }
    }


    private LinkedHashMap<String, String> criarFieldsDatatableAcompanhamento() {
        LinkedHashMap<String, String> fields = new LinkedHashMap<>(5);
        fields.put("Número", "codPeticao");
        fields.put("Dt. Entrada", "processBeginDate");
        fields.put("Situação", "situation");
        fields.put("Dt. Situação", "situationBeginDate");
        return fields;
    }

    private void criarItemRascunho(List<ItemBox> itemBoxes) {
        final ItemBox rascunho = new ItemBox();
        rascunho.setName("Rascunho");
        rascunho.setDescription("Petições de rascunho");
        rascunho.setIcone(Icone.DOCS);
        rascunho.setShowNewButton(true);
        rascunho.setShowDraft(true);
        rascunho.setSearchEndpoint(SEARCH_PETITIONS);
        rascunho.setCountEndpoint(COUNT_PETITIONS);
        rascunho.setFieldsDatatable(criarFieldsDatatableRascunho());
        rascunho.addAction(DefaultActions.EDIT)
                .addAction(DefaultActions.VIEW)
                .addAction(DefaultActions.DELETE);
        itemBoxes.add(rascunho);
    }

    private LinkedHashMap<String, String> criarFieldsDatatableRascunho() {
        LinkedHashMap<String, String> fields = new LinkedHashMap<>(3);
        fields.put("Descrição", "description");
        fields.put("Dt. Edição", "editionDate");
        fields.put("Data de Entrada", "creationDate");
        return fields;
    }


    private void criarItemAcompanhamento(List<ItemBox> itemBoxes) {
        final ItemBox acompanhamento = new ItemBox();
        acompanhamento.setName("Acompanhamento");
        acompanhamento.setDescription("Petições em andamento");
        acompanhamento.setIcone(Icone.CLOCK);
        acompanhamento.setSearchEndpoint(SEARCH_PETITIONS);
        acompanhamento.setCountEndpoint(COUNT_PETITIONS);
        acompanhamento.setFieldsDatatable(criarFieldsDatatableAcompanhamento());
        acompanhamento.addAction(DefaultActions.VIEW);
        itemBoxes.add(acompanhamento);
    }


}