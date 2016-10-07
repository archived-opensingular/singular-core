package org.opensingular.ws.wkhtmltopdf.client;


import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.ws.wkhtmltopdf.dto.WKHtmlToPdfDTO;
import org.apache.commons.io.IOUtils;
import org.opensingular.ws.wkhtmltopdf.constains.WkHtmlToPdfConstants;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class WkHtmlToPdfRestClient implements Loggable {

    public Optional<File> convertHtmlToPdf(WKHtmlToPdfDTO wkHtmlToPdfDTO) {

        final byte[] response = createRestTemplate()
                .postForObject(
                        retrieveWSBaseURL() + WkHtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH,
                        wkHtmlToPdfDTO,
                        byte[].class
                );
        if (response != null) {
            try {
                final File             temp   = File.createTempFile("singular-ws-html2pdf" + UUID.randomUUID().toString(), ".pdf");
                final FileOutputStream output = new FileOutputStream(temp);
                IOUtils.write(response, output);
                return Optional.of(temp);
            } catch (IOException ex) {
                getLogger().error("Não foi possivel escrever o arquivo temporario", ex);
            }
        }
        return Optional.empty();
    }

    private String retrieveWSBaseURL() {
        return System.getProperty("singular.ws.wkhtmltopdf.url", "http://10.0.0.142/wkhtmltopdf-ws");
    }

    private RestTemplate createRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        return restTemplate;
    }

}