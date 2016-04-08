package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.AbstractListaMapper;
import br.net.mirante.singular.form.wicket.mapper.MapperCommons;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.*;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.upload.SFileUploadField;
import com.google.common.base.Strings;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;

import java.util.List;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class AttachmentListMapper extends AbstractListaMapper {

    private final static String CLICK_DELEGATE_SCRIPT_TEMPLATE  = "$('#%s').on('click', function(){$('#%s').click();});";
    final static         String MULTIPLE_HIDDEN_UPLOAD_FIELD_ID = "uploadField";


    @Override
    public void buildView(WicketBuildContext ctx) {

        final SIList<SIAttachment> attachments = (SIList<SIAttachment>) ctx.getModel().getObject();

        if (!STypeAttachmentList.class.isAssignableFrom(attachments.getType().getClass())) {
            throw new SingularFormException("O tipo " + attachments.getType() + " não é compativel com AttachmentListMapper.");
        }

        final FileUploadField multipleFileUploadHiddenField = buildFileUploadField(ctx.getContainer(), (IModel<SIList<SIAttachment>>) ctx.getModel());

        ctx.getContainer().appendTag("input", true, "type='file' style='display:none' multiple", multipleFileUploadHiddenField);
        ctx.getContainer().appendTag("div", buildMetronicPanel(ctx, multipleFileUploadHiddenField));

    }

    private FileUploadField buildFileUploadField(BSContainer<?> container, IModel<SIList<SIAttachment>> attachments) {

        final FileUploadField uploadField = new SFileUploadField(MULTIPLE_HIDDEN_UPLOAD_FIELD_ID);
//        uploadField.getForm().setMultiPart(true);
//        uploadField.getForm().setFileMaxSize(Bytes.MAX);
        uploadField.add(new AjaxFormSubmitBehavior("change") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                final List<FileUpload> uploads = uploadField.getFileUploads();
                for (FileUpload upload : uploads) {
                    final SIAttachment attachment = attachments.getObject().addNew();
                    if (upload != null) {
                        attachment.setContent(upload.getBytes());
                        attachment.setFileName(upload.getClientFileName());
                        target.add(container.setOutputMarkupId(true));
                    }
                }
            }
        });

        return uploadField;
    }

    private MetronicPanel buildMetronicPanel(final WicketBuildContext ctx, final FileUploadField multipleFileUploadHiddenField) {

        final IModel<SIList<SInstance>> listModel    = $m.get(ctx::getCurrentInstance);
        final SIList<?>                 listInstance = listModel.getObject();
        final IModel<String>            label        = $m.ofValue(trimToEmpty(listInstance.as(SPackageBasic.aspect()).getLabel()));

        return new MetronicPanel("metronicPanel") {

            @Override
            protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                heading.appendTag("span", new Label("_title", label));
                heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(label.getObject()))));

                if (ctx.getViewMode().isEdition()) {
                    appendAddButton(heading, multipleFileUploadHiddenField);
                }
            }

            @Override
            protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                footer.setVisible(false);
            }

            @Override
            protected void buildContent(BSContainer<?> content, Form<?> form) {

                final TemplatePanel list = content.newTemplateTag(t -> ""
                        + " <div wicket:id='_e'> "
                        + "     <div class='col-md-6' wicket:id='_r'></div> "
                        + " </div> ");

                list.add($b.onConfigure(c -> c.setVisible(!listModel.getObject().isEmpty())));
                list.add(new ElementsView("_e", listModel) {
                    @Override
                    protected void populateItem(Item<SInstance> item) {
                        final BSContainer container = new BSContainer<>("_r");
                        ctx.getUiBuilderWicket().build(ctx.createChild(container, true, item.getModel()), ctx.getViewMode());
                        item.add(container);
                    }
                });

                content.add($b.attrAppender("style", "padding: 15px 15px 10px 15px", ";"));
            }

        };
    }

    private void appendAddButton(BSContainer<?> container, FileUploadField multipleFileUploadHiddenField) {
        final WebMarkupContainer addButton = new WebMarkupContainer("_add");
        container.newTemplateTag(t -> ""
                + "<button type='button' title='Adicionar Arquivo'"
                + " wicket:id='_add' class='btn blue btn-sm pull-right'"
                + " style='" + MapperCommons.BUTTON_STYLE + "'>"
                + " <i style='" + MapperCommons.ICON_STYLE + "' class='" + Icone.PLUS + "'></i>"
                + "</button>"
        ).add(addButton);

        addButton.add($b.onReadyScript(() -> {
            return String.format(CLICK_DELEGATE_SCRIPT_TEMPLATE,
                    addButton.getMarkupId(true), multipleFileUploadHiddenField.getMarkupId(true));
        }));
    }

}