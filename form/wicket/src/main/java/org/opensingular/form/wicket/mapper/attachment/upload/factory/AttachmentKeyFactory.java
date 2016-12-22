package org.opensingular.form.wicket.mapper.attachment.upload.factory;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class AttachmentKeyFactory {

    public AttachmentKey get() {
        return new AttachmentKey(UUID.randomUUID().toString());
    }

    public AttachmentKey get(HttpServletRequest req) throws IOException {
        String rawKey = substringAfterLast(defaultString(req.getPathTranslated()), File.separator);
        if(!StringUtils.isBlank(rawKey)){
            return new AttachmentKey(rawKey);
        }
        return null;
    }

}