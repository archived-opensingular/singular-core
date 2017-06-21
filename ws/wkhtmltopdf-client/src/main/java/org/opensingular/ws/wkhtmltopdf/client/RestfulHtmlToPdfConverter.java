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


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.lib.commons.pdf.HtmlToPdfConverter;
import org.opensingular.ws.wkhtmltopdf.constains.HtmlToPdfConstants;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementação do conversor de html para pdf que se comunica com um serviço
 * rest que gera o pdf e retorna um stream para o pdf.
 * 
 * @author torquato.neto
 *
 */
public class RestfulHtmlToPdfConverter implements HtmlToPdfConverter {

    private final String endpoint;

    public static RestfulHtmlToPdfConverter createUsingDefaultConfig() {
        String baseURL = SingularProperties.get().getProperty(HtmlToPdfConstants.ENDPOINT_WS_WKHTMLTOPDF);
        String context = HtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;
        return new RestfulHtmlToPdfConverter(baseURL + context);
    }

    public RestfulHtmlToPdfConverter(String endpoint) {
        this.endpoint = endpoint;
    }

    public InputStream convert(HtmlToPdfDTO htmlToPdfDTO) {
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