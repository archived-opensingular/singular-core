package br.net.mirante.singular.ws.client;


import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static br.net.mirante.singular.ws.constains.WkHtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;

public class WkHtmlToPdfRestClient {

    public Optional<File> convertHtmlToPdf(String html) throws IOException {
        final ResponseEntity<byte[]> response = createRestTemplate().exchange(
                retrieveWSBaseURL() + CONVERT_HTML_TO_PDF_PATH,
                HttpMethod.POST,
                new HttpEntity<>(html),
                byte[].class
        );
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            final File             temp   = File.createTempFile("singular-ws-html2pdf" + UUID.randomUUID().toString(), ".pdf");
            final FileOutputStream output = new FileOutputStream(temp);
            IOUtils.write(response.getBody(), output);
            return Optional.of(temp);
        }
        return Optional.empty();
    }

    private String retrieveWSBaseURL() {
        return System.getProperty("singular.ws.wkhtmltopdf.baseurl", "http://localhost:8080/wkhtmltopdf-ws");
    }

    private RestTemplate createRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        return restTemplate;
    }

}