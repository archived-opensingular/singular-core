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

(function () {

    var inputMarkupId = "${id}",
        addAll = "${addAllId}",
        removeAll = "${removeAllId}";

    var $pickList = $("#" + inputMarkupId);
    $pickList.multiSelect({
        selectableHeader: ${buttonAdd} ,
        selectionHeader: ${buttonRemove} ,
        afterSelect: function () {
            setTimeout(function () {
                $pickList.trigger('picklist:selected');
            }, 1400);
        },
        afterDeselect: function () {
            setTimeout(function () {
                $pickList.trigger('picklist:selected');
            }, 1400);
        }
    });


    $("#" + addAll).on('click', function () {
        $pickList.multiSelect('select_all');
    });

    $("#" + removeAll).on('click', function () {
        $pickList.multiSelect('deselect_all');
    });


})();
