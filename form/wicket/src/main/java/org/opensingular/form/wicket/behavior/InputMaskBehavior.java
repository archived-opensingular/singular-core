/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.behavior;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

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
 * @author Daniel Bordin
 */
public class InputMaskBehavior extends Behavior {

    public static final Logger logger = LoggerFactory.getLogger(InputMaskBehavior.class);

    public static final String MASK_ATTR       = "mask";
    public static final String MAX_LENGTH_ATTR = "repeat";

    private String jsonOptions;

    /**
     * <p>Enumerador com algumas máscaras predefinidas.</p>
     */
    public static class Masks {
        /**
         * <p>Máscara que permite apenas valores numéricos: [0-9].</p>
         */
        public static Masks NUMERIC = new Masks("9");

        /**
         * <p>Máscara para datas do tipo DD/MM/AAAA.</p>
         */
        public static Masks FULL_DATE = new Masks("99/99/9999");

        /**
         * <p>Máscara para datas do tipo MM/AAAA.</p>
         */
        public static Masks SHORT_DATE = new Masks("99/9999");

        /**
         * <p>Máscara para CPF.</p>
         */
        public static Masks CPF = new Masks("999.999.999-99");

        /**
         * <p>Máscara para CNPJ.</p>
         */
        public static Masks CNPJ = new Masks("99.999.999/9999-99");

        /**
         * <p>Máscara para CEP.</p>
         */
        public static Masks CEP = new Masks("99.999-999");

        public static Masks TIME = new Masks("9{1,2}:99");

        private String mask;

        public Masks(String mask) {
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

        public static Masks valueOf(String s) {
            try {
                Field f = ReflectionUtils.findField(Masks.class, s);
                if (f != null){
                    return (Masks) f.get(null);
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
            return new Masks(s);
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

    public InputMaskBehavior(final Map<String, Object> options, boolean appendDefaultOptions) {
        this((String) null, options, appendDefaultOptions);
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
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript("(function(){" + getScript(component) + ";})()"));
    }

    /**
     * <p>Retorna o <i>script</i> gerado para este <i>behavior</i>.</p>
     *
     * @param component componente o qual este <i>behavior</i> deverá ser adicionado.
     * @return o <i>javascript</i> gerado.
     */
    protected String getScript(Component component) {
        return "var $this = $('#" + component.getMarkupId() + "');"
        //  + "$this.on('paste', function(event) {console.log(event); setTimeout(function(){$this.change();},1);});"
            + "$this.on('drop', function(event) {"
            + "  event.preventDefault();"
            + "  $this.val(event.originalEvent.dataTransfer.getData('text'));"
            + "  $this.inputmask('remove').inputmask(" + jsonOptions + ");"
            + "  setTimeout(function(){$this.change();},1);"
            + "});"
            + "$this.inputmask('remove').inputmask(" + jsonOptions + ");";
    }
}
