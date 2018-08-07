(function () {
    "use strict";
    window.QuickNav = (function () {
        function getQuickNav() {
            return $(".quick-nav");
        }

        return {
            init: function () {
                if (getQuickNav().length > 0) {
                    getQuickNav().each(function () {
                        var nav = $(this),
                            trigger = nav.find(".quick-nav-trigger");
                        trigger.on("click", function (event) {
                            event.preventDefault();
                            nav.toggleClass("nav-is-visible");
                        })
                    });
                    $(document).on("click", function (event) {
                        if (!$(event.target).is(".quick-nav-trigger") && !$(event.target).is(".quick-nav-trigger span")) {
                            getQuickNav().removeClass("nav-is-visible")
                        }
                    });
                }
            }
        }
    })();
})();