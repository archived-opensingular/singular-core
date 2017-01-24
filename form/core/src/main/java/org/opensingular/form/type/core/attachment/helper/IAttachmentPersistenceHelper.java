package org.opensingular.form.type.core.attachment.helper;


import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;

public interface IAttachmentPersistenceHelper {

    void doPersistence(SDocument document, IAttachmentPersistenceHandler temporaryHandler, IAttachmentPersistenceHandler persistenceHandler);

}