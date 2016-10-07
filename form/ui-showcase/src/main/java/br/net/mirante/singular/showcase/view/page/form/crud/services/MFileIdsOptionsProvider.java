/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.crud.services;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.persistence.entity.AbstractAttachmentEntity;
import br.net.mirante.singular.form.provider.SSimpleProvider;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.util.transformer.SCompositeListBuilder;

@SuppressWarnings("serial")
@Component("filesChoiceProvider")
public class MFileIdsOptionsProvider implements SSimpleProvider {

    @Inject
    private IAttachmentPersistenceHandler<AbstractAttachmentEntity> filePersistence;

    @Override
    public void fill(SCompositeListBuilder builder) {
        filePersistence.getAttachments().forEach(file -> {
            builder.add()
                    .set("id", file.getId())
                    .set("hashSha1", file.getHashSha1());
        });
    }

}