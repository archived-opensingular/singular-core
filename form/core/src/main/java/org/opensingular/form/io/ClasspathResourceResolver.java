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

package org.opensingular.form.io;


import org.apache.xerces.dom.DOMInputImpl;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ClasspathResourceResolver implements LSResourceResolver, Loggable {


    private String localPath;

    public ClasspathResourceResolver(String localPath) {
        this.localPath = localPath;
    }

    public ClasspathResourceResolver() {
        this.localPath = "xsd/";
    }


    private InputStream getSchemaAsStream(String systemId, String baseUri, String localPath) {
        InputStream in = getSchemaFromClasspath(systemId, localPath);
        return in == null ? getSchemaFromWeb(baseUri, systemId) : in;
    }

    private InputStream getSchemaFromClasspath(String systemId, String localPath) {
        String file = systemId
                .replaceAll("\\/", "_")
                .replaceAll("\\\\", "_")
                .replaceAll(":", "_");
        getLogger().info("Try to get definitions from classpath: {}{}", localPath, file);
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(localPath + file);
    }

    /*
     * You can leave out the webstuff if you are sure that everything is
     * available on your machine
     */
    private InputStream getSchemaFromWeb(String baseUri, String systemId) {
        try {
            URI uri = new URI(systemId);
            if (uri.isAbsolute()) {
                getLogger().info("Get definitions from web: {} ",systemId);
                return urlToInputStream(uri.toURL(), "text/xml");
            }
            getLogger().info("Get definitions from web: Host: {} Path: {} ", baseUri, systemId);
            return getSchemaRelativeToBaseUri(baseUri, systemId);
        } catch (Exception e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    private InputStream urlToInputStream(URL url, String accept) {
        HttpURLConnection con         = null;
        InputStream       inputStream = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(15000);
            con.setRequestProperty("User-Agent", "Name of my application.");
            con.setReadTimeout(15000);
            con.setRequestProperty("Accept", accept);
            con.connect();
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == 307
                    || responseCode == 303) {
                String redirectUrl = con.getHeaderField("Location");
                try {
                    URL newUrl = new URL(redirectUrl);
                    return urlToInputStream(newUrl, accept);
                } catch (MalformedURLException e) {
                    URL newUrl = new URL(url.getProtocol() + "://" + url.getHost() + redirectUrl);
                    return urlToInputStream(newUrl, accept);
                }
            }
            inputStream = con.getInputStream();
            return inputStream;
        } catch (Exception e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    private InputStream getSchemaRelativeToBaseUri(String baseUri, String systemId) {
        try {
            URL url = new URL(baseUri + systemId);
            return urlToInputStream(url, "text/xml");
        } catch (Exception e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        LSInput input = new DOMInputImpl();
        input.setPublicId(publicId);
        input.setSystemId(systemId);
        input.setBaseURI(baseURI);
        input.setCharacterStream(new InputStreamReader(
                getSchemaAsStream(input.getSystemId(), input.getBaseURI(), localPath)));
        return input;
    }
}