package org.opensingular.form.wicket.util;

import org.apache.commons.io.IOUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.UUID;

public class OcrUtil {

    private static final Logger LOGGER                    = LoggerFactory.getLogger(OcrUtil.class);
    public static final  String PDF_TO_TIFF_COMMAND       = "singular.fileupload.ocr.pdf_to_tiff.command";
    public static final  String TESSERACT_PDF_OCR_COMMAND = "singular.fileupload.ocr.tesseract.command";

    public static File runOcrOnPdfCommandLine(File pdfFile) {
        try {
            File   tiffFile  = File.createTempFile(UUID.randomUUID().toString(), ".tiff");
            String pdtToTiff = MessageFormat.format(SingularProperties.get(PDF_TO_TIFF_COMMAND), pdfFile.getAbsolutePath(), tiffFile.getAbsolutePath());
            if (runCommand(pdtToTiff)) {
                String tesseract = MessageFormat.format(SingularProperties.get(TESSERACT_PDF_OCR_COMMAND), tiffFile.getAbsolutePath(), pdfFile.getAbsolutePath());
                if (runCommand(tesseract)) {
                    return new File(pdfFile.getAbsolutePath() + ".pdf");
                }
            }
        } catch (IOException | InterruptedException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
        return pdfFile;
    }

    private static boolean runCommand(String command) throws IOException, InterruptedException {
        LOGGER.info("Executing command Line: $" + command);
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(command);
        LOGGER.info(IOUtils.toString(pr.getInputStream(), StandardCharsets.UTF_8));
        LOGGER.error(IOUtils.toString(pr.getErrorStream(), StandardCharsets.UTF_8));
        int exitValue = pr.waitFor();
        LOGGER.info("Exit Value:" + exitValue);
        return exitValue == 0;
    }

}
