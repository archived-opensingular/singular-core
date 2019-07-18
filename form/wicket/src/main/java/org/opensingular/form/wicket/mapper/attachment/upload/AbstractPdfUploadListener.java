package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.view.FileEventListener;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.util.Loggable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public abstract class AbstractPdfUploadListener implements FileEventListener, Loggable {

    public static final String  MIN_TEXT_SIZE_THRESHOLD_PARAM = "singular.fileupload.ocr.min_chars";
    private final       Integer MIN_TEXT_SIZE_THRESHOLD;

    public AbstractPdfUploadListener() {
        MIN_TEXT_SIZE_THRESHOLD = NumberUtils.toInt(SingularProperties.get(MIN_TEXT_SIZE_THRESHOLD_PARAM), 0);
    }

    @Override
    public void accept(SIAttachment attachment) {
        if (isPdf(attachment)) {
            if (!hasTextOrSignature(attachment)) {
                acceptPdfWithoutText(attachment);
            }
        }
    }

    protected abstract void acceptPdfWithoutText(SIAttachment attachment);

    private boolean hasTextOrSignature(SIAttachment attachment) {
        try {
            Optional<InputStream> contentAsInputStream = attachment.getContentAsInputStream();
            if (contentAsInputStream.isPresent()) {
                PDDocument pdDoc = PDDocument.load(contentAsInputStream.get());
                return hasText(pdDoc)
                        || hasSignature(pdDoc);
            }
        } catch (IOException e) {
            String msg = "Não foi possível verificar o reconhecimento ótico de caracteres (OCR) do arquivo PDF.";
            getLogger().warn(msg, e);
            throw new SingularUploadException(attachment.getFileName(), msg);
        }
        return false;
    }

    private boolean hasSignature(PDDocument pdDoc) throws IOException {
        return !pdDoc.getSignatureDictionaries().isEmpty();
    }

    public boolean hasText(PDDocument pdDoc) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String          parsedText  = pdfStripper.getText(pdDoc);
        //Remove all non visible characters
        return parsedText.replaceAll("\\p{C}", "").length() > MIN_TEXT_SIZE_THRESHOLD;
    }

    private boolean isPdf(SIAttachment attachment) {
        return attachment.getFileName().toLowerCase().endsWith(".pdf");
    }

}
