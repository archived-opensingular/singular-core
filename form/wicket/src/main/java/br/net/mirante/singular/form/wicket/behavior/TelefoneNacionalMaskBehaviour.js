;
"use strict";

var Singular = Singular || {};

Singular.applyTelefoneNacionalMask = function (id) {

    var selector = '#' + id;
    var bypassKeys = [8, 9, 37, 39];
    var numberKeys = [49, 50, 51, 52, 53, 54, 55, 56, 57];
    var removeKeys = [8, 46];

    Array.prototype.singularContais = function (value) {
        for (var i = 0; i < this.length; i += 1) {
            if (this[i] == value) {
                return true;
            }
        }
        return false;
    };

    Array.prototype.singularAddAndShift = function (index, val) {
        if (index > this.length) {
            return;
        }
        if (this.length == index) {
            this.push(val);
        } else {
            var oldValue = this[index];
            this[index] = val;
            return this.singularAddAndShift(index + 1, oldValue);
        }
    };

    Array.prototype.singularToPlainString = function () {
        var output = "";
        for (var i = 0; i < this.length; i += 1) {
            output += this[i];
        }
        return output;
    };


    String.prototype.singularToArray = function () {
        return this.split('');
    };

    function currentValueLength() {
        var matchArray = $(selector).val().match(/[0-9]/g);
        if (matchArray) {
            return matchArray.length;
        } else {
            return 0;
        }
    }

    function clear() {
        $(selector).val($(selector).val().replace(/[^0-9]/g, ''));
    }

    function setEightDigitsMask() {
        clear();
        var array = $(selector).val().singularToArray();
        if ($(selector).val().trim().length > 0) {
            array.singularAddAndShift(0, '(');
            array.singularAddAndShift(3, ')');
            array.singularAddAndShift(4, ' ');
            array.singularAddAndShift(9, '-');
        }
        $(selector).val(array.singularToPlainString());
    }

    function setNineDigitsMask() {
        clear();
        var array = $(selector).val().singularToArray();
        if ($(selector).val().trim().length > 0) {
            array.singularAddAndShift(0, '(');
            array.singularAddAndShift(3, ')');
            array.singularAddAndShift(4, ' ');
            array.singularAddAndShift(10, '-');
        }
        $(selector).val(array.singularToPlainString());
    }

    function onKeyPress(event) {
        var keyCode = event.which || event.keyCode;
        if (bypassKeys.singularContais(keyCode) || removeKeys.singularContais(keyCode) || event.ctrlKey) {
            return true;
        }
        if (currentValueLength() > 10) {
            setNineDigitsMask();
            return false;
        }
        return numberKeys.singularContais(keyCode);

    }

    function onKeyUp(event) {
        var keyCode = event.which || event.keyCode;
        if (removeKeys.singularContais(keyCode)) {
            if (currentValueLength() <= 11) {
                setEightDigitsMask();
            }
            return true;
        }
        if (bypassKeys.singularContais(keyCode) || event.ctrlKey) {
            return true;
        }
        if (currentValueLength() > 10) {
            setNineDigitsMask();
        } else {
            setEightDigitsMask();
        }
    }

    $(selector).on('keypress', onKeyPress);
    $(selector).on('keyup', onKeyUp);

};