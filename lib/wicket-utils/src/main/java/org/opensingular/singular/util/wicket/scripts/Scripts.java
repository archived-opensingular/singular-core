/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.scripts;

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
