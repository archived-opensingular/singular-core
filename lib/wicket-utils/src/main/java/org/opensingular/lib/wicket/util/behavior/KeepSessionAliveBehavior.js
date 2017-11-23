;(function () {
    setInterval(function () {
        $.ajax({
            url: "${callbackUrl}"
        });
    }, 5 * 60 * 1000);//5 minutos
}());