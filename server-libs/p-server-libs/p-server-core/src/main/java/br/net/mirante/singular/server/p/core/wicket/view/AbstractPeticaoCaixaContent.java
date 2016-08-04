/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.p.core.wicket.view;

import static br.net.mirante.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;

import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;

/**
 * Classe base para construição de caixas do servidor de petições
 */
public abstract class AbstractPeticaoCaixaContent<T extends PeticaoDTO> extends AbstractCaixaContent<T> {

    private static final long serialVersionUID = -3611649597709058163L;

    @Inject
    private PetitionService petitionService;

    public AbstractPeticaoCaixaContent(String id, String processGroupCod, String menu) {
        super(id, processGroupCod, menu);
    }

    @Override
    protected WebMarkupContainer criarLink(T peticao, String id, FormActions formActions) {
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
