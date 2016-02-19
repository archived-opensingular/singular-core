package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static com.google.common.collect.Sets.newHashSet;

import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
        import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.model.AbstractSInstanceModel;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the visual component of an annotated field on screen.
 *
 * @author Fabricio Buzeto
 */
public class AnnotationComponent extends Panel {
    private final AbstractSInstanceModel referenced;
    private Component referencedComponent;
    private final WicketBuildContext context;
    private MInstanciaValorModel textModel, approvedModel;
    private MInstanceRootModel model;
    private Label comment_field, approval_field;
    private boolean keepOpened = false;
    private ActionAjaxButton openModalButton;

    public AnnotationComponent(String id, AbstractSInstanceModel referenced,
                                WicketBuildContext context) {
        super(id);
        this.referenced = referenced;
        this.context = context;
        createModels(referenced);
    }

    public AbstractSInstanceModel referenced() {    return referenced;  }

    public void setReferencedComponent(Component referencedComponent) {
        this.referencedComponent = referencedComponent;
    }

    private void createModels(AbstractSInstanceModel referenced) {
        final SIAnnotation target = annotated(referenced).annotation();
        target.setTargetId(referenced.getMInstancia().getId());
        model = new MInstanceRootModel(target);
        setDefaultModel(model);
        createSubModels();
    }

    private void createSubModels() {
        textModel = new MInstanciaValorModel(new SInstanceCampoModel<>(model,"text"));
        approvedModel = new MInstanciaValorModel(new SInstanceCampoModel<>(model,"isApproved"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.queue(new Label("target_label",$m.ofValue(title(referenced))));
        this.queue(comment_field = createCommentSnippet());
        this.queue(approval_field = createApprovalLabel(approvedModel));
        this.queue(openModalButton = createOpenModalButton(createEditModal()));
        this.queue(createDeleteModalButton());
    }

    private Label createCommentSnippet() {
        return new Label("comment_field", new Model(){
            public Serializable getObject() {
                if(textModel.getObject() == null){  return "";  }
                String text = (String) textModel.getObject();
                if(text.length() > 100){    return text.substring(0,100) + " [...]";   }
                return text;
            }
        });
    }

    private static Label createApprovalLabel(final MInstanciaValorModel model) {
        return new Label("approval_field", new Model(){
            public Serializable getObject() {
                if(Boolean.TRUE.equals(model.getObject())){
                    return "Aprovado";
                }else if(Boolean.FALSE.equals(model.getObject())) {
                    return "Rejeitado";
                }
                return "";
            }
        }){
            protected void onConfigure() {
                super.onConfigure();
                if(Boolean.TRUE.equals(model.getObject())){
                    this.add(WicketUtils.$b.attr("class", "list-group-item bg-blue bg-font-blue"));
                }else if(Boolean.FALSE.equals(model.getObject())) {
                    this.add(WicketUtils.$b.attr("class", "list-group-item bg-red bg-font-red"));
                }
            }
        };
    }

    private BFModalWindow createEditModal() {
        BFModalWindow annotationModal = new AnnotationModalWindow("annotationModal",
                                                          model, referenced, context, this);
        context.getExternalContainer().appendTag("div", true, null, annotationModal);
        return annotationModal;
    }

    private ActionAjaxButton createOpenModalButton(final BFModalWindow annotationModal) {
        return new ActionAjaxButton("open_modal") {
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                keepOpened = true;
                annotationModal.show(target);
            }
        };
    }

    private ActionAjaxButton createDeleteModalButton() {
        final BFModalWindow deleteModal = new BFModalWindow("deleteAnnotationModal"){
            @Override
            protected void onInitialize() {
                super.onInitialize();
                final BFModalWindow thiz = this;

                thiz.setBody(new Label("alert",$m.ofValue("Deseja realmente apagar este comentário?")));

                this.addButton(BSModalBorder.ButtonStyle.PRIMARY, $m.ofValue("Apagar"),
                    new ActionAjaxButton("deleteBtn"){
                        protected void onAction(AjaxRequestTarget target, Form<?> form){
                            ((SIAnnotation)model.getObject()).clear();
                            target.add(AnnotationComponent.this);
                            target.appendJavaScript(AnnotationComponent.this.generateUpdateJS());
                            thiz.hide(target);
                        }
                    }
                );
                this.addLink(BSModalBorder.ButtonStyle.DANGER, $m.ofValue("Cancelar"),
                    new ActionAjaxLink("cancelDeleteBtn"){
                        protected void onAction(AjaxRequestTarget target) {
                            thiz.hide(target);
                        }
                    }
                );
            }
        };
        context.getExternalContainer().appendTag("div", true, null, deleteModal);
        return new ActionAjaxButton("trash_modal") {
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                deleteModal.show(target);
            }

            @Override
            public boolean isVisible() {
                return context.getViewMode().isVisualization();
            }
        };
    }

    private static String title(AbstractSInstanceModel referenced) {
        if(StringUtils.isNoneBlank(annotated(referenced).label())) return annotated(referenced).label();
        String label = labelOf(referenced);
        if(StringUtils.isNoneBlank(label))  return String.format("Comentários sobre %s", label);
        return "Comentários";
    }

    private static AtrAnnotation annotated(AbstractSInstanceModel referenced) {
        return referenced.getMInstancia().as(AtrAnnotation::new);
    }

    private static String labelOf(AbstractSInstanceModel target) {
        return target.getMInstancia().as(AtrBasic::new).getLabel();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("annotation.js")));
        response.render(CssReferenceHeaderItem.forReference(resourceRef("annotation.css")));
        response.render(JavaScriptContentHeaderItem.forScript(generateUpdateJS(),
                        "updateAnnotation_"+this.getMarkupId()+"_"+ new Date().getTime()
        ));
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }


    private String generateUpdateJS() {
        if(referencedComponent == null) return "";
        return "$(function(){\n" +
                "Annotation.create_or_update(" +
                    "'#"+referencedComponent.getMarkupId()+"', " +
                    "'#"+this.getMarkupId()+"'," +
                    "'#"+openModalButton.getMarkupId()+"'," +
                    "`"+textModel.getObject()+"`," +
                    ""+approvedModel.getObject()+"" +
                ");\n" +
                "});\n";
    }


    protected void onConfigure() {
        super.onConfigure();
        if(keepOpened){
            this.add(WicketUtils.$b.attr("style", "float: left; display: block;"));
        }else{
            this.add(WicketUtils.$b.attr("style", "float: left; display: none;"));
        }
        keepOpened = false;
        this.add(WicketUtils.$b.attrAppender("class", "portlet box sannotation-snipet-box", ""));
    }

    private static class AnnotationModalWindow extends BFModalWindow{

        private MInstanciaValorModel textModel, approvedModel ;
        private WicketBuildContext context;
        private AnnotationComponent parentComponent;
        private AbstractSInstanceModel referenced;

        public AnnotationModalWindow(String id,
                                     MInstanceRootModel model,
                                     AbstractSInstanceModel referenced,
                                     WicketBuildContext context,
                                     AnnotationComponent parentComponent
        ) {
            super(id);
            this.referenced = referenced;
            setDefaultModel(model);
            textModel = new MInstanciaValorModel<>(new SInstanceCampoModel<>(model,"text"));
            approvedModel = new MInstanciaValorModel<>(new SInstanceCampoModel<>(model,"isApproved"));
            this.context = context;
            this.parentComponent = parentComponent;
            this.setSize(BSModalBorder.Size.NORMAL);

            this.setBody(createBody());

            this.addButton(BSModalBorder.ButtonStyle.PRIMARY, $m.ofValue("OK"),
                    createOkButton(parentComponent)
            );
            this.addLink(BSModalBorder.ButtonStyle.DANGER, $m.ofValue("Cancelar"),
                    createCancelButton()
            );
        }

        private BSContainer createBody() {
            BSContainer modalBody = new BSContainer("bogoMips");
            createFields(modalBody);
            return modalBody;
        }

        private void createFields(BSContainer modalBody) {
            if(context.getViewMode().isVisualization()){
                createCommentField(modalBody);
                createApprovedField(modalBody);
            }else{
                modalBody.appendTag("pre", true, "", new Label("modalText",textModel));
                modalBody.appendTag("div", true, "", createApprovalLabel(approvedModel));
            }
        }

        private void createCommentField(BSContainer modalBody) {
            TextArea modalText = new TextArea<>("modalText", textModel);
            modalText.add(new Behavior(){
                public void bind( Component component ){
                    super.bind( component );
                    component.add(
                        AttributeModifier.replace( "onkeydown",
                                    Model.of( "window.Annotation.update_comment_box(event);")));
                }
            });
            modalBody.appendTag("textarea", true, "style='width: 100%;height: 60vh;' cols='15' ",
                    modalText);
        }

        private void createApprovedField(BSContainer modalBody) {
            modalBody.appendTag("label", true, "class=\"control-label\"",
                    new Label("approvalLabel",$m.ofValue("Aprovado?")));
            modalBody.appendTag("input", true, "type=\"checkbox\" class=\"make-switch\" "+
                            "data-on-color=\"info\" data-on-text=\"Sim\" "+
                            "data-off-color=\"danger\" data-off-text=\"Não\" ",
                    new CheckBox("modalApproval",approvedModel));
        }

        public void show(AjaxRequestTarget target) {
            this.setTitleText($m.ofValue(title(referenced)));

            super.show(target);
        }

        private ActionAjaxButton createOkButton(final AnnotationComponent parentComponent) {
            return new ActionAjaxButton("btn") {
                protected void onAction(AjaxRequestTarget target, Form<?> form) {
                    target.add(parentComponent);
                    AnnotationModalWindow.this.hide(target);
                    target.appendJavaScript(parentComponent.generateUpdateJS());
                }
            };
        }

        private ActionAjaxLink<Void> createCancelButton() {
            return new ActionAjaxLink<Void>("btn-cancelar") {
                protected void onAction(AjaxRequestTarget target) {
                    AnnotationModalWindow.this.hide(target);
                }
            };
        }
    }
}
