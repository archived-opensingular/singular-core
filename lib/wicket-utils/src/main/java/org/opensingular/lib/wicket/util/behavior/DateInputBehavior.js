(function () {
    var dateInput = $("#${inputDate}");
    dateInput.on('blur', function () {
        if (!verificarDataValida(dateInput.val())) {
            dateInput.val('');
        }
    });

    function verificarDataValida(value) {
        return value.match(/^(0?[1-9]|[12][0-9]|3[0-1])[/., -](0?[1-9]|1[0-2])[/., -](19|20)?\d{2}$/)
            || value.match(/^(0?[1-9]|1[0-2])[/., -](19|20)?\d{2}$/);
    }
})();
