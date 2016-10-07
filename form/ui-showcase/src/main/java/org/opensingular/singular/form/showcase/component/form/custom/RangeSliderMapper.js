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

RangeSliderMapper = {
    init: function (parent, fieldOneId, fieldTwoId, disable) {

        var dummyInput = document.createElement("input");

        var fieldOneVal = $(fieldOneId).val();
        var fieldTwoVal = $(fieldTwoId).val();

        if (fieldOneVal && fieldTwoVal) {
            $(dummyInput).val(fieldOneVal + ';' + fieldTwoVal);
        }

        parent.appendChild(dummyInput);

        $(dummyInput).ionRangeSlider({
            keyboard: true,
            min: 0,
            max: 120,
            type: 'double',
            disable: disable
        });

        $(dummyInput).on('change', function () {
            var v = $(dummyInput).val().split(';');
            $(fieldOneId).val(v[0]);
            $(fieldTwoId).val(v[1]);
        });
    }
};