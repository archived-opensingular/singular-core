"use strict";

function appendListItemEvent() {
    $(".list-item-header").click(function () {
        $(this).siblings('.list-item-body').toggle();
    });
    $('.list-item-header .singular-remove-btn').click(function (event) {
        stopPropagation(event)
    });
}

function stopPropagation(event) {
    if (typeof event.stopPropagation == "function") {
        event.stopPropagation();
    } else {
        event.cancelBubble = true;
    }
}
