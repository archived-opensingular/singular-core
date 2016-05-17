/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core.select;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import br.net.mirante.singular.studio.component.CaseBase;
import br.net.mirante.singular.studio.component.ResourceRef;
import br.net.mirante.singular.studio.view.page.form.crud.services.MFileIdsOptionsProvider;

public class CaseInputCoreSelectProvider extends CaseBase implements Serializable {
    public CaseInputCoreSelectProvider() {
        super("Select", "Provedor Dinâmico");
        setDescriptionHtml("É permitido alterar o provedor de dados de forma que estes sejam carregados de forma dinâmica ou de outras fontes de informação.");
    }

    @Override
    public List<ResourceRef> getAditionalSources() {
        Optional<ResourceRef> rr = ResourceRef.forSource(MFileIdsOptionsProvider.class);
        if(rr.isPresent()) return newArrayList(rr.get());
        return Collections.emptyList();

    }
}
