package br.net.mirante.singular.showcase.component.file;

import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.showcase.component.custom.comment.CaseAnnotationPackage;

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
