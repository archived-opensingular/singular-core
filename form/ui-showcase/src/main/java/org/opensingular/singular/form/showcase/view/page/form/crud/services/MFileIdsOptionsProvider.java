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

package org.opensingular.singular.form.showcase.view.page.form.crud.services;

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