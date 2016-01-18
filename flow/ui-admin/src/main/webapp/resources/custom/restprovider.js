restprovider = (function ($) {
    return {
        callDelegate: function (portletContext, onSucess, onError) {
            $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                url: "/rest/delegate",
                data: JSON.stringify(portletContext),
                type: "POST",
                success: function (data) {
                    onSucess(data);
                },
                error: function () {
                    onError();
                }
            });
        }
    }
})(jQuery);