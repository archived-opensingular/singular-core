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

;(function ($) {
    "use strict";
    if (window.hasOwnProperty('SEH')) {
        return;
    }
    window.SEH = (function () {

        var SINGULAR_BLUR_KEY = 'SingularBlurKey',
            SINGULAR_CHANGE_KEY = 'SingularChangeKey',
            SINGULAR_PROCESS = 'singular:process',
            SINGULAR_VALIDATE = 'singular:validate';

        var contex = {};

        function addTextFieldHandlers(input) {
            var inputJQueryRef = $('#'+input);
            inputJQueryRef.on('blur', function (event) {
                if (!contex.hasOwnProperty(SINGULAR_CHANGE_KEY)) {
                    contex[SINGULAR_BLUR_KEY] = window.setTimeout(function () {
                        inputJQueryRef.trigger(SINGULAR_VALIDATE);
                        delete contex[SINGULAR_BLUR_KEY];
                    }, 40);
                }
            });
            inputJQueryRef.on('change', function (event) {
                if (contex.hasOwnProperty(SINGULAR_BLUR_KEY)) {
                    window.clearTimeout(contex[SINGULAR_BLUR_KEY]);
                    delete contex[SINGULAR_BLUR_KEY];
                }
                contex[SINGULAR_CHANGE_KEY] = window.setTimeout(function () {
                    inputJQueryRef.trigger(SINGULAR_PROCESS);
                    delete contex[SINGULAR_CHANGE_KEY];
                }, 30);
            });
        }

        /**
         * Retorna os métodos publicos da API
         */
        return {
            "addTextFieldHandlers": addTextFieldHandlers
        }
    }())
}(jQuery));