package br.net.mirante.singular.form.wicket.mapper.attachment;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

@SuppressWarnings({ "serial", "rawtypes" })
class AttachmentContainer extends BSContainer {
    public static String PARAM_NAME = "FILE-UPLOAD";
    private UploadBehavior uploader;

    public AttachmentContainer(String id, FileUploadField field, MInstancia instance) {
	super(id);
	this.add( this.uploader = new UploadBehavior(instance));
	setup(field);
    }
	
    
    
    public void setup(FileUploadField field) {
	String fieldId = field.getMarkupId();

	appendTag("span", true, "class='btn btn-success fileinput-button'", 
		appendInputButton(field));
	appendTag("div", true, "class='progress' id='progress_" + fieldId + "'", 
		createProgressBar(field));
	appendTag("div", true, "class='files' id='files_" + fieldId + "'", 
		emptyLabel());
	appendTag("input", true, "type='hidden' id='data_" + fieldId + "'", 
		emptyLabel());
	
	
    }

    private BSContainer appendInputButton(FileUploadField field) {
	BSContainer buttonContainer = new BSContainer<>("_bt_" + field.getId())
		.appendTag("span", new Label("_", Model.of("Selecionar ...")))
		.appendTag("input", true, "type='file' id='" + field.getMarkupId() + "'", field);

	appendScriptContainer(field.getMarkupId(), buttonContainer);
	return buttonContainer;
    }

    @SuppressWarnings({"unchecked"})
    private void appendScriptContainer(String fieldId, BSContainer buttonContainer) {
	    TemplatePanel scriptContainer = (TemplatePanel) 
		    buttonContainer.newComponent(id -> new TemplatePanel(id,
			() -> {
			    return "<script > " 
			    	+ "$(function () {" 
			    	+ "  $('#" + fieldId 
			    	+ "').fileupload({  "
			    	+ "    url: '"+uploader.getUrl()+"',  " 
			    	+ "    paramName: '"+PARAM_NAME+"',  " 
			    	+ "    singleFileUploads: true,  " 
			    	+ "    dataType: 'json',  "
			    	+ "    start: function (e, data) {  "
			    	+ "        $('#files_"+ fieldId+"').html('');"
			    	+ "        $('#progress_"+ fieldId+" .progress-bar').css('width','0%')"
			    	+ "    },"
			    	+ "    done: function (e, data) {  "
			    	+ "        console.log(e,data);    "
			    	+ "        $.each(data.result.files, function (index, file) {  "
			    	+ "            $('<p/>').text(file.name).appendTo('#files_"+ fieldId+"'); "
			    	+ "            $('#data_" + fieldId+ "').val(JSON.stringify(file));"
			    	+ "        });  " 
			    	+ "    },  " 
			    	+ "    progressall: function (e, data) {  "
			    	+ "        var progress = parseInt(data.loaded / data.total * 100, 10); "
			    	+ "        $('#progress_"+ fieldId+" .progress-bar').css( 'width', "
			    	+ "                        progress + '%' ); "
			    	+ "    }  " 
			    	+ "  }).prop('disabled', !$.support.fileInput)  "
			    	+ "    .parent().addClass($.support.fileInput ? undefined : 'disabled');  " 
			    	+ "});"
			    	+ " </script>\n";
			}));
		scriptContainer.setRenderBodyOnly(true);
	}

	private BSContainer createProgressBar(FileUploadField field) {
	    BSContainer progressContainer = new BSContainer<>("_progress_" + field.getId());
	    progressContainer.appendTag("div", true, "class='progress-bar progress-bar-success'",emptyLabel());
	    return progressContainer;
	}

	private Label emptyLabel() {
	    return new Label("_", Model.of(""));
	}
	
}