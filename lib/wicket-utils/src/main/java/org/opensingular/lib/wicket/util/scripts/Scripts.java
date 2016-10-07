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

package org.opensingular.lib.wicket.util.scripts;

public class Scripts {

    /**
     * Quando a tecla Enter é acionada procura um button da classe "btn-primary"
     * e executa o click().
     */
    public static String executeEnter() {
        return ".keypress(function (e) {"
            + "  var buttons = $(this).find('.btn-primary:visible');"
            + "  if (buttons.length > 0 && e.which === 13) {"
            + "    e.preventDefault();"
            + "    $(buttons[buttons.length - 1]).click();"
            + "  }"
            + "});";
    }
    
    /**
     * Script para incluir uma div sobre cada um dos componetes desabilitados para permitir a rolagem
     */
    public static String slimScrollOverDisabledInputsWorkAround(){ 
        return "$('div').remove('.overalldisabled');$('<div class=\"overalldisabled\"></div>').insertAfter(':disabled:not(button)');";
    }
}
