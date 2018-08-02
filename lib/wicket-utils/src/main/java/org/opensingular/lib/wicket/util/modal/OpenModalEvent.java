package org.opensingular.lib.wicket.util.modal;

import java.io.Serializable;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.lambda.ITriConsumer;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class OpenModalEvent<T> implements IOpenModalEvent {

    public static interface ConfigureCallback<T> extends Serializable {
        void configure(ModalDelegate<T> delegate);
    }

    protected static interface ActionComponentFactory<C extends Component, T> extends Serializable {
        C create(String id, ModalDelegate<T> delegate, Optional<Form<?>> form, ActionCallback<T> action);
    }

    @FunctionalInterface
    public static interface ActionCallback<T> extends Serializable {

        void onAction(AjaxRequestTarget target, ModalDelegate<T> delegate, IModel<T> model);
        default void onError(AjaxRequestTarget target, ModalDelegate<T> delegate, IModel<T> model) {
            target.add(delegate.getModalBorder().getModalBody());
        }

        default ActionCallback<T> withOnError(ITriConsumer<ModalDelegate<T>, AjaxRequestTarget, IModel<T>> onError) {
            ActionCallback<T> self = this;
            return new ActionCallback<T>() {
                @Override
                public void onAction(AjaxRequestTarget target, ModalDelegate<T> delegate, IModel<T> model) {
                    self.onAction(target, delegate, model);
                }
                @Override
                public void onError(AjaxRequestTarget target, ModalDelegate<T> delegate, IModel<T> model) {
                    onError.accept(delegate, target, model);
                }
            };
        }
    }

    private final AjaxRequestTarget                target;
    private final IFunction<String, Component>     bodyContentFactory;
    private final ConfigureCallback<T>             configureCallback;
    @SuppressWarnings("unchecked")
    private IFunction<Component, IModel<T>>        modelExtractor = c -> (IModel<T>) c.getDefaultModel();
    private ActionComponentFactory<AjaxLink<T>, T> linkFactory    = OpenModalEvent::defaultNewLink;
    private ActionComponentFactory<AjaxButton, T>  buttonFactory  = OpenModalEvent::defaultNewButton;

    public OpenModalEvent(AjaxRequestTarget target, IModel<T> model, IFunction<String, Component> bodyContentFactory, ConfigureCallback<T> configureCallback) {
        this(target, bodyContentFactory, configureCallback);
        this.setModelExtractor(c -> model);
    }
    protected OpenModalEvent(AjaxRequestTarget target, IFunction<String, Component> bodyContentFactory, ConfigureCallback<T> configureCallback) {
        this.target = target;
        this.bodyContentFactory = bodyContentFactory;
        this.configureCallback = configureCallback;
    }

    protected final void setModelExtractor(IFunction<Component, IModel<T>> modelExtractor) {
        this.modelExtractor = modelExtractor;
    }
    protected final void setLinkFactory(ActionComponentFactory<AjaxLink<T>, T> linkFactory) {
        this.linkFactory = linkFactory;
    }
    protected final void setButtonFactory(ActionComponentFactory<AjaxButton, T> buttonFactory) {
        this.buttonFactory = buttonFactory;
    }

    protected static <T> AjaxLink<T> defaultNewLink(String id, ModalDelegate<T> delegate, Optional<Form<?>> form, ActionCallback<T> action) {
        return new AjaxLink<T>(id, delegate.getModel()) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                action.onAction(target, delegate, delegate.getModel());
            }
        };
    }
    protected static <T> AjaxButton defaultNewButton(String id, ModalDelegate<T> delegate, Optional<Form<?>> form, ActionCallback<T> action) {
        return new AjaxButton(id, form.orElse(null)) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                action.onAction(target, delegate, delegate.getModel());
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                action.onError(target, delegate, delegate.getModel());
            }
        };
    }

    @Override
    public Component getBodyContent(String id) {
        return bodyContentFactory.apply(id);
    }

    @Override
    public Optional<AjaxRequestTarget> getTarget() {
        return Optional.ofNullable(this.target);
    }

    @Override
    public void configureModal(BSModalBorder modal, Component bodyContent) {
        configureCallback.configure(
            new ModalDelegate<>(
                modal,
                modelExtractor.apply(bodyContent),
                new BodyContentPredicate(bodyContent),
                linkFactory,
                buttonFactory));
    }

    public static class ModalDelegate<T> implements Serializable {

        private final BSModalBorder                          modalBorder;
        private final IModel<T>                              model;
        private final IPredicate<Component>                  bodyContentPredicate;
        private final ActionComponentFactory<AjaxLink<T>, T> linkFactory;
        private final ActionComponentFactory<AjaxButton, T>  buttonFactory;

        public ModalDelegate(
                BSModalBorder modalBorder,
                IModel<T> model,
                IPredicate<Component> bodyContentPredicate,
                ActionComponentFactory<AjaxLink<T>, T> linkFactory,
                ActionComponentFactory<AjaxButton, T> buttonFactory) {
            this.modalBorder = modalBorder;
            this.model = model;
            this.bodyContentPredicate = bodyContentPredicate;
            this.linkFactory = linkFactory;
            this.buttonFactory = buttonFactory;
        }

        public void setTitle(String title) {
            setTitle(Model.of(title));
        }
        public void setTitle(Model<String> titleModel) {
            getModalBorder().setTitleText(titleModel);
        }
        public AjaxLink<T> addLink(String label, ActionCallback<T> action) {
            return addLink(getModalBorder().newButtonId(), Model.of(label), ButtonStyle.LINK, action);
        }
        public AjaxLink<T> addLink(String id, IModel<String> labelModel, ButtonStyle style, ActionCallback<T> action) {
            Optional<Form<?>> form = WicketUtils.findFirstChild(getModalBorder().getModalBody(), Form.class).map(it -> (Form<?>) it);
            AjaxLink<T> link = newAjaxLink(id, form, action);
            getModalBorder().addLink(style, labelModel, link);
            return link;
        }
        public AjaxButton addButton(String label, ActionCallback<T> action) {
            return addButton(getModalBorder().newButtonId(), Model.of(label), ButtonStyle.PRIMARY, action);
        }
        public AjaxButton addButton(String id, IModel<String> labelModel, ButtonStyle style, ActionCallback<T> action) {
            Optional<Form<?>> form = WicketUtils.findFirstChild(getModalBorder().getModalBody(), Form.class).map(it -> (Form<?>) it);
            AjaxButton button = newAjaxButton(id, form, action);
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
        public IModel<T> getModel() {
            return model;
        }
        public IPredicate<Component> getBodyContentPredicate() {
            return bodyContentPredicate;
        }
        public void close(AjaxRequestTarget target) {
            ICloseModalEvent.of(target, getBodyContentPredicate()).bubble(getModalBorder());
        }

        private ActionCallback<T> newCloseAction() {
            return (t, d, i) -> d.close(t);
        }
        private AjaxLink<T> newAjaxLink(String id, Optional<Form<?>> form, ActionCallback<T> action) {
            return linkFactory.create(id, ModalDelegate.this, form, action);
        }
        private AjaxButton newAjaxButton(String id, Optional<Form<?>> form, ActionCallback<T> action) {
            return buttonFactory.create(id, ModalDelegate.this, form, action);
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
