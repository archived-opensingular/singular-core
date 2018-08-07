(function () {
    "use strict";
    window.QuickNav = {};
    window.QuickNav.init = function () {
        var quickNavs = $(".quick-nav");
        if (quickNavs.length > 0) {
            quickNavs.each(function () {
                var quickNav = $(this),
                    quickNavTrigger = quickNav.find(".quick-nav-trigger");
                quickNavTrigger.on("click", function (quickNavTriggerClickEvent) {
                    quickNavTriggerClickEvent.preventDefault();
                    quickNav.toggleClass("nav-is-visible");
                })
            });
            $(document).on("click", function (event) {
                if (!$(event.target).is(".quick-nav-trigger") && !$(event.target).is(".quick-nav-trigger span")) {
                    i.removeClass("nav-is-visible")
                }
            });
        }
    };
})();