package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.view.FileEventListener;
import org.opensingular.lib.commons.util.Loggable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class FilePdfOcrUploadListener implements FileEventListener, Loggable {
    @Override
    public void accept(SIAttachment attachment) {
        if (isPdf(attachment)) {
            if (!hasText(attachment)) {
                throw new SingularUploadException(attachment.getFileName(), "Arquivos digitalizados em formato PDF " +
                        "devem conter reconhecimento ótico de caracteres (OCR), para que seja possível pesquisar o texto no arquivo");
            }
        }
    }

    public boolean isPdf(SIAttachment attachment) {
        return attachment.getFileName().endsWith("pdf");
    }

    public boolean hasText(SIAttachment attachment) {
        try {
            Optional<InputStream> contentAsInputStream = attachment.getContentAsInputStream();
            if (contentAsInputStream.isPresent()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                PDDocument      pdDoc       = PDDocument.load(contentAsInputStream.get());
                String          parsedText  = pdfStripper.getText(pdDoc);
                //Remove all non visible characters
                return !parsedText.replaceAll("\\p{C}", "").isEmpty();
            }
        } catch (IOException e) {
            String msg = "Não foi possível verificar o reconhecimento ótico de caracteres (OCR) do arquivo PDF.";
            getLogger().warn(msg, e);
            throw new SingularUploadException(attachment.getFileName(), msg);
        }
        return false;
    }
}
