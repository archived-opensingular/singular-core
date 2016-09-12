package br.net.mirante.singular.ws.controller;

import br.net.mirante.singular.commons.pdf.PDFUtil;
import br.net.mirante.singular.commons.util.Loggable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;

import static br.net.mirante.singular.ws.constains.WkHtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;

@RestController
public class WkHtmlToPdfRestController implements Loggable {

    @ResponseBody
    @RequestMapping(value = CONVERT_HTML_TO_PDF_PATH, method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<InputStreamResource> convertHtmlToPdf(@RequestBody String html) {
        final File file;
        try {
            file = PDFUtil.getInstance().convertHTML2PDF(html);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .contentLength(file.length())
                    .body(new InputStreamResource(new FileInputStream(file)));
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
        return null;
    }

}