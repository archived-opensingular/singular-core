package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.view.SViewAttachmentList;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.AbstractListaMapper;
import br.net.mirante.singular.form.wicket.mapper.MapperCommons;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.panel.SUploadProgressBar;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.upload.SFileUploadField;
import com.google.common.base.Strings;
import org.apache.wicket.Application;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.lang.Bytes;

import java.util.List;
import java.util.Set;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class AttachmentListMapper extends AbstractListaMapper {

    private final static String CLICK_DELEGATE_SCRIPT_TEMPLATE  = "$('#%s').on('click', function(){$('#%s').click();});";
    final static         String MULTIPLE_HIDDEN_UPLOAD_FIELD_ID = "uploadField";


    @Override
    public void buildView(WicketBuildContext ctx) {
/*
        final SIList<SIAttachment> attachments = (SIList<SIAttachment>) ctx.getModel().getObject();

        if (!STypeAttachmentList.class.isAssignableFrom(attachments.getType().getClass())) {
            throw new SingularFormException("O tipo " + attachments.getType() + " não é compativel com AttachmentListMapper.");
        }

        final FileUploadField multipleFileUploadHiddenField =
                buildFileUploadField(ctx.getContainer(),
                        (IModel<SIList<SIAttachment>>) ctx.getModel());

        ctx.getContainer().appendTag("input", true, "type='file' style='display:none' multiple", multipleFileUploadHiddenField);
        ctx.getContainer().appendTag("div",
                buildMetronicPanel(ctx, multipleFileUploadHiddenField));*/

        ctx.getContainer().appendTag("div",
                new FileListUploadPanel("up-list",
                        (IModel<SIList<SIAttachment>>) ctx.getModel()));

    }

    private FileUploadField buildFileUploadField(BSContainer<?> container, IModel<SIList<SIAttachment>> attachments) {

        final FileUploadField uploadField = new SFileUploadField(MULTIPLE_HIDDEN_UPLOAD_FIELD_ID);
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

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.getAjaxCallListeners().add(new AjaxCallListener(){
                    @Override
                    public CharSequence getPrecondition(Component component) {
                        return generateOnchangeValidationJS(
                                component.getApplication(),
                                "$('#"+uploadField.getMarkupId()+"')[0]");
                    }
                });
                attributes.getExtraParameters().put("forceDisableAJAXPageBlock", true);
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("FileUploadPanel.js")));
            }
        });

        return uploadField;
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    private String generateOnchangeValidationJS(Application application, String element) {
        Bytes max = application.getApplicationSettings().getDefaultMaximumUploadSize();
        return "FileUploadPanel.validateInputFile( "+ element +" ,"+max.bytes()+")";
    }


    private MetronicPanel buildMetronicPanel(final WicketBuildContext ctx,
                                             final FileUploadField multipleFileUploadHiddenField) {

        final IModel<SIList<SInstance>> listModel    = $m.get(ctx::getCurrentInstance);
        final SIList<?>                 listInstance = listModel.getObject();
        final IModel<String>            label        = $m.ofValue(trimToEmpty(listInstance.asAtr().getLabel()));

        MetronicPanel panel = new MetronicPanel("metronicPanel") {

            @Override
            protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                heading.appendTag("span", new Label("_title", label));
//                heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(label.getObject()))));

//                if (ctx.getViewMode().isEdition()) {
//                    appendAddButton(heading, multipleFileUploadHiddenField);
//                }

                heading.appendTag("span", new SUploadProgressBar("progress", multipleFileUploadHiddenField){
                    @Override
                    protected Form<?> getForm() {
                        return multipleFileUploadHiddenField.getForm();
                    }
                });

                heading.getApplication().getApplicationSettings().setUploadProgressUpdatesEnabled(true);
            }

            @Override
            protected void buildFooter(BSContainer<?> footer, Form<?> form) {
//                final String markup = "" +
//                        "<button wicket:id=\"_add\" " +
//                        "       class=\"btn btn-add\" type=\"button\" " +
//                        "       title=\"Carregar Arquivo\">" +
//                        "       <i class=\"fa fa-upload\"></i>" +
//                        "           Carregar Arquivo" +
//                        "</button>";
//                final TemplatePanel template = footer.newTemplateTag(tp -> markup);
                if (ctx.getViewMode().isEdition()) {
//                    template.add(appendAddButton(footer, multipleFileUploadHiddenField));
//                    AddButton btnAdd = new AddButton("_add", form, (IModel<SIList<SInstance>>) ctx.getModel());

//                    final WebMarkupContainer btnAdd = new WebMarkupContainer("_add");

//                    template.add(btnAdd);
//                    btnAdd.add($b.onReadyScript(() -> {
//                        return String.format(CLICK_DELEGATE_SCRIPT_TEMPLATE,
//                                template.getMarkupId(true), multipleFileUploadHiddenField.getMarkupId(true));
//                    }));
                    appendAddButton(footer, multipleFileUploadHiddenField);
                }else{
                    footer.setVisible(false);
                }

                footer.add(new ClassAttributeModifier(){
                    protected Set<String> update(Set<String> oldClasses) {
                        oldClasses.remove("text-right");
                        return oldClasses;
                    }
                });
            }

            @Override
            protected void buildContent(BSContainer<?> content, Form<?> form) {

                final TemplatePanel list = content.newTemplateTag(t -> ""
                        + " <div wicket:id='_e' > "
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

        return panel;
    }

    private WebMarkupContainer appendAddButton(BSContainer<?> container, FileUploadField multipleFileUploadHiddenField) {
        final WebMarkupContainer addButton = new WebMarkupContainer("_add");
        container.newTemplateTag(t -> ""
                + "<button type='button' title='Carregar Arquivo'"
                + " wicket:id='_add' class=\"btn btn-add\" >"
                + " <i class=\"fa fa-upload\"></i>"
                + "           Carregar Arquivo"
                + "</button>"
        ).add(addButton);

        addButton.add($b.onReadyScript(() -> {
            return String.format(CLICK_DELEGATE_SCRIPT_TEMPLATE,
                    addButton.getMarkupId(true), multipleFileUploadHiddenField.getMarkupId(true));
        }));
        return addButton;
    }

}