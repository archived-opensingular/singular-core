package br.net.mirante.singular.form.wicket.behavior;

import java.util.Map;

import org.apache.wicket.Component;

public class MoneyMaskBehavior extends InputMaskBehavior {

    public MoneyMaskBehavior(Map<String, Object> options) {
        super(options, false);
    }

    /**
     * <p>Retorna o <i>script</i> gerado para este <i>behavior</i>.</p>
     *
     * @param component componente o qual este <i>behavior</i> deverá ser adicionado.
     * @return o <i>javascript</i> gerado.
     */
    protected String getScript(Component component) {
        return "var $this = $('#" + component.getMarkupId() + "');"
                + "$this.on('paste', function() {setTimeout(function(){$this.maskMoney('mask');},1);});"
                + "$this.on('drop', function(event) {"
                + "  event.preventDefault();"
                + "  $this.val(event.originalEvent.dataTransfer.getData('text'));"
                + "  $this.maskMoney('mask');"
                + "  setTimeout(function(){$this.maskMoney('mask');},1);"
                + "});"
                + "$this.maskMoney(" + getJsonOptions() + ");";
    }
}
