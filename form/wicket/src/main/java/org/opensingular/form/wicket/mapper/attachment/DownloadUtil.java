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

package org.opensingular.form.wicket.mapper.attachment;

import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.request.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DownloadUtil {

    public static void writeJSONtoResponse(SIAttachment attachment, Response response) {
        writeJSONtoResponse(toJSON(attachment), (HttpServletResponse) response.getContainerResponse());
    }

    public static void writeJSONtoResponse(SIAttachment attachment, HttpServletResponse response) {
        writeJSONtoResponse(toJSON(attachment), response);
    }

    public static void writeJSONtoResponse(Object json, HttpServletResponse response) {
        response.setContentType("application/json");
        try {
            PrintWriter writer = response.getWriter();
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

    public static JSONObject toJSON(SIAttachment attachment) {
        return toJSON(attachment.getFileId(), attachment.getFileHashSHA1(), attachment.getFileName(), attachment.getFileSize());
    }

    public static JSONObject toJSON(String id, String hash, String name, long size) {
        JSONObject jsonFile = new JSONObject();
        jsonFile.put("name", name);
        jsonFile.put("fileId", id);
        jsonFile.put("hashSHA1", hash);
        jsonFile.put("size", size);
        return jsonFile;
    }
}
