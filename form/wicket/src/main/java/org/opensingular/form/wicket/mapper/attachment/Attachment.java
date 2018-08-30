package org.opensingular.form.wicket.mapper.attachment;

import java.io.Serializable;

import org.apache.wicket.request.resource.ContentDisposition;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

public class Attachment implements Serializable {

    final String filename;
    final ContentDisposition contentDisposition;
    final IAttachmentRef attachmentRef;

    public Attachment(String filename, ContentDisposition contentDisposition, IAttachmentRef attachmentRef) {
        this.filename = filename;
        this.contentDisposition = contentDisposition;
        this.attachmentRef = attachmentRef;
    }

}
