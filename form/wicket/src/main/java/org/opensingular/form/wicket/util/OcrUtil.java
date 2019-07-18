package org.opensingular.form.wicket.util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opensingular.lib.commons.base.SingularException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class OcrUtil {

    public static File runOcrOnPdf(String path) {
        ITesseract tesseract = new Tesseract();
        //TODO delfino separar em propriedades
        tesseract.setDatapath("/opt/local/share/tessdata/");
        tesseract.setLanguage("por_fast");

        try {
            File tempFile = File.createTempFile("file", ".pdf");
            tesseract.createDocuments(
                    path,
                    tempFile.getAbsolutePath().replace(".pdf", ""),
                    Arrays.asList(ITesseract.RenderedFormat.PDF)
            );
            return tempFile;
        } catch (TesseractException| IOException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

}
