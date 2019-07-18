package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.commons.io.IOUtils;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.util.OcrUtil;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.TempFileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class FilePdfOcrListener extends AbstractPdfUploadListener {

    @Override
    protected void acceptPdfWithoutText(SIAttachment attachment) {
        IAttachmentRef attachmentRef = attachment.getAttachmentRef();
        try {
            File tempFile = File.createTempFile("ocr", ".pdf");
            tempFile.deleteOnExit();

            IOUtils.copy(attachmentRef.getContentAsInputStream(), new FileOutputStream(tempFile));
            File              file   = TempFileUtils.stream2file(attachmentRef.getContentAsInputStream());

            File              pdfOcr = OcrUtil.runOcrOnPdf(tempFile.getPath());
            IAttachmentRef ref = attachment.getDocument().getAttachmentPersistenceTemporaryHandler()
                    .addAttachment(pdfOcr, Files.size(pdfOcr.toPath()), attachment.getFileName(), HashUtil.toSHA1Base16(pdfOcr));
            attachment.update(ref);

        } catch (IOException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

}
