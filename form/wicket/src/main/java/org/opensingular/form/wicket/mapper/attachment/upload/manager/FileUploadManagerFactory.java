package org.opensingular.form.wicket.mapper.attachment.upload.manager;

import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

public class FileUploadManagerFactory implements Loggable, Serializable {

    public synchronized FileUploadManager get(HttpSession session) {
        FileUploadManager manager = (FileUploadManager) session.getAttribute(FileUploadManager.SESSION_KEY);
        if (manager == null) {
            manager = new FileUploadManager();
            session.setAttribute(FileUploadManager.SESSION_KEY, manager);
            getLogger().debug("Manager created: SESSION_ID = " + session.getId());
        }
        return manager;
    }

}