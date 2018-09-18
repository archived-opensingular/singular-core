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

package org.opensingular.lib.commons.dto;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HtmlToPdfDTO implements Serializable {

    private String header;
    private String body;
    private String footer;
    private List<String> additionalParams;

    public HtmlToPdfDTO() {

    }

    public HtmlToPdfDTO(String header, String body, String footer) {
        this(header, body, footer, true);
    }

    public HtmlToPdfDTO(String body) {
        this(null, body, null, true);
    }

    public HtmlToPdfDTO(String header, String body, String footer, boolean defaultParam) {
        this.header = header;
        this.body = body;
        this.footer = footer;
        if (defaultParam) {
            addParam("--print-media-type");
            addParam("--load-error-handling");
            addParam("ignore");
        }
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public List<String> getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(List<String> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public String getAll(){
        return StringUtils.defaultString(getHeader()) + StringUtils.defaultString(getBody()) + StringUtils.defaultString(getFooter());
    }

    public void addParam(String param) {
        if (additionalParams == null) {
            additionalParams = new ArrayList<>();
        }
        additionalParams.add(param);
    }

}