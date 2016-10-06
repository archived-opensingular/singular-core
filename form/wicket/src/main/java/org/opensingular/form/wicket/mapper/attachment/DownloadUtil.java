package org.opensingular.form.wicket.mapper.attachment;

import org.opensingular.singular.commons.base.SingularUtil;
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
