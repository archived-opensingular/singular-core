package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.commons.io.IOUtils;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.util.OcrUtil;
import org.opensingular.lib.commons.base.SingularException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FilePdfOcrListener extends AbstractPdfUploadListener {

    @Override
    protected void acceptPdfWithoutText(SIAttachment attachment) {
        IAttachmentRef attachmentRef = attachment.getAttachmentRef();
        try {
            File tempFile = File.createTempFile("ocr", ".pdf");
            tempFile.deleteOnExit();
            try (InputStream contentAsInputStream = attachmentRef.getContentAsInputStream();
                 FileOutputStream output = new FileOutputStream(tempFile)) {
                IOUtils.copy(contentAsInputStream, output);
            }

            File pdfOcr = OcrUtil.runOcrOnPdfCommandLine(tempFile);

            IAttachmentRef ref = attachment.getDocument().getAttachmentPersistenceTemporaryHandler()
                    .addAttachment(pdfOcr, Files.size(pdfOcr.toPath()), attachment.getFileName(), HashUtil.toSHA1Base16(pdfOcr));
            attachment.update(ref);

            if (tempFile.exists()) {
                tempFile.delete();
            }
            if (pdfOcr.exists()) {
                pdfOcr.delete();
            }
        } catch (IOException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

}
