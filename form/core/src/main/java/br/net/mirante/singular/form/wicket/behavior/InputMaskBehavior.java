package br.net.mirante.singular.form.wicket.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;

import br.net.mirante.singular.util.wicket.jquery.JQuery;

/**
 * <p>Classe responsável por adicionar máscara a um {@code input}.</p>
 *
 * <p>Usa como implementação <i>javascript</i> a <strong>API JQuery InputMask</strong>.</p>
 *
 * @author Mirante Tecnologia
 */
public class InputMaskBehavior extends Behavior {

    private String jsonOptions;

    public InputMaskBehavior(String mask) {
        this(mask, null);
    }

    public InputMaskBehavior(String mask, Map<String, Object> options) {
        this(mask, options, true);
    }

    public InputMaskBehavior(final String mask, final Map<String, Object> options, boolean appendDefaultOptions) {
        Map<String, Object> opts = new HashMap<>();
        if (Objects.nonNull(options)) {
            opts.putAll(options);
        }
        if (appendDefaultOptions) {
            setDefaultOpcoes(opts);
        }
        if (Objects.nonNull(mask)) {
            opts.put("mask", mask);
        }
        setJsonOptions(opts);
    }

    private void setDefaultOpcoes(Map<String, Object> options) {
        options.put("placeholder", "");
        options.put("skipOptionalPartCharacter", "");
        options.put("showMaskOnHover", false);
        options.put("showMaskOnFocus", false);
        options.put("greedy", false);
    }

    private void setJsonOptions(Map<String, Object> options) {
        this.jsonOptions = new JSONObject(options).toString();
    }

    protected String getJsonOptions() {
        return jsonOptions;
    }

    @Override
    public void afterRender(Component component) {
        component.getResponse().write("<script>" + JQuery.ready(getScript(component)) + "</script>");
    }

    protected String getScript(Component component) {
        return "var $this = $('#" + component.getMarkupId() + "');"
                + "$this.on('paste', function() {setTimeout(function(){$this.change();},1);});"
                + "$this.on('drop', function(event) {"
                + "  event.preventDefault();"
                + "  $this.val(event.originalEvent.dataTransfer.getData('text'));"
                + "  $this.inputmask(" + jsonOptions + ");"
                + "  setTimeout(function(){$this.change();},1);"
                + "});"
                + "$this.inputmask(" + jsonOptions + ");";
    }
}
