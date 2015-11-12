package br.net.mirante.singular.form.wicket.mapper.attachment;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

@SuppressWarnings({"serial", "rawtypes"})
class AttachmentContainer extends BSContainer {
    public static String PARAM_NAME = "FILE-UPLOAD";
    private UploadBehavior uploader;
    private FormComponent fileField, nameField, hashField, sizeField, idField;

    public AttachmentContainer(IModel<? extends MInstancia> model) {
        super("_attachment_" + model.getObject().getNome());
        MInstancia instance = model.getObject();
        setupFields(model);
        this.add(this.uploader = new UploadBehavior(instance));

        setup(field(), model);
    }

    @SuppressWarnings("unchecked")
    protected FormComponent setupFields(IModel<? extends MInstancia> model) {
        String name = model.getObject().getNome();
        fileField = new FileUploadField(name,
                new IMInstanciaAwareModel() {
                    public Object getObject() {
                        return null;
                    }

                    public void setObject(Object object) {
                    }

                    public void detach() {
                    }

                    public MInstancia getMInstancia() {
                        return model.getObject().getMTipo().novaInstancia();
                    }
                });
        nameField = new HiddenField("file_name_" + name,
                new PropertyModel<>(model, "fileName"));
        hashField = new HiddenField("file_hash_" + name,
                new PropertyModel<>(model, "fileHashSHA1"));
        sizeField = new HiddenField("file_size_" + name,
                new PropertyModel<>(model, "fileSize"));
        idField = new HiddenField("file_id_" + name,
                new PropertyModel<>(model, "fileId"));
        return field();
    }

    protected FormComponent field() {
        return fileField;
    }

    public void setup(FormComponent field, IModel<? extends MInstancia> model) {
        String fieldId = field.getMarkupId();

        appendTag("span", true, "class='btn btn-success fileinput-button'",
                appendInputButton(field));
        appendTag("div", true, "class='progress' id='progress_" + fieldId + "'",
                createProgressBar(field));
        appendTag("div", true, "class='files' id='files_" + fieldId + "'",
                new Label("_", new PropertyModel(model, "fileName")));
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
                        "type='file' id='" + fileField.getMarkupId() + "'", fileField);

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
                                + "    url: '" + uploader.getUrl() + "',  "
                                + "    paramName: '" + PARAM_NAME + "',  "
                                + "    singleFileUploads: true,  "
                                + "    dataType: 'json',  "
                                + "    start: function (e, data) {  "
                                + "        $('#files_" + fieldId + "').html('');"
                                + "        $('#progress_" + fieldId + " .progress-bar').css('width','0%')"
                                + "    },"
                                + "    done: function (e, data) {  "
                                + "        console.log(e,data);    "
                                + "        $.each(data.result.files, function (index, file) {  "
                                + "            $('<p/>').text(file.name).appendTo('#files_" + fieldId + "'); "
                                + "            $('#" + nameField.getMarkupId() + "').val(file.name);"
                                + "            $('#" + idField.getMarkupId() + "').val(file.fileId);"
                                + "            $('#" + hashField.getMarkupId() + "').val(file.hashSHA1);"
                                + "            $('#" + sizeField.getMarkupId() + "').val(file.size);"
                                + "        });  "
                                + "    },  "
                                + "    progressall: function (e, data) {  "
                                + "        var progress = parseInt(data.loaded / data.total * 100, 10); "
                                + "        $('#progress_" + fieldId + " .progress-bar').css( 'width', "
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

    private Label emptyLabel() {
        return new Label("_", Model.of(""));
    }
}
