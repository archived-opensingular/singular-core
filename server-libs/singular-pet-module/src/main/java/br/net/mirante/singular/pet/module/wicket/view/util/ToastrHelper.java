package br.net.mirante.singular.pet.module.wicket.view.util;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.text.MessageFormat;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.json.JSONStringer;
import org.json.JSONWriter;

import br.net.mirante.singular.lambda.ISupplier;
import de.alpharogroup.wicket.js.addon.core.StringTextType;
import de.alpharogroup.wicket.js.addon.core.StringTextValue;
import de.alpharogroup.wicket.js.addon.core.ValueEnum;
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

    private String getString(String messageKey, String[] args) {
        String message = component.getString(messageKey);
        return MessageFormat.format(message, args);
    }


    public void addToastrMessageWorklist(ToastrType toastrType, String messageKey, String... args) {
        ToastrSettings settings = getDefaultSettings();
        settings.getToastrType().setValue(toastrType);

        String options = toStringJson(settings);
        String mensagem = getString(messageKey, args);
        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        String js =
                " $(document).ready(function() { "
                        + "     Singular.exibirMensagemWorklist('%s', %s); "
                        + " }); ";
        target.appendJavaScript(String.format(js, mensagem, options));
    }

    private String toStringJson(ToastrSettings settings) {
        JSONWriter jsonWriter = new JSONStringer().object();

        for (StringTextValue<?> textValue : settings.asSet()) {
            String name = textValue.getName();
            name = name.substring(name.lastIndexOf(".") + 1);
            Object value;
            if (textValue.getType() == StringTextType.ENUM) {
                value = ((StringTextValue<? extends ValueEnum>) textValue).getValue().getValue();
            } else {
                value = textValue.getValue();
            }

            jsonWriter.key(name).value(value);
        }

        return jsonWriter.endObject().toString();
    }

    private String generateJs(ToastrSettings settings, boolean withDocumentReadyFunction) {
        ToastJsGenerator generator = new ToastJsGenerator(settings, withDocumentReadyFunction);
        return generator.generateJs();
    }

    private ToastrSettings getDefaultSettings() {
        ToastrSettings settings = ToastrSettings.builder().build();
        settings.getPositionClass().setValue(Position.TOP_FULL_WIDTH);
        settings.getShowMethod().setValue(ShowMethod.SLIDE_DOWN);
        settings.getNotificationContent().setValue("");
        settings.getCloseButton().setValue(true);
        return settings;
    }
}
