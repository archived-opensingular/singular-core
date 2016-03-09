package br.net.mirante.singular.util.wicket.toastr;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.text.MessageFormat;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import br.net.mirante.singular.lambda.ISupplier;
import de.alpharogroup.wicket.js.addon.toastr.Position;
import de.alpharogroup.wicket.js.addon.toastr.ShowMethod;
import de.alpharogroup.wicket.js.addon.toastr.ToastJsGenerator;
import de.alpharogroup.wicket.js.addon.toastr.ToastrSettings;
import de.alpharogroup.wicket.js.addon.toastr.ToastrType;

public class ToastrHelper {

    private Component component;

    public ToastrHelper(Component component) {
        this.component = component;
    }

    public void addToastrMessage(ToastrType toastrType, String messageKey, String... args) {
        ToastrSettings settings = getDefaultSettings();
        settings.getToastrType().setValue(toastrType);
        settings.getNotificationTitle().setValue(getString(messageKey, args));

        if (!((WebRequest) RequestCycle.get().getRequest()).isAjax()) {
            component.add($b.onReadyScript((ISupplier<CharSequence>) () -> generateJs(settings, false)));
        } else {
            AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
            target.appendJavaScript(generateJs(settings, true));
        }

    }

    protected String getString(String messageKey, String[] args) {
        String message = component.getString(messageKey);
        return MessageFormat.format(message, args);
    }

    protected String generateJs(ToastrSettings settings, boolean withDocumentReadyFunction) {
        ToastJsGenerator generator = new ToastJsGenerator(settings, withDocumentReadyFunction);
        return generator.generateJs();
    }

    protected ToastrSettings getDefaultSettings() {
        ToastrSettings settings = ToastrSettings.builder().build();
        settings.getPositionClass().setValue(Position.TOP_CENTER);
        settings.getTimeOut().setValue(5000);
        settings.getExtendedTimeOut().setValue(3000);
        settings.getShowMethod().setValue(ShowMethod.SLIDE_DOWN);
        settings.getNotificationContent().setValue("");
        settings.getCloseButton().setValue(true);
        settings.getProgressBar().setValue(true);
        return settings;
    }

    protected Component getComponent() {
        return component;
    }
}