package org.opensingular.singular.ws.controller;

import org.opensingular.singular.commons.pdf.PDFUtil;
import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.singular.ws.dto.WKHtmlToPdfDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;

import static org.opensingular.singular.ws.constains.WkHtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;

@RestController
public class WkHtmlToPdfRestController implements Loggable {

    @ResponseBody
    @RequestMapping(value = CONVERT_HTML_TO_PDF_PATH, method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<InputStreamResource> convertHtmlToPdf(@RequestBody WKHtmlToPdfDTO dto) {
        final File file;
        try {
            file = PDFUtil.getInstance().convertHTML2PDF(dto.getBody(), dto.getHeader(), dto.getFooter());
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