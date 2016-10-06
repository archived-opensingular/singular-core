/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.modal;

import org.opensingular.singular.commons.lambda.IConsumer;
import org.opensingular.singular.util.wicket.ajax.AjaxErrorEventPayload;
import org.opensingular.singular.util.wicket.feedback.BSFeedbackPanel;
import org.opensingular.singular.util.wicket.feedback.NotContainedFeedbackMessageFilter;
import org.opensingular.singular.util.wicket.jquery.JQuery;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.ILabelProvider;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.io.Serializable;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;

@SuppressWarnings({ "serial" })
public class BSModalBorder extends Border {

    private static final String BUTTON_LABEL = "label";

    public enum ButtonStyle {
        //@formatter:off
        EMPTY   (""), 
        DEFAULT ("btn-default"), 
        PRIMARY ("btn-primary"),
        LINK    ("btn-link"),
        DANGER  ("btn-danger"),
        BLUE    ("blue"),
        CANCEl  ("cancel-btn"),
        CONFIRM ("confirm-btn");
        //@formatter:on

        private String cssClass;

        ButtonStyle(String cssClass) {
            this.cssClass = cssClass;
        }

        public String getCssClass() {
            return cssClass;
        }

        public IModel<String> cssClassModel() {
            return new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {
                    return getCssClass();
                }
            };
        }
    }

    public static enum Size {
        NORMAL("modal-belver"), LARGE("modal-lg"), SMALL("modal-sm"), FULL("modal-full"), FIT("modal-fit");
        protected final String styleClass;
        private Size(String styleClass) {
            this.styleClass = styleClass;
        }
    }

    private static final String DIALOG           = "dialog";
    private static final String CLOSE_ICON       = "closeIcon";
    private static final String COMPRESS_ICON    = "compressIcon";
    private static final String EXPAND_ICON      = "expandIcon";
    private static final String TITLE            = "title";
    private static final String HEADER           = "header";
    private static final String BODY             = "body";
    private static final String FOOTER           = "footer";

    private Size                size             = Size.NORMAL;
    private boolean             dismissible      = false;

    private final RepeatingView buttonsContainer = new RepeatingView("buttons");
    protected BSFeedbackPanel   feedbackGeral    = newFeedbackPanel("feedbackGeral", this, newIFeedbackMessageFilter());

    protected BSFeedbackPanel newFeedbackPanel(String id, BSModalBorder fence, IFeedbackMessageFilter messageFilter) {
        return new BSFeedbackPanel(id, fence, messageFilter);
    }

    protected IFeedbackMessageFilter newIFeedbackMessageFilter() {
        return new NotContainedFeedbackMessageFilter(getBodyContainer());
    }

    private final Component              closeIcon;
    private final Component              compressIcon;
    private final Component              expandIcon;
    private IConsumer<AjaxRequestTarget> closeIconCallBack;

    public BSModalBorder(String id) {
        this(id, null);
    }
    public BSModalBorder(String id, IModel<?> model) {
        super(id, model);
        setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

        final IModel<String> modalSizeModel = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return size.styleClass;
            }
        };

        final WebMarkupContainer dialog = new WebMarkupContainer(DIALOG);
        final WebMarkupContainer header = new WebMarkupContainer(HEADER);
        final WebMarkupContainer body = new WebMarkupContainer(BODY);
        final WebMarkupContainer footer = new WebMarkupContainer(FOOTER);

        closeIcon = newCloseIcon(CLOSE_ICON);
        compressIcon = newCompressIcon(COMPRESS_ICON);
        expandIcon = newExpandIcon(EXPAND_ICON);
        final Component title = newTitle(TITLE, getDefaultModel());
        final Fragment buttonsFragment = new Fragment("buttons", "buttonsFragment", this);

        header.setOutputMarkupId(true);
        footer.setOutputMarkupId(true);

        addToBorder(dialog
            .add(header
                .add(closeIcon)
                .add(compressIcon)
                .add(expandIcon)
                .add(title))
            .add(body)
            .add(footer
                .add(feedbackGeral)
                .add(buttonsFragment
                    .add(buttonsContainer)))
            .add(new AttributeAppender("class", modalSizeModel, " "))

        );

        dialog.add($b.onReadyScript(comp -> JQuery.$(comp) + ".on('keypress', function (e) {"
            + "  var buttons = $(this).find('.btn-primary:visible');"
            + "  if (buttons.length > 0 && e.which === 13) {"
            + "    e.preventDefault();"
            + "    $(buttons[buttons.length - 1]).click();"
            + "  }"
            + "});"));

        add(new AttributeAppender("class", Model.of("modal fade modal-scroll"), " "));
        add(new AttributeAppender("style", Model.of("visibility:visible"), ";"));
        add(new AttributeModifier("tabindex", "-1"));
        add($b.onReadyScript(this::getShowJavaScriptCallback));

        setVisible(false);
        setMinimizable(false);
    }

    @Override
    public void onEvent(IEvent<?> event) {
        Object payload = event.getPayload();
        if (payload instanceof AjaxErrorEventPayload) {
            AjaxRequestTarget target = ((AjaxErrorEventPayload) payload).getTarget();
            refreshContent(target);
            event.stop();
        }
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        tag.put("tabindex", "-1");
        tag.put("role", DIALOG);
        tag.put("aria-labelledby", getTitle().getMarkupId());
        tag.put("aria-hidden", "true");
    }

    public boolean anyMessage() {
        return feedbackGeral.anyMessage();
    }

    public BSModalBorder addButton(ButtonStyle style, String labelKey, AjaxButton button) {
        IModel<String> model = null;
        if (labelKey != null) {
            model = new ResourceModel(labelKey);
        }
        return addButton(style, model, button);
    }

    public BSModalBorder addButton(ButtonStyle style, IModel<String> label, Button button) {
        if (label != null) {
            button.setLabel(label);
        }
        buttonsContainer.addOrReplace(button
            .add(newButtonLabel(BUTTON_LABEL, button))
            .add(new AttributeAppender("class", style.cssClassModel(), " ")));

        return this;
    }

    public BSModalBorder addButton(ButtonStyle style, AjaxButton button) {
        return addButton(style, (String) null, button);
    }

    public BSModalBorder addLink(ButtonStyle style, String labelKey, AjaxLink<?> button) {
        IModel<String> model = null;
        if (labelKey != null) {
            model = new ResourceModel(labelKey);
        }
        return addLink(style, model, button);
    }

    public BSModalBorder addLink(ButtonStyle style, IModel<String> label, AjaxLink<?> button) {
        buttonsContainer.addOrReplace(button
            .add(newLinkLabel(BUTTON_LABEL, button, label))
            .add(new AttributeAppender("class", style.cssClassModel(), " ")));
        return this;
    }

    public BSModalBorder addLink(ButtonStyle style, AjaxLink<?> button) {
        return addLink(style, (String) null, button);
    }

    public BSModalBorder removeButtons() {
        buttonsContainer.removeAll();
        return this;
    }

    public BSModalBorder setRenderModalBodyTag(boolean render) {
        getModalBody().setRenderBodyOnly(render);
        return this;
    }

    public BSModalBorder setRenderModalFooterTag(boolean render) {
        getModalFooter().setRenderBodyOnly(render);
        return this;
    }

    protected Label newButtonLabel(String id, ILabelProvider<String> button) {
        return new Label(id, new LabelModel(button));
    }

    protected Label newLinkLabel(String id, AbstractLink button, IModel<String> label) {
        return new Label(id, label);
    }

    @Override
    public BSModalBorder add(Behavior... behaviors) {
        return (BSModalBorder) super.add(behaviors);
    }

    @Override
    public BSModalBorder add(Component... children) {
        /* XXX: Verificar o problema que está ocorrendo na página de processos! */
        if (children.length > 0 && children[0].getId().equals("buttonsFragment")) {
            return this;
        }
        return (BSModalBorder) super.add(children);
    }

    @Override
    public BSModalBorder addOrReplace(Component... children) {
        return (BSModalBorder) super.addOrReplace(children);
    }

    @Override
    public BSModalBorder setDefaultModel(IModel<?> model) {
        return (BSModalBorder) super.setDefaultModel(model);
    }

    public BSModalBorder setSize(Size size) {
        this.size = size;
        return this;
    }

    public BSModalBorder setCloseIconVisible(boolean visible) {
        closeIcon.setVisible(visible);
        return this;
    }
    public boolean isCloseIconVisible() {
        return closeIcon.isVisible();
    }

    public BSModalBorder setCloseIconCallback(IConsumer<AjaxRequestTarget> closeIconCallBack) {
        this.closeIconCallBack = closeIconCallBack;
        return this;
    }

    public BSModalBorder setMinimizable(boolean minimizable) {
        compressIcon.setVisible(minimizable);
        expandIcon.setVisible(minimizable);
        return this;
    }
    public BSModalBorder setDismissible(boolean dismissible) {
        this.dismissible = dismissible;
        return this;
    }
    public boolean isDismissible() {
        return dismissible;
    }

    public BSModalBorder setTitleText(IModel<String> titleModel) {
        getTitle().setDefaultModel(titleModel);
        return this;
    }

    public void show(AjaxRequestTarget target) {
        this.setVisible(true);
        if (target != null) {
            target.add(this);
        }
    }

    public void hide(AjaxRequestTarget target) {
        // limpo os valores, pois erros de validacao impedem o formulario de se ser recarregado 
        clearInputs();

        this.setVisible(false);

        if (target != null) {
            final String blockingFunction = "hide_hidden_wicket_modal";
            target.prependJavaScript(blockingFunction + "|" + getHideJavaScriptCallback(blockingFunction));
            target.add(this);
        }
    }

    public void clearInputs() {
        visitChildren(new IVisitor<Component, Void>() {
            @Override
            public void component(Component comp, IVisit<Void> visit) {
                if (comp instanceof Form) {
                    ((Form<?>) comp).clearInput();
                    visit.dontGoDeeper();
                } else if (comp instanceof FormComponent) {
                    ((FormComponent<?>) comp).clearInput();
                }
            }
        });
    }

    public void focusFirstComponent(AjaxRequestTarget target) {
        getBodyContainer().visitChildren(FormComponent.class, new IVisitor<FormComponent<?>, Void>() {
            @Override
            public void component(FormComponent<?> object, IVisit<Void> visit) {
                if (object.isEnabledInHierarchy()) {
                    target.focusComponent(object);
                    visit.stop();
                }
            }
        });
    }

    public void refreshContent(AjaxRequestTarget target) {
        target.add(getModalHeader(), getModalFooter());
        for (Component child : getBodyContainer()) {
            target.add(child);
        }
    }

    public String getShowJavaScriptCallback() {
        StringBuilder sb = JQuery.$(this)
            .append(".modal({")
            .append("keyboard:").append(isDismissible())
            .append(",backdrop:").append(isDismissible() ? "true" : "'static'")
            .append("})");
        sb.append(""
            + "\n.on('shown.bs.modal',function(evt) {"
            + "\n $(this).find('.modal-body, .modal-footer')"
            + "\n  .find('input:not([type=hidden]),select,textarea,button,object,a')"
            + "\n  .filter(':visible')"
            + "\n  .first()"
            + "\n  .each(function(){ this.focus(); });"
            + "\n})");
        return sb.toString();
    }

    public CharSequence getHideJavaScriptCallback() {
        return getHideJavaScriptCallback(null);
    }

    public CharSequence getHideJavaScriptCallback(String blockingFunction) {
        StringBuilder sb = JQuery.$(this);

        if (blockingFunction != null)
            sb.append(".one('hidden.bs.modal', ").append(blockingFunction).append(")");

        return sb.append(".modal('hide')");

    }
    public final MarkupContainer getModalHeader() {
        return (MarkupContainer) get(DIALOG).get(HEADER);
    }
    public final MarkupContainer getModalBody() {
        return (MarkupContainer) get(DIALOG).get(BODY);
    }
    public final MarkupContainer getModalFooter() {
        return (MarkupContainer) get(DIALOG).get(FOOTER);
    }

    public Component getTitle() {
        return getModalHeader().get(TITLE);
    }

    protected Component newCloseIcon(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onCloseClicked(target);
            }
        };
    }

    protected Component newCompressIcon(String id) {
        return new WebMarkupContainer(id)
            .add($b.onReadyScript(comp -> JQuery.$(comp) + ""
                + ".on('click', function() {"
                + JQuery.$(expandIcon) + ".show();"
                + JQuery.$(compressIcon) + ".hide();"
                + JQuery.$(getModalBody()) + ".slideUp();"
                + JQuery.$(getModalFooter()) + ".slideUp();"
                + " $('.modal-backdrop.fade.in').css('opacity',0.2);"
                + "})"
                + ";"))
            .add(new Behavior() {
                @Override
                public void renderHead(Component component, IHeaderResponse response) {
                    super.renderHead(component, response);
                    response.render(CssHeaderItem.forCSS(""
                        + ".modal-header-icon {"
                        + " background-color: transparent;"
                        + " float: right;"
                        + " border: 0;"
                        + " margin: 0;"
                        + " padding: 0;"
                        + " border-image: none;"
                        + " line-height: 14px;"
                        + " margin-top: -4px;"
                        + " margin-right: 8px;"
                        + " color: #ccc;"
                        + "}"
                        + ".modal-header-icon:hover {"
                        + " color: #888;"
                        + "}",
                        "ModalBorder_modal-header-icon"));
                }
            });
    }

    protected Component newExpandIcon(String id) {
        return new WebMarkupContainer(id).add($b.onReadyScript(comp -> JQuery.$(comp) + ""
            + ".on('click', function() {"
            + JQuery.$(expandIcon) + ".hide();"
            + JQuery.$(compressIcon) + ".show();"
            + JQuery.$(getModalBody()) + ".slideDown();"
            + JQuery.$(getModalFooter()) + ".slideDown();"
            + " $('.modal-backdrop.fade.in').css('opacity','');"
            + "})"
            + ".css('display','none')"
            + ";"));
    }

    protected Component newTitle(String id, IModel<?> titleModel) {
        return new Label(id, titleModel);
    }

    protected void onCloseClicked(AjaxRequestTarget target) {
        if (closeIconCallBack != null) {
            closeIconCallBack.accept(target);
        }
        hide(target);
    }

    private static final class LabelModel extends AbstractReadOnlyModel<String> {
        private final LabelModelProvider provider;
        public LabelModel(ILabelProvider<String> provider) {
            this.provider = () -> {
                IModel<String> label = provider.getLabel();
                return (label != null) ? label.getObject() : null;
            };
        }
        @Override
        public String getObject() {
            return String.valueOf(provider.get());
        }
        interface LabelModelProvider extends Serializable {
            Object get();
        }
    }
}
