package br.net.mirante.singular.view.page.form.crud.services;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;

@SuppressWarnings("serial")
@Component("br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler")
public class InMemoryAttachmentPersitenceHandlerWrapper extends InMemoryAttachmentPersitenceHandler {

}
