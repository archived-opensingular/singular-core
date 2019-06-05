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

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.wicket.ajax.json.JSONArray;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.lib.commons.base.SingularUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

public class UploadResponseWriter implements Serializable {

    public static final String APPLICATION_JSON = "application/json;charset=utf-8";

    public void writeJsonObjectResponseTo(HttpServletResponse response, UploadResponseInfo uploadResponseInfo) {
        doWrite(response, uploadResponseInfo.toString());
    }

    public void writeJsonArrayResponseTo(HttpServletResponse response, List<UploadResponseInfo> list) {
        JSONArray array = new JSONArray();
        for (UploadResponseInfo r : list) {
            array.put(r);
        }
        doWrite(response, array.toString());
    }

    private void doWrite(HttpServletResponse response, String s) {
        response.setContentType(APPLICATION_JSON);
        try {
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

}
