package org.opensingular.form.wicket.mapper.attachment;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.lib.commons.base.SingularUtil;

public class UploadResponseInfo implements Serializable {
    public final UUID   fileId;
    public final String name;
    public final long   size;
    public final String hashSHA1;
    public final String errorMessage;
    public UploadResponseInfo(FileUploadInfo fileUploadInfo) {
        this(
            fileUploadInfo.fileId,
            fileUploadInfo.name,
            fileUploadInfo.size,
            fileUploadInfo.hash);
    }
    public UploadResponseInfo(SIAttachment attachment) {
        this(
            UUID.fromString(attachment.getFileId()),
            attachment.getFileName(),
            attachment.getFileSize(),
            attachment.getFileHashSHA1());
    }
    public UploadResponseInfo(UUID fileId, String name, long size, String hashSHA1) {
        this.fileId = fileId;
        this.name = name;
        this.size = size;
        this.hashSHA1 = hashSHA1;
        this.errorMessage = null;
    }
    public UploadResponseInfo(String name, String errorMessage) {
        this.fileId = null;
        this.name = name;
        this.size = 0L;
        this.hashSHA1 = null;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
    public JSONObject toJson() {
        JSONObject jsonFile = new JSONObject();
        if (errorMessage != null) {
            jsonFile.put("name", name);
            jsonFile.put("errorMessage", errorMessage);
        } else {
            jsonFile.put("fileId", fileId);
            jsonFile.put("name", name);
            jsonFile.put("size", size);
            jsonFile.put("hashSHA1", hashSHA1);
        }
        return jsonFile;
    }

    public void writeJsonObjectResponseTo(HttpServletResponse response) {
        doWrite(response, this.toString());
    }
    public static void writeJsonArrayResponseTo(HttpServletResponse response, List<UploadResponseInfo> list) {
        JSONArray array = new JSONArray();
        for (UploadResponseInfo r : list)
            array.put(r);
        doWrite(response, array.toString());
    }

    private static void doWrite(HttpServletResponse response, String s) {
        response.setContentType("application/json");
        try {
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }
}
