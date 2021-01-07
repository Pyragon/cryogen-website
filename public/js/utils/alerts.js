function sendAlert(text, onClick) {
    let options = {
        text: text,
        layout: 'topRight',
        timeout: 5000,
        theme: 'cryogen'
    };
    if (onClick) {
        options.closeWith = [];
        options.callback = {
            onClick
        };
    }
    n = noty(options);
    return n.options.id;
}

function closeNoty(id) {
    if (id) {
        if (id.options)
            id = id.options.id;
        let noty = $.noty.store[id];
        noty.close();
        if (id == n.options.id)
            n = null;
        return true;
    }
    if (!n) return false;
    n.close();
    n = null;
    return true;
}

function openNoty(title, html, buttons) {
    n = noty({
        text: title,
        type: 'confirm',
        layout: 'center',
        template: html,
        dismissQueue: false,
        theme: 'cryogen',
        buttons
    })
}

function openConfirmationNoty(title, yes) {
    title = title || 'Are you sure?';
    n = noty({
        text: title,
        type: 'confirm',
        layout: 'center',
        dismissQueue: false,
        theme: 'cryogen',
        buttons: [{
            addClass: 'btn btn-primary',
            text: 'Yes',
            onClick: ($noty) => {
                yes($noty.options.id);
            }
        }, {
            addClass: 'btn btn-danger',
            text: 'No',
            onClick: ($noty) => closeNoty($noty.options.id)
        }]
    });
    return n.options.id;
}

$(document).click(function(e) {
    let target = e.target;
    if ($('.sorted')) {
        if (!$(e.target).closest('.sorted').length) {
            let display = $('.sorted').css('display');
            if (display != 'none') {
                $('.sorted').css('display', 'none');
                return true;
            }
        }
    }
    if ($('.filtered')) {
        if (!$(e.target).closest('.filtered').length) {
            let display = $('.filtered').css('display');
            if (display != 'none') {
                $('.filtered').css('display', 'none');
                return true;
            }
        }
    }
    if (typeof n == 'undefined' || !n)
        return;
    var id = n.options.id;
    if ($(e.target).closest('#' + id).length) {

    } else {
        n.close();
        n = null;
    }
});