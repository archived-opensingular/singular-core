package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nullable;

public class SingularUploadException extends SingularException {

    private String fileName;

    public SingularUploadException(@Nullable String fileName, @Nullable String msg) {
        super(msg);
        this.fileName = fileName;
    }

    public SingularUploadException(@Nullable String fileName, @Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
