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
 * <p>Algumas opções podem ser adicionadas por padrão às configurações de máscara. Elas
 * são as seguintes:</p>
 *
 * <ul>
 *     <li>placeholder: ''</li>
 *     <li>skipOptionalPartCharacter: ''</li>
 *     <li>showMaskOnHover: false</li>
 *     <li>showMaskOnFocus: false</li>
 *     <li>greedy: false</li>
 * </ul>
 *
 * @see <a href="https://github.com/RobinHerbots/jquery.inputmask">JQuery InputMask</a>
 * @author Mirante Tecnologia
 */
public class InputMaskBehavior extends Behavior {

    public static final String MASK_ATTR = "mask";
    public static final String MAX_LENGTH_ATTR = "repeat";

    private String jsonOptions;

    /**
     * <p>Enumerador com algumas máscaras predefinidas.</p>
     */
    public enum Masks {
        /**
         * <p>Máscara que permite apenas valores numéricos: [0-9].</p>
         */
        NUMERIC("9"),
        /**
         * <p>Máscara para datas do tipo DD/MM/AAAA.</p>
         */
        FULL_DATE("99/99/9999"),
        /**
         * <p>Máscara para datas do tipo MM/AAAA.</p>
         */
        SHORT_DATE("99/9999"),
        /**
         * <p>Máscara para CPF.</p>
         */
        CPF("999.999.999-99"),
        /**
         * <p>Máscara para CNPJ.</p>
         */
        CNPJ("99-999.999/9999-99"),
        /**
         * <p>Máscara para CEP.</p>
         */
        CEP("99.999-999");

        private String mask;

        Masks(String mask) {
            this.mask = mask;
        }

        /**
         * <p>Retorna a máscara correspondente a este enumerador.</p>
         *
         * @return a máscara correspondente a este enumerador.
         */
        public String getMask() {
            return mask;
        }
    }

    /**
     * <p>Instancia um novo <i>behavior</i> com a máscara especificada.</p>
     *
     * <p>Apenas as opções de máscaras padrões serão carregadas.</p>
     *
     * @param mask a máscara especificada.
     * @see <a href="https://github.com/RobinHerbots/jquery.inputmask">JQuery InputMask</a>
     */
    public InputMaskBehavior(String mask) {
        this(mask, null);
    }

    /**
     * <p>Instancia um novo <i>behavior</i> com a máscara especificada.</p>
     *
     * <p>Apenas as opções de máscaras padrões serão carregadas.</p>
     *
     * @param mask a máscara especificada.
     * @see <a href="https://github.com/RobinHerbots/jquery.inputmask">JQuery InputMask</a>
     */
    public InputMaskBehavior(Masks mask) {
        this(mask.getMask(), null);
    }

    /**
     * <p>Instancia um novo <i>behavior</i> com máscara e opções especificadas.</p>
     *
     * <p>Além das opções especificadas, as opções padrões também serão carregadas.</p>
     *
     * @param mask a máscara especificada.
     * @param options as opções especificadas.
     * @see <a href="https://github.com/RobinHerbots/jquery.inputmask">JQuery InputMask</a>
     */
    public InputMaskBehavior(String mask, Map<String, Object> options) {
        this(mask, options, true);
    }

    /**
     * <p>Instancia um novo <i>behavior</i> com máscara e opções especificadas.</p>
     *
     * <p>Além das opções especificadas, as opções padrões também serão carregadas.</p>
     *
     * @param mask a máscara especificada.
     * @param options as opções especificadas.
     * @see <a href="https://github.com/RobinHerbots/jquery.inputmask">JQuery InputMask</a>
     */
    public InputMaskBehavior(Masks mask, Map<String, Object> options) {
        this(mask.getMask(), options, true);
    }

    /**
     * <p>Instancia um novo <i>behavior</i> com máscara e opções especificadas.</p>
     *
     * <p>Além das opções especificadas, as opções padrões poderão ser carregadas dependendo
     * do parâmetro {@code appendDefaultOptions}.</p>
     *
     * @param mask a máscara especificada.
     * @param options as opções especificadas.
     * @param appendDefaultOptions indica quando carregar as opções padrões.
     * @see <a href="https://github.com/RobinHerbots/jquery.inputmask">JQuery InputMask</a>
     */
    public InputMaskBehavior(final String mask, final Map<String, Object> options, boolean appendDefaultOptions) {
        Map<String, Object> opts = new HashMap<>();
        if (Objects.nonNull(options)) {
            opts.putAll(options);
        }
        if (appendDefaultOptions) {
            setDefaultOpcoes(opts);
        }
        if (Objects.nonNull(mask)) {
            opts.put(MASK_ATTR, mask);
        }
        setJsonOptions(opts);
    }

    public InputMaskBehavior(final Map<String, Object> options) {
        this((String) null, options, true);
    }

    /**
     * <p>Instancia um novo <i>behavior</i> com máscara e opções especificadas.</p>
     *
     * <p>Além das opções especificadas, as opções padrões poderão ser carregadas dependendo
     * do parâmetro {@code appendDefaultOptions}.</p>
     *
     * @param mask a máscara especificada.
     * @param options as opções especificadas.
     * @param appendDefaultOptions indica quando carregar as opções padrões.
     * @see <a href="https://github.com/RobinHerbots/jquery.inputmask">JQuery InputMask</a>
     */
    public InputMaskBehavior(final Masks mask, final Map<String, Object> options, boolean appendDefaultOptions) {
        this(mask.getMask(), options, appendDefaultOptions);
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

    /**
     * <p>Retorna as opções usadas para criar este <i>behavior</i>, já em format {@code json}.</p>
     *
     * @return o {@code json} das opções usadas por este <i>behavior</i>.
     */
    protected String getJsonOptions() {
        return jsonOptions;
    }

    @Override
    public void afterRender(Component component) {
        component.getResponse().write("<script>" + JQuery.ready(getScript(component)) + "</script>");
    }

    /**
     * <p>Retorna o <i>script</i> gerado para este <i>behavior</i>.</p>
     *
     * @param component componente o qual este <i>behavior</i> deverá ser adicionado.
     * @return o <i>javascript</i> gerado.
     */
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
