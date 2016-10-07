/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.p.core.wicket.view;

import static org.opensingular.server.commons.util.Parameters.SIGLA_FORM_NAME;
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
                .formId(peticao.getCodPeticao())
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
        petitionService.delete(peticao);
    }
}
