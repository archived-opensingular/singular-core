package org.opensingular.singular.form.wicket.link;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

import java.io.File;

public class FileDownloadLink extends Link<File> {

    private final ContentDisposition contentDisposition;
    private final String             fileName;

    public FileDownloadLink(String id, IModel<File> model, ContentDisposition contentDisposition, String fileName) {
        super(id, model);
        this.contentDisposition = contentDisposition;
        this.fileName = fileName;
    }

    @Override
    public void onClick() {
        final File            file           = getModelObject();
        final IResourceStream resourceStream = new FileResourceStream(new org.apache.wicket.util.file.File(file));

        getRequestCycle().scheduleRequestHandlerAfterCurrent(
                new ResourceStreamRequestHandler(resourceStream) {
                    @Override
                    public void respond(IRequestCycle requestCycle) {
                        super.respond(requestCycle);
                        Files.remove(file);
                    }
                }
                        .setFileName(fileName)
                        .setCacheDuration(Duration.NONE)
                        .setContentDisposition(contentDisposition));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        if (ContentDisposition.INLINE.equals(contentDisposition)) {
            tag.put("target", "_blank");
        }
    }
}
