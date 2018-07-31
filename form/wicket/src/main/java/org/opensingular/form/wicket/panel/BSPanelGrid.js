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
    if (window.hasOwnProperty('BSPANEL')) {
        return;
    }
    window.BSPANEL = (function () {

        function updateContentTabActive(idContent, idTabMenu) {
            $(idContent).addClass(' container-content-active ');
            $(idTabMenu).addClass(' container-tabMenu-active ');
        }

        function updateClassActive(nameTab, idContent, idTabMenu) {
            updateContentTabActive(idContent, idTabMenu);
            updateTabActive(nameTab, idTabMenu, idContent);
        }

        function isTabInsideTab(inside, idTabMenu) {
            var id = $('.container-content-active ' + inside).attr('id');
            if (id == undefined || id == null || idTabMenu == undefined) {
                return false;
            } else if (id == idTabMenu.replace('#', '')) {
                return true;
            } else {
                isTabInsideTab(' .container-content-active ' + inside , idTabMenu);
            }
        }

        function updateTabActive(nameTab, idTabMenu, idContent) {
            if (!isTabInsideTab(' .container-tabMenu-active ', idTabMenu)) {
                $('.tab-pane.active .container-tabMenu-active li').removeClass('active');
            } else {
                $('.tab-pane.active .container-content-active').removeClass('container-content-active');
                $('.tab-pane.active .container-tabMenu-active').removeClass('container-tabMenu-active');
                updateContentTabActive(idContent, idTabMenu);
            }
            $('.tab-pane.active .container-tabMenu-active li[data-tab-name=' + nameTab + ']').addClass('active');
        }

        return {
            "updateClassActive": updateClassActive
        };


    }())
}(jQuery));