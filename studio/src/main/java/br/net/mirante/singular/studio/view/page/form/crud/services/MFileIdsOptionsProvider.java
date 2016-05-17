/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.view.page.form.crud.services;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.provider.SSimpleProvider;
import br.net.mirante.singular.form.util.transformer.SCompositeListBuilder;
import br.net.mirante.singular.studio.dao.form.FileDao;

@SuppressWarnings("serial")
@Component("filesChoiceProvider")
public class MFileIdsOptionsProvider implements SSimpleProvider {

    @Inject
    private FileDao filePersistence;

    @Override
    public void fill(SCompositeListBuilder builder) {
        filePersistence.list().forEach(file -> {
            builder.add()
                    .set("id", file.getId())
                    .set("hashSha1", file.getHashSha1());
        });
    }

}