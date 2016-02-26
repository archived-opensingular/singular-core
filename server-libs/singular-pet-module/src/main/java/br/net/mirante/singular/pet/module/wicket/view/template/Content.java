package br.net.mirante.singular.pet.module.wicket.view.template;


import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import br.net.mirante.singular.lambda.IFunction;
import br.net.mirante.singular.lambda.ISupplier;
import br.net.mirante.singular.pet.module.wicket.PetSession;
import de.alpharogroup.wicket.js.addon.toastr.Position;
import de.alpharogroup.wicket.js.addon.toastr.ShowMethod;
import de.alpharogroup.wicket.js.addon.toastr.ToastJsGenerator;
import de.alpharogroup.wicket.js.addon.toastr.ToastrSettings;
import de.alpharogroup.wicket.js.addon.toastr.ToastrType;


public abstract class Content extends Panel {

    private boolean withBreadcrumb;
    private boolean withInfoLink;
    private RepeatingView toolbar;

    public Content(String id) {
        this(id, false, false);
    }

    public Content(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id);
        this.withBreadcrumb = withBreadcrumb;
        this.withInfoLink = withInfoLink;
    }

    public Content addSideBar() {
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("contentTitle", getContentTitlelModel()));
        add(new Label("contentSubtitle", getContentSubtitlelModel()));
        WebMarkupContainer breadcrumb = new WebMarkupContainer("breadcrumb");
        add(breadcrumb);
        breadcrumb.add(new WebMarkupContainer("breadcrumbDashboard").add(
                $b.attr("href", "null null")));
        breadcrumb.add(getBreadcrumbLinks("_BreadcrumbLinks"));
        if (!withBreadcrumb) {
            breadcrumb.add(new AttributeAppender("class", "hide", " "));
        }
        add(toolbar = new RepeatingView("toolbarItems"));

    }


    protected StringResourceModel getMessage(String prop) {
        return new StringResourceModel(prop.trim(), this, null);
    }

    public MarkupContainer addToolbarItem(IFunction<String, Component> toolbarItem) {
        return toolbar.add(toolbarItem.apply(toolbar.newChildId()));
    }

    public PetSession getPetSession() {
        return PetSession.get();
    }

    protected void addToastrSuccessMessage(String messageKey) {
        addToastrMessage(messageKey, ToastrType.SUCCESS);
    }

    protected void addToastrErrorMessage(String messageKey) {
        addToastrMessage(messageKey, ToastrType.ERROR);
    }

    protected void addToastrWarningMessage(String messageKey) {
        addToastrMessage(messageKey, ToastrType.WARNING);
    }

    protected void addToastrInfoMessage(String messageKey) {
        addToastrMessage(messageKey, ToastrType.INFO);
    }

    private void addToastrMessage(String messageKey, ToastrType toastrType) {
        ToastrSettings settings = getDefaultSettings();
        settings.getToastrType().setValue(toastrType);
        settings.getNotificationTitle().setValue(getString(messageKey));
        ToastJsGenerator generator = new ToastJsGenerator(settings);

        if (!((WebRequest)RequestCycle.get().getRequest()).isAjax()) {
            add($b.onReadyScript((ISupplier<CharSequence>) () -> generator.generateJs(settings, null)));
        } else {
            AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
            target.appendJavaScript(generator.generateJs(settings, null));
        }

    }

    private ToastrSettings getDefaultSettings() {
        ToastrSettings settings = ToastrSettings.builder().build();
        settings.getPositionClass().setValue(Position.TOP_FULL_WIDTH);
        settings.getShowMethod().setValue(ShowMethod.SLIDE_DOWN);
        settings.getNotificationContent().setValue("");
        settings.getCloseButton().setValue(true);
        return settings;
    }

    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return new WebMarkupContainer(id);
    }

    protected WebMarkupContainer getInfoLink(String id) {
        return new WebMarkupContainer(id);
    }

    protected abstract IModel<?> getContentTitlelModel();

    protected abstract IModel<?> getContentSubtitlelModel();
}
