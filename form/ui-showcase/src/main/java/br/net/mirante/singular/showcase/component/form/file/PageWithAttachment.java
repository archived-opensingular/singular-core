/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.file;

import org.opensingular.singular.form.RefService;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.document.SDocument;
import org.opensingular.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.singular.form.type.core.attachment.IAttachmentRef;

public class PageWithAttachment {

    RefService<IAttachmentPersistenceHandler<? extends IAttachmentRef>> persistanceRef;

    public void beforeRendering(SIComposite instance){
        /**
         * Você deve estabelecer o serviço para persistir seus arquivos.
         */
        SDocument document = instance.getDocument();
        document.setAttachmentPersistencePermanentHandler(persistanceRef);
    }

    public void beforeSave(SIComposite instance){
        /**
         * Isto converterá seus arquivos de temporários para persistentes.
         */
        instance.getDocument().persistFiles();
    }

}
