package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.view.FileEventListener;
import org.opensingular.lib.commons.util.Loggable;

public abstract class AbstractPdfUploadListener implements FileEventListener, Loggable {
    private final OcrHelper ocrHelper = new OcrHelper();

    @Override
    public void accept(SIAttachment attachment) {
        if (ocrHelper.isValid(attachment)) {
            acceptPdfWithoutText(attachment);
        }
    }

    protected abstract void acceptPdfWithoutText(SIAttachment attachment);
}