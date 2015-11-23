package br.net.mirante.singular.form.wicket.mapper.attachment;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.SDocument;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

/**
 * AttachmentContainer is the class responsible for rendering a upload field
 *         using the jquery-file-upload javascript plugin. Even though it creates
 *         a file input it is not used by the singular-form to submit the file
 *         information to the {@link MInstancia} representing it. Instead, it
 *         populates the instance with a composite type containing the file
 *         descriptor.
 *         The workings of this component is as follows:
 *
 *         1 - Whem a file is uploaded it uses the UploadBehaviour to call the
 *                 {@link IAttachmentPersistenceHandler} registered in the
 *                 {@link SDocument#getAttachmentPersistenceHandler()}. It has a
 *                 default handler, but you can personalize as desired by using
 *                 the {@link SDocument#setAttachmentPersistenceHandler(br.net.mirante.singular.form.mform.ServiceRef)}
 *                 register method.
 *         2 - The information returne by the persistence handler is stored in the
 *                 file field as its descriptor. Using the handler is possible
 *                 to retrieve the proper information about the file.
 *
 *         Since only the descriptor is stored in the Instance, it's advised to
 *         use different handlers for the upload (default) and submission
 *         (persistent) of the form.
 *
 *         OBS: Remember that each {@link MInstancia} has its own {@link SDocument}
 *                 making each handler configuration unique for its own instance.
 *
 * @author Fabricio Buzeto
 *
 */
@SuppressWarnings({"serial", "rawtypes"})
class AttachmentContainer extends BSContainer {
    public static String PARAM_NAME = "FILE-UPLOAD";
    private UploadBehavior uploader;
    private DownloadBehaviour downloader;
    private FormComponent fileField, nameField, hashField, sizeField, idField;

    public AttachmentContainer(IModel<? extends MIAttachment> model) {
        super("_attachment_" + model.getObject().getNome());
        setupFields(model);
        this.add(this.uploader = new UploadBehavior(model.getObject()));
        this.add(this.downloader = new DownloadBehaviour(model.getObject()));
        setup(field(), model);
    }

    @SuppressWarnings("unchecked")
    protected FormComponent setupFields(IModel<? extends MInstancia> model) {
        String name = model.getObject().getNome();
        fileField = new FileUploadField(name, new IMInstanciaAwareModel() {
            public Object getObject() {return null;}

            public void setObject(Object object) {}

            public void detach() {}

            public MInstancia getMInstancia() {
                return model.getObject().getMTipo().novaInstancia();
            }
        });
        nameField = new HiddenField("file_name_"+name,
                        new PropertyModel<>(model, "fileName"));
        hashField = new HiddenField("file_hash_"+name,
                            new PropertyModel<>(model, "fileHashSHA1"));
        sizeField = new HiddenField("file_size_"+name,
                            new PropertyModel<>(model, "fileSize"));
        idField = new HiddenField("file_id_"+name,
                            new PropertyModel<>(model, "fileId"));
        return field();
    }

    protected FormComponent field(){
        return fileField;
    }

    public void setup(FormComponent field, IModel<? extends MInstancia> model) {
        String fieldId = field.getMarkupId();

        appendTag("span", true, "class='btn btn-success fileinput-button'",
                appendInputButton(field));
        appendTag("div", true, "class='progress' id='progress_" + fieldId + "'",
                createProgressBar(field));

        appendTag("div", true, "class='files' id='files_" + fieldId + "'",
                createDownloadLink(model));

        appendTag("input", true, "type='hidden' id='" + nameField.getMarkupId() + "'",
                nameField);
        appendTag("input", true, "type='hidden' id='" + hashField.getMarkupId() + "'",
                hashField);
        appendTag("input", true, "type='hidden' id='" + idField.getMarkupId() + "'",
                idField);
        appendTag("input", true, "type='hidden' id='" + sizeField.getMarkupId() + "'",
                sizeField);
    }

    private BSContainer appendInputButton(FormComponent field) {
        BSContainer buttonContainer = new BSContainer<>("_bt_" + field.getId())
                .appendTag("span", new Label("_", Model.of("Selecionar ...")))
                .appendTag("input", true,
                "type='file' id='" + fileField.getMarkupId() + "'",fileField);

        appendScriptContainer(field.getMarkupId(), buttonContainer);
        return buttonContainer;
    }

    @SuppressWarnings({"unchecked"})
    private void appendScriptContainer(String fieldId, BSContainer buttonContainer) {
        TemplatePanel scriptContainer = (TemplatePanel)
                buttonContainer.newComponent(id -> new TemplatePanel(id,
                    () -> "<script > "
                            + "$(function () {"
                            + "  $('#" + fileField.getMarkupId()
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
                            + "            $('<p/>').append($('<a />').attr('href','"+downloader.getUrl()+"&fileId='+file.fileId+'&fileName='+file.name).text(file.name)).appendTo('#files_"+ fieldId+"'); "
                            + "            $('#" + nameField.getMarkupId()+ "').val(file.name);"
                            + "            $('#" + idField.getMarkupId()+ "').val(file.fileId);"
                            + "            $('#" + hashField.getMarkupId()+ "').val(file.hashSHA1);"
                            + "            $('#" + sizeField.getMarkupId()+ "').val(file.size);"
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
                            + " </script>\n"));
        scriptContainer.setRenderBodyOnly(true);
    }

    private BSContainer createProgressBar(FormComponent field) {
        BSContainer progressContainer = new BSContainer<>("_progress_" + field.getId());
        progressContainer.appendTag("div", true, "class='progress-bar progress-bar-success'", emptyLabel());
        return progressContainer;
    }

    private final class DownloadLink extends AjaxFallbackLink<Object> {
        private DownloadLink(String id, IModel<Object> model) {
            super(id, model);
            setBody(model);
        }

        protected CharSequence getURL() {
            return downloader.getUrl();
        }

        public void onClick(AjaxRequestTarget target) {}
    }

    @SuppressWarnings("unchecked")
    private WebMarkupContainer createDownloadLink(IModel<? extends MInstancia> model) {
        Link<Object> link = new DownloadLink("_", new PropertyModel(model, "fileName"));

        BSContainer wrapper = new BSContainer<>("_");
        wrapper.appendTag("a", true, "", link);
        return wrapper;
    }

    private Label emptyLabel() {
        return new Label("_", Model.of(""));
    }
}
