;(function () {
    setInterval(function () {
        $.ajax({
            url: "${callbackUrl}"
        });
    }, 10 * 60 * 1000);//10minutos
}());