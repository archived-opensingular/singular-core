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

;(function () {
    "use strict";

    var ArrayWrapper = function (array) {
        var _array = array;
        return {
            contains: function (value) {
                for (var i = 0; i < _array.length; i += 1) {
                    if (_array[i] == value) {
                        return true;
                    }
                }
                return false;
            },
            addAndShift: function (index, val) {
                if (index > _array.length) {
                    return;
                }
                if (_array.length == index) {
                    _array.push(val);
                } else {
                    var oldValue = _array[index];
                    _array[index] = val;
                    return this.addAndShift(index + 1, oldValue);
                }
            },
            toPlainString: function () {
                var output = "";
                for (var i = 0; i < _array.length; i += 1) {
                    output += _array[i];
                }
                return output;
            },
            get: function () {
                return _array;
            }
        }
    };

    window.Singular = window.Singular || {};
    window.Singular.applyTelefoneNacionalMask = function (id) {

        var inputPhoneNumber = $('#' + id);

        var maskBehavior = function(val) {
            return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000'
                : '(00) 0000-00009';
        };

        var options = {
            onKeyPress : function(val, e, field, options) {
                field.mask(maskBehavior.apply({}, arguments), options);
            }
        };

        inputPhoneNumber.mask(maskBehavior, options);

    };
}());
