/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.attachment;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.util.Loggable;

/**
 * Shared Resource bound to application.
 * <p>
 * This shared file will be deleted after the first call.
 * This shared file should be used for the Google Map Component.
 * This class just accept the google api calls, if it's not will return a FORBIDDEN.
 * <p>
 *
 * @see DownloadSupportedBehavior
 */
public class AttachmentPublicMapperResource extends AttachmentPublicResource implements Loggable {

    private Map<String, Attachment> attachments = new HashMap<>();
    public static final String APPLICATION_MAP_KEY = "public/map";

    public AttachmentPublicMapperResource() { /*Blank constructor*/}

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse resourceResponse = new ResourceResponse();
        String userAgent = ((HttpServletRequest) attributes.getRequest().getContainerRequest()).getHeader("User-Agent");
        //Verify if it's google calling the server, if it's not will send a FORBIDDEN.
        if (!userAgent.contains("google.com")) {
            return resourceResponse.setStatusCode(HttpServletResponse.SC_FORBIDDEN);
        }
        StringValue attachmentKey = attributes.getParameters().get("attachmentKey");
        System.out.println(attachmentKey);
        if (attachmentKey.isNull() || attachmentKey.isEmpty()) {
            return resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }
        Attachment attachment = attachments.get(attachmentKey.toString());
        if (attachment == null) {
            return resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }
        IAttachmentRef attachmentRef = attachment.attachmentRef;
        if (attachmentRef.getSize() > 0) {
            resourceResponse.setContentLength(attachmentRef.getSize());
        }

        resourceResponse.setFileName(attachment.filename);

        try {
            resourceResponse.setContentDisposition(attachment.contentDisposition);
            String attachmentContent = configureAttachmentContent(attachmentRef);
            resourceResponse.setContentType(attachmentContent);
            resourceResponse.setWriteCallback(new AttachmentResourceWriteCallback(resourceResponse, attachmentRef));
            //Remove the file exactly after read.
            attachments.remove(attachmentKey.toString());
        } catch (Exception e) {
            getLogger().error("Erro ao recuperar arquivo.", e);
            resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }

        return resourceResponse;
    }

    /**
     * Configure the header of Attachment content.
     *
     * @param attachmentRef The Attachment that contains the content.
     * @return The content of attachment configured.
     */
    private String configureAttachmentContent(IAttachmentRef attachmentRef) {
        String attachmentContent;
        if (!attachmentRef.getContentType().contains("<?xml")) {
            attachmentContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + attachmentRef.getContentType();
        } else {
            attachmentContent = attachmentRef.getContentType();
        }
        return attachmentContent;
    }

    /**
     * @param name        the file name
     * @param disposition the disposition
     * @param ref         the reference
     * @return the URL for download
     */
    @Override
    public String addAttachment(String name, ContentDisposition disposition, IAttachmentRef ref) {
        WebApplication app = WebApplication.get();
        attachments.put(ref.getId(), new Attachment(name, disposition, ref));
        String path = app.getServletContext().getContextPath() + '/' + app.getWicketFilter().getFilterPath() + getDownloadURL(ref.getId());
        return path.replaceAll("\\*", "").replaceAll("//", "/");
    }

    public static String getDownloadURL(String path) {
        return  '/' + APPLICATION_MAP_KEY + "/download/" + path;
    }

    public static String getMountPathPublic() {
        return getDownloadURL("${attachmentKey}");
    }

    /**
     * Create a public file that can be used by other application or session.
     *
     * @param nameFile          The name of file.
     * @param attachmentRef  The Attachment reference.
     * @return return a public url for the file.
     */
    public static String createTempPublicMapFile(String nameFile, IAttachmentRef attachmentRef) {
        AttachmentPublicMapperResource attachmentResource;
        if (WebApplication.get().getSharedResources().get(APPLICATION_MAP_KEY) == null) {
            WebApplication.get().mountResource(getMountPathPublic(), new SharedResourceReference(APPLICATION_MAP_KEY));
            attachmentResource = new AttachmentPublicMapperResource();
            WebApplication.get().getSharedResources().add(APPLICATION_MAP_KEY, attachmentResource);
        } else {
            attachmentResource = (AttachmentPublicMapperResource) WebApplication.get().getSharedResources().get(APPLICATION_MAP_KEY).getResource();
        }

        return attachmentResource.addAttachment(nameFile, ContentDisposition.INLINE, attachmentRef);
    }


}