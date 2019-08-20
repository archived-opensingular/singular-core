package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.util.OcrUtil;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.util.Loggable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class OcrHelper implements Loggable, Serializable {
    public static final String MIN_TEXT_SIZE_THRESHOLD_PARAM = "singular.fileupload.ocr.min_chars";

    private int getMinTextSizeThreshold() {
        return NumberUtils.toInt(SingularProperties.get(MIN_TEXT_SIZE_THRESHOLD_PARAM), 0);
    }

    public boolean isValid(SIAttachment attachment) {
        return isPdf(attachment) && !hasTextOrSignature(attachment);
    }

    private boolean hasTextOrSignature(SIAttachment attachment) {
        final Optional<InputStream> contentAsInputStream = attachment.getContentAsInputStream();
        if (!contentAsInputStream.isPresent()) {
            return false;
        }
        try (InputStream in = contentAsInputStream.get()) {
            PDDocument pdDoc = PDDocument.load(in);
            return hasText(pdDoc)
                    || hasSignature(pdDoc);
        } catch (IOException e) {
            String msg = "Não foi possível verificar o reconhecimento ótico de caracteres (OCR) do arquivo PDF.";
            getLogger().warn(msg, e);
            throw new SingularUploadException(attachment.getFileName(), msg);
        }
    }

    private boolean hasSignature(PDDocument pdDoc) throws IOException {
        return !pdDoc.getSignatureDictionaries().isEmpty();
    }

    private boolean hasText(PDDocument pdDoc) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String          parsedText  = pdfStripper.getText(pdDoc);
        //Remove all non visible characters
        return parsedText.replaceAll("\\p{C}", "").length() > getMinTextSizeThreshold();
    }

    private boolean isPdf(SIAttachment attachment) {
        return attachment.getFileName().toLowerCase().endsWith(".pdf");
    }

    public File toOcrFile(InputStream attachment, boolean isCloseOriginalStream) {
        try {
            final Path             ocrContent          = Files.createTempFile("ocr", ".pdf");
            final FileOutputStream ocrContentOutStream = new FileOutputStream(ocrContent.toFile());

            IOUtils.copy(attachment, ocrContentOutStream);

            ocrContentOutStream.close();
            if (isCloseOriginalStream) {
                attachment.close();
            }

            return OcrUtil.runOcrOnPdfCommandLine(ocrContent.toFile());
        } catch (Exception ex) {
            throw SingularException.rethrow("Não foi possivel inflar o inputstream do anexo", ex);
        }
    }
}