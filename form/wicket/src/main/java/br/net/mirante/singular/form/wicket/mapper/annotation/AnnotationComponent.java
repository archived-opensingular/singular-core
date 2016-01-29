package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;

import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
        import br.net.mirante.singular.form.mform.core.annotation.MIAnnotation;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.model.AbstractMInstanciaModel;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
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
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * This is the visual component of an annotated field on screen.
 *
 * @author Fabricio Buzeto
 */
public class AnnotationComponent extends Panel {
    private final AbstractMInstanciaModel referenced;
    private final Component referencedComponent;
    private final WicketBuildContext context;
    private MInstanciaValorModel textModel, approvedModel;
    private MInstanceRootModel model;
    private FormComponent comment_field, approval_field;
    private boolean keepOpened = false;

    public AnnotationComponent(String id, AbstractMInstanciaModel referenced,
                               Component referencedComponent, WicketBuildContext context) {
        super(id);
        this.referenced = referenced;
        this.referencedComponent = referencedComponent;
        this.context = context;
        createModels(referenced);
    }

    private void createModels(AbstractMInstanciaModel referenced) {
        final MIAnnotation target = annotated(referenced).annotation();
        target.setTargetId(referenced.getMInstancia().getId());
        model = new MInstanceRootModel(target);
        setDefaultModel(model);
        createSubModels();
    }

    private void createSubModels() {
        textModel = new MInstanciaValorModel(new MInstanciaCampoModel<>(model,"text"));
        approvedModel = new MInstanciaValorModel(new MInstanciaCampoModel<>(model,"isApproved"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.queue(new Label("target_label",$m.ofValue(title(referenced))));
        comment_field = new TextArea<>("comment_field");
        approval_field = new CheckBox("approval_field");
        updateModels();
        this.queue(comment_field);
        this.queue(approval_field);

        BFModalWindow annotationModal = new AnnotationModalWindow("annotationModal",
                model, referenced,
                context, this);
        context.getExternalContainer().appendTag("div", true, null, annotationModal);
        this.queue(new ActionAjaxButton("open_modal") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                keepOpened = true;
                annotationModal.show(target);
            }
        });
    }

    private void updateModels() {
        createSubModels();
        comment_field.setDefaultModel(textModel);
        approval_field.setDefaultModel(approvedModel);
    }

    private static String title(AbstractMInstanciaModel referenced) {
        if(StringUtils.isNoneBlank(annotated(referenced).label())) return annotated(referenced).label();
        String label = labelOf(referenced);
        if(StringUtils.isNoneBlank(label))  return String.format("Comentários sobre %s", label);
        return "Comentários";
    }

    private static AtrAnnotation annotated(AbstractMInstanciaModel referenced) {
        return referenced.getMInstancia().as(AtrAnnotation::new);
    }

    private static String labelOf(AbstractMInstanciaModel target) {
        return target.getMInstancia().as(AtrBasic::new).getLabel();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final PackageResourceReference customJS = new PackageResourceReference(getClass(), "annotation.js");
        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        response.render(JavaScriptContentHeaderItem.forScript(generateUpdateJS(),"updateAnnotation_"+this.getMarkupId()
        ));
    }

    private String generateUpdateJS() {
        return "$(function(){\n" +
                "new Annotation(" +
                    "'#"+referencedComponent.getMarkupId()+"', " +
                    "'#"+this.getMarkupId()+"'," +
                    "'#"+approval_field.getMarkupId()+"'" +
                ").setup()\n" +
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
        this.add(WicketUtils.$b.attrAppender("class", "portlet box", ""));
    }

    private static class AnnotationModalWindow extends BFModalWindow{

        private MInstanciaValorModel textModel, approvedModel ;
        private WicketBuildContext context;
        private AnnotationComponent parentComponent;
        private AbstractMInstanciaModel referenced;

        public AnnotationModalWindow(String id,
                                     MInstanceRootModel model,
                                     AbstractMInstanciaModel referenced,
                                     WicketBuildContext context,
                                     AnnotationComponent parentComponent
        ) {
            super(id);
            this.referenced = referenced;
            setDefaultModel(model);
            textModel = new MInstanciaValorModel<>(new MInstanciaCampoModel<>(model,"text"));
            approvedModel = new MInstanciaValorModel<>(new MInstanciaCampoModel<>(model,"isApproved"));
            this.context = context;
            this.parentComponent = parentComponent;
            this.setSize(BSModalBorder.Size.NORMAL);

            BSContainer modalBody = new BSContainer("bogoMips");

            TextArea modalText = new TextArea<>("modalText", textModel);
            modalText.add(new Behavior(){
                @Override
                public void bind( Component component ){
                    super.bind( component );
                    component.add( AttributeModifier.replace( "onkeydown",
                            Model.of( "if(event.keyCode == 13) {$(event.target).val($(event.target).val()+'\\n');event.preventDefault();}" ) ) );
                }
            });
            modalBody.appendTag("textarea", true, "style='width: 100%;height: 60vh;' cols='15' ",
                    modalText);
            modalBody.appendTag("label", true, "class=\"control-label\"",
                    new Label("approvalLabel",$m.ofValue("Aprovado?")));
            modalBody.appendTag("input", true, "type=\"checkbox\" class=\"make-switch\" "+
                            "data-on-color=\"info\" data-on-text=\"Sim\" "+
                            "data-off-color=\"danger\" data-off-text=\"Não\" ",
                    new CheckBox("modalApproval",approvedModel));
            this.setBody(modalBody);

            addButton(BSModalBorder.ButtonStyle.PRIMARY, $m.ofValue("OK"),
                    new ActionAjaxButton("btn") {
                        @Override
                        protected void onAction(AjaxRequestTarget target, Form<?> form) {
                            target.add(parentComponent);
                            parentComponent.updateModels();
                            AnnotationModalWindow.this.hide(target);
                            target.appendJavaScript(parentComponent.generateUpdateJS());
                        }


                    }
            );

            this.addLink(BSModalBorder.ButtonStyle.DANGER, $m.ofValue("Cancelar"), new ActionAjaxLink<Void>("btn-cancelar") {
                @Override
                protected void onAction(AjaxRequestTarget target) {
                    AnnotationModalWindow.this.hide(target);
                }
            });
        }

        public void show(AjaxRequestTarget target) {
            this.setTitleText($m.ofValue(title(referenced)));

            super.show(target);
            target.appendJavaScript(getConfigureBackdropScript());
        }

        private String getConfigureBackdropScript(){
            String js = "";
            js +=" (function (zindex){ ";
            js +="     $('.modal-backdrop').each(function(index) { ";
            js +="         var zIndex = $(this).css('z-index'); ";
            js +="         $(this).css('z-index', zindex-1+index); ";
            js +="     }); ";
            js +="     $('.modal').each(function(index) { ";
            js +="         var zIndex = $(this).css('z-index'); ";
            js +="         $(this).css('z-index', zindex+index); ";
            js +="     }); ";
            js +=" })(10050); ";
            return js;
        }
    }
}
