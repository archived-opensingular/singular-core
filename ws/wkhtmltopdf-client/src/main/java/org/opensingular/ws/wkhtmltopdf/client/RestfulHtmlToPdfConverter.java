/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.ws.wkhtmltopdf.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.lib.commons.pdf.HtmlToPdfConverter;
import org.opensingular.ws.wkhtmltopdf.constains.HtmlToPdfConstants;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Implementação do conversor de html para pdf que se comunica com um serviço
 * rest que gera o pdf e retorna um stream para o pdf.
 * 
 * @author torquato.neto
 *
 */
public class RestfulHtmlToPdfConverter implements HtmlToPdfConverter {

    public static final String ENDPOINT_WS_WKHTMLTOPDF_DEFAULT_VALUE = "http://localhost:8080/wkhtmltopdf-ws";
    private final String endpoint;

    public static RestfulHtmlToPdfConverter createUsingDefaultConfig() {
        String baseURL = SingularProperties.get(HtmlToPdfConstants.ENDPOINT_WS_WKHTMLTOPDF, ENDPOINT_WS_WKHTMLTOPDF_DEFAULT_VALUE);
        LoggerFactory.getLogger(RestfulHtmlToPdfConverter.class).warn("Singular property {} not set! Defaulting to {} ", HtmlToPdfConstants.ENDPOINT_WS_WKHTMLTOPDF, ENDPOINT_WS_WKHTMLTOPDF_DEFAULT_VALUE);
        String context = HtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;
        return new RestfulHtmlToPdfConverter(baseURL + context);
    }

    public RestfulHtmlToPdfConverter(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public Optional<File> convert(HtmlToPdfDTO htmlToPdfDTO) {
        InputStream in = convertStream(htmlToPdfDTO);
        if (in != null) {
            return Optional.ofNullable(createTempFile(in));
        }
        return Optional.empty();
    }
    
    private File createTempFile(InputStream in) {
        
        Path path = null;
        try {
            path = Files.createTempFile(generateFileName(), ".pdf");

            try (OutputStream out = Files.newOutputStream(path)) {
                IOUtils.copy(in, out);
                return path.toFile();
            } catch (IOException ex) {
                getLogger().error("Não foi possivel escrever o arquivo temporario", ex);
            }
        } catch (IOException e) {
            getLogger().error("Não foi possivel criar o arquivo temporario", e);
        }

        return null;
    }
    
    @Override    
    public InputStream convertStream(HtmlToPdfDTO htmlToPdfDTO) {
        if (htmlToPdfDTO != null) {
            ClientHttpResponse response = null;
            try {
                ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                ClientHttpRequest request = requestFactory.createRequest(URI.create(endpoint), HttpMethod.POST);
                request.getHeaders().add("content-type", "application/json");
                ObjectMapper mapper = new ObjectMapper();
                request.getBody().write(mapper.writeValueAsBytes(htmlToPdfDTO));
                response = request.execute();
                return response.getBody();
            } catch (IOException ex) {
                getLogger().error("Problema ao converter HtmlToPdfDTO para pdf", ex);
            }
        }
        return null;
    }
       
    public static String generateFileName() {
        return String.format("singular-ws-html2pdf-%s.pdf",UUID.randomUUID() );
    }
}