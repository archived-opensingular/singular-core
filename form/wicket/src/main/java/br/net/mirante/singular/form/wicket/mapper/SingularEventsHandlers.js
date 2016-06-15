;(function ($) {
    "use strict";
    window.SEH = (function () {

        var SINGULAR_BLUR_KEY   = 'SingularBlurKey',
            SINGULAR_CHANGE_KEY = 'SingularChangeKey',
            SINGULAR_PROCESS    = 'singular:process',
            SINGULAR_VALIDATE   = 'singular:validate';

        function addTextFieldHandlers(input) {

            var inputJQueryRef = $(input);

            inputJQueryRef.on('blur', function (event) {
                if (!inputJQueryRef.data(SINGULAR_CHANGE_KEY)) {
                    inputJQueryRef.data(SINGULAR_BLUR_KEY, window.setTimeout(function () {
                        inputJQueryRef.trigger(SINGULAR_VALIDATE);
                        inputJQueryRef.removeData(SINGULAR_BLUR_KEY);
                    }, 50));
                }
            });

            inputJQueryRef.on('change', function (event) {
                if (inputJQueryRef.data(SINGULAR_BLUR_KEY)) {
                    window.clearTimeout(inputJQueryRef.data(SINGULAR_BLUR_KEY));
                    inputJQueryRef.removeData(SINGULAR_BLUR_KEY);
                }
                inputJQueryRef.data(SINGULAR_CHANGE_KEY, window.setTimeout(function () {
                    inputJQueryRef.trigger(SINGULAR_PROCESS);
                    inputJQueryRef.removeData(SINGULAR_CHANGE_KEY);
                }, 50));
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
            "addTextFieldHandlers": addTextFieldHandlers
        }
    }())
}(jQuery));