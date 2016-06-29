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

        function addMousedownHandlers(input) {
            var inputJQueryRef = $(input);
            inputJQueryRef.on('mousedown', function () {
                if (contex.hasOwnProperty(SINGULAR_BLUR_KEY)) {
                    window.clearTimeout(contex[SINGULAR_BLUR_KEY]);
                    delete contex[SINGULAR_BLUR_KEY];
                }
                contex[SINGULAR_CHANGE_KEY] = window.setTimeout(function () {
                    delete contex[SINGULAR_CHANGE_KEY];
                }, 30);
            });
        }

        function addTextFieldHandlers(input) {
            var inputJQueryRef = $(input);
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
            inputJQueryRef.on(SINGULAR_VALIDATE, function () {
                console.log('validated' + this);
            });
            inputJQueryRef.on(SINGULAR_PROCESS, function () {
                console.log('processed' + this);
            });
        }

        /**
         * Retorna os métodos publicos da API
         */
        return {
            "addTextFieldHandlers": addTextFieldHandlers,
            "addMousedownHandlers": addMousedownHandlers
        }
    }())
}(jQuery));