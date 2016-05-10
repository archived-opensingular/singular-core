/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.crud.services;

import br.net.mirante.singular.form.provider.SSimpleProvider;
import br.net.mirante.singular.form.util.transformer.SCompositeListBuilder;
import br.net.mirante.singular.showcase.dao.form.FileDao;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

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