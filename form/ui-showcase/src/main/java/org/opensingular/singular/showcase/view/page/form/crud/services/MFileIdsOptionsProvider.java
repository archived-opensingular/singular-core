/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.view.page.form.crud.services;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import org.opensingular.form.provider.SSimpleProvider;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.util.transformer.SCompositeListBuilder;

@SuppressWarnings("serial")
@Component("filesChoiceProvider")
public class MFileIdsOptionsProvider implements SSimpleProvider {

    @Inject
    private IAttachmentPersistenceHandler<IAttachmentRef> filePersistence;

    @Override
    public void fill(SCompositeListBuilder builder) {
        filePersistence.getAttachments().forEach(file -> {
            builder.add()
                    .set("id", file.getId())
                    .set("hashSha1", file.getHashSHA1());
        });
    }

}