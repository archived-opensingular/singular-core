;(function (jQuery) {
    'use strict';

    if (typeof(Singular) === 'undefined') {
        window.Singular = {};
    }

    jQuery.extend(true, Singular, {

        opener: window.opener,

        exibirMensagem: function (mensagem, settings) {
            jQuery.extend(true, toastr.options, settings);

            toastr[toastr.options.toastrType](mensagem);
        },

        exibirMensagemWorklist: function (mensagem, options) {
            if (opener && opener.Singular) {
                opener.Singular.exibirMensagem(mensagem, options);
            }
        },

        atualizarContentWorklist: function () {
            if (opener && opener.Singular) {
                opener.Singular.reloadContent();
            }
        }
    });

})(jQuery);