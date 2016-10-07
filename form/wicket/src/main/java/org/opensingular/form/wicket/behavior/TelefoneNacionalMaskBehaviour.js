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

        var input = $('#' + id),
            bypassKeys = new ArrayWrapper([8, 9, 37, 39]),
            numberKeys = new ArrayWrapper([48, 49, 50, 51, 52, 53, 54, 55, 56, 57]),
            removeKeys = new ArrayWrapper([8, 46]);

        function toArray(string) {
            return string.split('');
        }

        function currentValueLength() {
            var matchArray = input.val().match(/[0-9]/g);
            if (matchArray) {
                return matchArray.length;
            } else {
                return 0;
            }
        }

        function clear() {
            input.val(input.val().replace(/[^0-9]/g, ''));
        }

        function setEightDigitsMask() {
            clear();
            var array = new ArrayWrapper(toArray(input.val()));
            if (input.val().trim().length > 0) {
                array.addAndShift(0, '(');
                array.addAndShift(3, ')');
                array.addAndShift(4, ' ');
                array.addAndShift(9, '-');
            }
            input.val(array.toPlainString());
        }

        function setNineDigitsMask() {
            clear();
            var array = new ArrayWrapper(toArray(input.val()));
            if (input.val().trim().length > 0) {
                array.addAndShift(0, '(');
                array.addAndShift(3, ')');
                array.addAndShift(4, ' ');
                array.addAndShift(10, '-');
            }
            input.val(array.toPlainString());
        }

        function getKeyCode(event) {
            return event.which || event.keyCode;
        }

        function onKeyPress(event) {
            var keyCode = getKeyCode(event);
            if (bypassKeys.contains(keyCode) && !event.shiftKey || event.ctrlKey) {
                return true;
            }
            if (currentValueLength() > 10) {
                setNineDigitsMask();
                return false;
            }
            return numberKeys.contains(keyCode);
        }

        function onKeyUp(event) {
            var keyCode = getKeyCode(event);
            if (removeKeys.contains(keyCode)) {
                if (currentValueLength() <= 11) {
                    setEightDigitsMask();
                }
                return true;
            }
            if (bypassKeys.contains(keyCode) || event.ctrlKey) {
                return true;
            }
            if (currentValueLength() > 10) {
                setNineDigitsMask();
            } else {
                setEightDigitsMask();
            }
        }

        input.on('keypress', onKeyPress);
        input.on('keyup', onKeyUp);

    };
}());
