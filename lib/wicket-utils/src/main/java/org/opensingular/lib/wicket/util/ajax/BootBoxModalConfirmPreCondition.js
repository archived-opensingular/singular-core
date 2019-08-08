if (attrs.confirmed) {
    return true
}

bootbox.confirm({
    title: "${title}",
    message: "${message}",
    buttons: {
        confirm: {
            label: '${confirmLabel}',
            className: '${confirmClassName}'
        },
        cancel: {
            label: '${cancelLabel}',
            className: '${cancelClassName}'
        }
    },
    locale: 'pt',
    callback: function (result) {
        if (result) {
            attrs.e = null;
            attrs.confirmed = true;
            Wicket.Ajax.ajax(attrs);
        }
    }
});

return false;