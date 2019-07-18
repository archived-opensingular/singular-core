package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.form.type.core.attachment.SIAttachment;

public class FilePdfValidarTextoUploadListener extends AbstractPdfUploadListener {

    @Override
    protected void acceptPdfWithoutText(SIAttachment attachment) {
        throw new SingularUploadException(attachment.getFileName(), "Arquivos digitalizados em formato PDF " +
                "devem conter reconhecimento ótico de caracteres (OCR), para que seja possível pesquisar o texto no arquivo");
    }

}
