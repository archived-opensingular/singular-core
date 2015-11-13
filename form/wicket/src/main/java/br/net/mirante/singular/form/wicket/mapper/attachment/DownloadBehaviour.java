package br.net.mirante.singular.form.wicket.mapper.attachment;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;

//TODO: Test when we have a persistent and a temporary one
@SuppressWarnings("serial")
public class DownloadBehaviour extends Behavior implements IResourceListener {
    transient protected WebWrapper w = new WebWrapper(); 
    private Component component;
    transient private MInstancia instance;

    public DownloadBehaviour(MInstancia instance) {
	this.instance = instance;
    }
    
    public void setWebWrapper(WebWrapper w) {
	this.w = w;
    }
    
    @Override
    public void bind(Component component) {
	this.component = component;
    }

    @Override
    public void onResourceRequested() {
	try {
	    MIAttachment attachment = (MIAttachment) instance;
	    IAttachmentPersistenceHandler handler = instance.getDocument().getAttachmentPersistenceHandler();

	    ServletWebRequest request = w.request();
	    StringValue id = request.getRequestParameters().getParameterValue("fileId");
	    StringValue name = request.getRequestParameters().getParameterValue("fileName");
	    String fileId;
	    String fileName;
	    if(id.isEmpty() || name.isEmpty()){
        	fileId = attachment.getFileId();
        	fileName = attachment.getFileName();
	    }else{
		fileId = id.toString();
		fileName = name.toString();
	    }
	    IAttachmentRef data = handler.getAttachment(fileId);
	    WebResponse response = (WebResponse) w.response();
	    response.addHeader("Content-Type", "application/octet-stream");
	    response.addHeader("Content-disposition", "attachment; filename="+ fileName);
	    response.getOutputStream().write(data.getContentAsByteArray());
	} catch (IOException e) {
	    throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

    }
    
    public String getUrl() {
	return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }
}