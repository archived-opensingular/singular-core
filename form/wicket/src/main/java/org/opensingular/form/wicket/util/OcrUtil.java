package org.opensingular.form.wicket.util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.ITesseract.RenderedFormat;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularProperties;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.UUID;

public class OcrUtil {

    public static final String PDF_TO_TIFF_COMMAND       = "singular.fileupload.ocr.pdf_to_tiff.command";
    public static final String TESSERACT_PDF_OCR_COMMAND = "singular.fileupload.ocr.tesseract.command";

    public static File runOcrOnPdf(String path) {
        ITesseract tesseract = new Tesseract();
        //TODO delfino separar em propriedades
        tesseract.setDatapath("/usr/local/share/tessdata/");
        tesseract.setLanguage("por_fast");

        try {
            File tempFile = File.createTempFile("file", ".pdf");
            tempFile.deleteOnExit();
            tesseract.createDocuments(
                    path,
                    tempFile.getAbsolutePath().replace(".pdf", ""),
                    Collections.singletonList(RenderedFormat.PDF)
            );
            return tempFile;
        } catch (TesseractException | IOException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    public static File runOcrOnPdfCommandLine(File pdfFile) {
        try {
            Runtime rt        = Runtime.getRuntime();
            File    tiffFile  = File.createTempFile(UUID.randomUUID().toString(), ".tiff");
            Process pdfToTiff = rt.exec(MessageFormat.format(SingularProperties.get(PDF_TO_TIFF_COMMAND), pdfFile.getAbsolutePath(), tiffFile.getAbsolutePath()));
            if (pdfToTiff.waitFor() == 0) {
                Process tesseract = rt.exec(MessageFormat.format(SingularProperties.get(TESSERACT_PDF_OCR_COMMAND), tiffFile.getAbsolutePath(), pdfFile.getAbsolutePath()));
                tesseract.waitFor();
                return new File(pdfFile.getAbsolutePath() + ".pdf");
            }
        } catch (IOException | InterruptedException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
        return pdfFile;
    }

}
