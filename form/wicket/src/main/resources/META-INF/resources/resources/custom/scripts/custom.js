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

var Page = function() {

    function handleSlimScroll(){
        $('.scroller').slimScroll({});
    }

    function handleDatePickers() {
        if (jQuery().datepicker) {
            $('.date-picker').datepicker({
                rtl: App.isRTL(),
                orientation: "right",
                autoclose: true,
                language: 'pt-BR'
            });
        }
    }

    function handleBootstrapSelect() {
        $('.bs-select').selectpicker({
            iconBase: 'fa',
            tickIcon: 'fa-check'
        });
    }

    function handleMultiSelect() {
        $('.multi-select').multiSelect();
    }

    return {
        init: function() {
            handleDatePickers();
            handleBootstrapSelect();
            handleMultiSelect();
            handleSlimScroll();
        }
    };

}();
