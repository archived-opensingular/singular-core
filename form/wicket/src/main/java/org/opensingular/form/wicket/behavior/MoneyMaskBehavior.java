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
