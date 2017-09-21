package org.opensingular.form.wicket.mapper.attachment.upload.info;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.lib.commons.base.SingularUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

public class UploadResponseInfo implements Serializable {

    public static final String ARQUIVO_NAO_PODE_SER_DE_TAMANHO_0_ZERO = "Arquivo não pode ser de tamanho 0 (zero)";
    public static final String TIPO_DE_ARQUIVO_NAO_PERMITIDO          = "Tipo de arquivo não permitido. <BR> Permitido: ";

    private final AttachmentKey fileId;
    private final String        name;
    private final long          size;
    private final String        hashSHA1;
    private final String        errorMessage;

    public UploadResponseInfo(IAttachmentRef attachmentRef) {
        this(attachmentRef.getId(), attachmentRef.getName(), attachmentRef.getSize(), attachmentRef.getHashSHA1());
    }

    public UploadResponseInfo(SIAttachment attachment) {
        this(attachment.getFileId(), attachment.getFileName(), attachment.getFileSize(), attachment.getFileHashSHA1());
    }

    private UploadResponseInfo(String fileId, String name, long size, String hashSHA1) {
        this.fileId = new AttachmentKey(fileId);
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

    private JSONObject toJson() {
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

    public AttachmentKey getFileId() {
        return fileId;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getHashSHA1() {
        return hashSHA1;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
