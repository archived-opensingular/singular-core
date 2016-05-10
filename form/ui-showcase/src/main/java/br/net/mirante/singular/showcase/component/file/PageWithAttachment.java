/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.file;

import br.net.mirante.singular.form.RefService;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;

public class PageWithAttachment {

    RefService<IAttachmentPersistenceHandler> persistanceRef;

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
