package org.opensingular.form.wicket.modal;

import java.io.Serializable;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.lambda.ITriConsumer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;
import org.opensingular.lib.wicket.util.modal.ICloseModalEvent;

public class OpenSingularFormModalEvent<ST extends SType<SI>, SI extends SInstance> implements IOpenSingularFormModalEvent {

    public static interface ConfigureCallback<SI extends SInstance> extends Serializable {
        void configure(ModalDelegate<SI> modalBuilder);
    }
    @FunctionalInterface
    public static interface ActionCallback<SI extends SInstance> extends Serializable {

        void onAction(ModalDelegate<SI> delegate, AjaxRequestTarget target, IModel<SI> instanceModel);

        static <SI extends SInstance> ActionCallback<SI> dtm(ITriConsumer<ModalDelegate<SI>, AjaxRequestTarget, IModel<SI>> callback) {
            return (d, t, m) -> callback.accept(d, t, m);
        }
        static <SI extends SInstance> ActionCallback<SI> dti(ITriConsumer<ModalDelegate<SI>, AjaxRequestTarget, SI> callback) {
            return (d, t, m) -> callback.accept(d, t, m.getObject());
        }
        static <SI extends SInstance> ActionCallback<SI> dt(IBiConsumer<ModalDelegate<SI>, AjaxRequestTarget> callback) {
            return (d, t, m) -> callback.accept(d, t);
        }
        static <SI extends SInstance> ActionCallback<SI> d(IConsumer<ModalDelegate<SI>> callback) {
            return (d, t, m) -> callback.accept(d);
        }
    }

    private final AjaxRequestTarget            target;
    private final IConsumer<SingularFormPanel> formPanelConfigurer;
    private final ConfigureCallback<SI>        configureCallback;

    public OpenSingularFormModalEvent(AjaxRequestTarget target, IModel<SI> instanceModel, ConfigureCallback<SI> configureCallback) {
        this.target = target;
        this.formPanelConfigurer = sfp -> sfp.setInstanceCreator(instanceModel::getObject);
        this.configureCallback = configureCallback;
    }

    public OpenSingularFormModalEvent(AjaxRequestTarget target, Class<ST> instanceType, ConfigureCallback<SI> configureCallback) {
        this.target = target;
        this.formPanelConfigurer = sfp -> sfp.setInstanceFromType(instanceType);
        this.configureCallback = configureCallback;
    }

    @Override
    public Optional<AjaxRequestTarget> getTarget() {
        return Optional.ofNullable(this.target);
    }

    @Override
    public Component getBodyContent(String id) {
        final SingularFormPanel sfp = newSingularFormPanel("formPanel");
        this.formPanelConfigurer.accept(sfp);

        return new TemplatePanel(id, sfp.getInstanceModel(), "<div wicket:id='formPanel'></div>")
            .add(sfp);
    }

    protected SingularFormPanel newSingularFormPanel(String id) {
        return new SingularFormPanel(id, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configureModal(BSModalBorder modal, Component bodyContent) {
        IModel<SI> instanceModel = (IModel<SI>) bodyContent.getDefaultModel();
        configureCallback.configure(
            new ModalDelegate<>(modal, instanceModel, new BodyContentPredicate(bodyContent)));
    }

    public static class ModalDelegate<SI extends SInstance> implements Serializable {

        private final BSModalBorder         modalBorder;
        private final IModel<SI>            instanceModel;
        private final IPredicate<Component> bodyContentPredicate;

        public ModalDelegate(BSModalBorder modalBorder, IModel<SI> instanceModel, IPredicate<Component> bodyContentPredicate) {
            this.modalBorder = modalBorder;
            this.instanceModel = instanceModel;
            this.bodyContentPredicate = bodyContentPredicate;
        }

        public void setTitle(String title) {
            setTitle(Model.of(title));
        }
        public void setTitle(Model<String> titleModel) {
            getModalBorder().setTitleText(titleModel);
        }
        public AjaxLink<?> addLink(String label, ActionCallback<SI> action) {
            return addLink(getModalBorder().newButtonId(), Model.of(label), ButtonStyle.LINK, action);
        }
        public AjaxLink<?> addLink(String id, IModel<String> labelModel, ButtonStyle style, ActionCallback<SI> action) {
            AjaxLink<Void> link = newAjaxLink(id, action);
            getModalBorder().addLink(style, labelModel, link);
            return link;
        }
        public AjaxButton addButton(String label, ActionCallback<SI> action) {
            return addButton(getModalBorder().newButtonId(), Model.of(label), ButtonStyle.PRIMARY, action);
        }
        public AjaxButton addButton(String id, IModel<String> labelModel, ButtonStyle style, ActionCallback<SI> action) {
            AjaxButton button = newAjaxButton(id, action);
            getModalBorder().addButton(style, labelModel, button);
            return button;
        }

        public AjaxLink<?> addCloseLink(String label) {
            return addLink(getModalBorder().newButtonId(), Model.of(label), ButtonStyle.LINK, newCloseAction());
        }
        public AjaxLink<?> addCloseButton(String label) {
            return addLink(getModalBorder().newButtonId(), Model.of(label), ButtonStyle.CANCEL, newCloseAction());
        }
        
        public BSModalBorder getModalBorder() {
            return modalBorder;
        }
        public IModel<SI> getInstanceModel() {
            return instanceModel;
        }
        public IPredicate<Component> getBodyContentPredicate() {
            return bodyContentPredicate;
        }
        public void close(AjaxRequestTarget target) {
            ICloseModalEvent.of(target, getBodyContentPredicate()).bubble(getModalBorder());
        }

        private ActionCallback<SI> newCloseAction() {
            return (d, t, i) -> d.close(t);
        }
        private AjaxLink<Void> newAjaxLink(String id, ActionCallback<SI> action) {
            return new AjaxLink<Void>(id) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    WicketFormProcessing.onFormSubmit(getModalBorder().getModalBody(), target, getInstanceModel(), true);
                    action.onAction(ModalDelegate.this, target, getInstanceModel());
                }
            };
        }
        private AjaxButton newAjaxButton(String id, ActionCallback<SI> action) {
            return new AjaxButton(id) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    WicketFormProcessing.onFormSubmit(getModalBorder().getModalBody(), target, getInstanceModel(), true);
                    action.onAction(ModalDelegate.this, target, getInstanceModel());
                }
            };
        }
    }

    private static final class BodyContentPredicate implements IPredicate<Component> {
        private final Component bodyContent;
        public BodyContentPredicate(Component bodyContent) {
            this.bodyContent = bodyContent;
        }
        @Override
        public boolean test(Component t) {
            return t == bodyContent;
        }

    }
}
