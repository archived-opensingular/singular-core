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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.pdf.HtmlToPdfConverter;
import org.opensingular.ws.wkhtmltopdf.constains.HtmlToPdfConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public InputStream convert2(HtmlToPdfDTO htmlToPdfDTO) {

        String baseURL = SingularProperties.get().getProperty(HtmlToPdfConstants.ENDPOINT_WS_WKHTMLTOPDF);
        String context = HtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;
        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(htmlToPdfDTO);
            MultiValueMap headers = new HttpHeaders();
            headers.add("content-type", "application/json");
            HttpEntity he = new RequestEntity(json, headers, null, null);
            ResponseEntity<Resource> responseEntity = restTemplate.exchange(URI.create(baseURL + context),
                    HttpMethod.POST, he, Resource.class);
            return responseEntity.getBody().getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    

    //TODO nao tem como retornar o stream !!!
    public InputStream convertStream(HtmlToPdfDTO htmlToPdfDTO) {
        if (htmlToPdfDTO != null) {
          
            String baseURL = SingularProperties.get().getProperty(HtmlToPdfConstants.ENDPOINT_WS_WKHTMLTOPDF);
            String context = HtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;
            RestTemplate restTemplate = new RestTemplate();
        
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//            requestFactory.setBufferRequestBody(false);     
//            restTemplate.setRequestFactory(requestFactory);
            
            
            RequestCallback requestCallback = request -> {
                request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                ObjectMapper mapper = new ObjectMapper();
                request.getBody().write(mapper.writeValueAsBytes(htmlToPdfDTO));
            };
        
             ResponseExtractor<Void> responseExtractor = response -> {
                 File file = new File("C:\\Desenv\\temp\\files\\test-"+RandomStringUtils.randomAlphanumeric(16)+".pdf");
                 IOUtils.copy(response.getBody(), new FileOutputStream(file));
                 
                 return null;
             };
             restTemplate.execute(URI.create(baseURL + context), HttpMethod.POST, requestCallback, responseExtractor);
        }
        return null;
    }
    
    
    public InputStream converterHttpClient(HtmlToPdfDTO htmlToPdfDTO){
        
        
        String baseURL = SingularProperties.get().getProperty(HtmlToPdfConstants.ENDPOINT_WS_WKHTMLTOPDF);
        String context = HtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;
        ClientHttpResponse response = null;
        try {
            ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            ClientHttpRequest request = requestFactory.createRequest(URI.create(baseURL+context), HttpMethod.POST);
            request.getHeaders().add("content-type", "application/json");
            ObjectMapper mapper = new ObjectMapper();
            request.getBody().write(mapper.writeValueAsBytes(htmlToPdfDTO));
            response = request.execute();
            return response.getBody();
        } catch (IOException ex) {
            getLogger().error("",ex);
        }
        return null;
    }
    
    public InputStream converterHttpClientApache(HtmlToPdfDTO htmlToPdfDTO) {
        try {
            String baseURL = SingularProperties.get().getProperty(HtmlToPdfConstants.ENDPOINT_WS_WKHTMLTOPDF);
            String context = HtmlToPdfConstants.CONVERT_HTML_TO_PDF_PATH;

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(baseURL + context);
            post.addHeader("content-type", "application/json");
            ObjectMapper mapper = new ObjectMapper();
            post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(htmlToPdfDTO)));
            HttpResponse response = client.execute(post);
            return response.getEntity().getContent();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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