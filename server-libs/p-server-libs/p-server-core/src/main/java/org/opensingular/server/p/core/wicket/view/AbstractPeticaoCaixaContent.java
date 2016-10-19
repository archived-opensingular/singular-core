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

package org.opensingular.server.p.core.wicket.view;

import static org.opensingular.server.commons.util.DispatcherPageParameters.SIGLA_FORM_NAME;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import org.opensingular.server.commons.form.FormActions;
import org.opensingular.server.commons.persistence.dto.PeticaoDTO;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.wicket.view.util.DispatcherPageUtil;

/**
 * Classe base para construição de caixas do servidor de petições
 */
public abstract class AbstractPeticaoCaixaContent<T extends PeticaoDTO> extends AbstractCaixaContent<T> {

    private static final long serialVersionUID = -3611649597709058163L;

    @Inject
    private PetitionService<?> petitionService;

    public AbstractPeticaoCaixaContent(String id, String processGroupCod, String menu) {
        super(id, processGroupCod, menu);
    }

    @Override
    protected WebMarkupContainer criarLink(String id, IModel<T> peticaoModel, FormActions formActions) {
        T peticao = peticaoModel.getObject();
        String href = DispatcherPageUtil
                .baseURL(getBaseUrl())
                .formAction(formActions.getId())
                .petitionId(peticao.getCodPeticao())
                .params(getCriarLinkParameters(peticao))
                .build();

        WebMarkupContainer link = new WebMarkupContainer(id);
        link.add($b.attr("target", String.format("_%s", peticao.getCodPeticao())));
        link.add($b.attr("href", href));
        return link;
    }

    @Override
    protected Map<String, String> getCriarLinkParameters(T peticao){
        Map<String, String> params = new HashMap<>();
        params.put(SIGLA_FORM_NAME, peticao.getType());
        return params;
    }

    @Override
    protected void onDelete(PeticaoDTO peticao) {
        petitionService.deletePetition(peticao);
    }
}
