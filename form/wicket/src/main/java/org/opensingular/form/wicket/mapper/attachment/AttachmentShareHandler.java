package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.SharedResourceReference;

import java.util.Date;

public class AttachmentShareHandler {

    private static final String DOWNLOAD_PATH = "/download";

    private final String id;
    private final String url;
    private final SharedResourceReference ref;
    private final WebApplication webApplication;

    public AttachmentShareHandler(String id, WebApplication webApplication) {
        this.id = id;
        this.url = DOWNLOAD_PATH + "/" + id + "/" + new Date().getTime();
        this.ref = new SharedResourceReference(String.valueOf(id));
        this.webApplication = webApplication;
    }

    public String share(AttachmentResource resource) {
        resource.setAttachmentSharedHandler(this);
        webApplication.getSharedResources().add(String.valueOf(id), resource);
        webApplication.mountResource(url, ref);
        String path = webApplication.getServletContext().getContextPath() + "/" + webApplication.getWicketFilter().getFilterPath() + url;
        return path.replaceAll("\\*", "").replaceAll("//", "/");
    }

    public void unShare() {
        webApplication.unmount(url);
        webApplication.getSharedResources().remove(ref.getKey());
    }

    public String getId() {
        return id;
    }
}
