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


import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.pdf.HtmlToPdfConverter;
import org.opensingular.ws.wkhtmltopdf.constains.HtmlToPdfConstants;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

public class RestfulHtmlToPdfConverter implements HtmlToPdfConverter {

    private final String endpoint;
    private final ISupplier<RestTemplate> restTemplateSupplier;

    public static RestfulHtmlToPdfConverter createUsingDefaultConfig() {
        String baseURL = SingularProperties.get().getProperty(HtmlToPdfConstants.ENDPOINT_WS_WKHTMLTOPDF);
        String context = HtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;
        return new RestfulHtmlToPdfConverter(baseURL + context, RestfulHtmlToPdfConverter::defaultRestTemplate);
    }

    public RestfulHtmlToPdfConverter(String endpoint, ISupplier<RestTemplate> restTemplateSupplier) {
        this.endpoint = endpoint;
        this.restTemplateSupplier = restTemplateSupplier;
    }

    @Override
    public Optional<File> convert(HtmlToPdfDTO htmlToPdfDTO) {
        if (htmlToPdfDTO != null) {
            final byte[] response = restTemplateSupplier.get().postForObject(endpoint, htmlToPdfDTO, byte[].class);
            if (response != null) {
                return Optional.ofNullable(convertByteArrayToFile(response));
            }
        }
        return Optional.empty();
    }

    private File convertByteArrayToFile(byte[] bytes) {
        try {
            return Files.write(Files.createTempFile(generateFileName(), ".pdf"), bytes).toFile();
        } catch (IOException ex) {
            getLogger().error("Não foi possivel escrever o arquivo temporario", ex);
            return null;
        }
    }

    private String generateFileName() {
        return "singular-ws-html2pdf" + UUID.randomUUID();
    }

    private static RestTemplate defaultRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        return restTemplate;
    }

}