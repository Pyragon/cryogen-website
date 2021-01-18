function closeButton(text) {
    text = text || 'Close';
    return {
        addClass: 'btn btn-danger',
        text,
        onClick: ($noty) => closeNoty($noty.options.id)
    }
}

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
        if (n && id == n.options.id)
            n = null;
        noty.close();
        return true;
    }
    if (!n) return false;
    n.close();
    n = null;
    return true;
}

function postNoty(endpoint, data, title, buttons, layout) {
    post(endpoint, data, null, data => {
        openNoty(title, data.html, buttons, layout);
    });
}

function openNoty(title, html, buttons, layout) {
    layout = layout || 'center';
    n = noty({
        text: title,
        type: 'confirm',
        layout,
        template: html,
        dismissQueue: false,
        theme: 'cryogen',
        buttons
    });
    return n.options.id;
}

function openConfirmationNoty(title, yes, yesTitle, noTitle) {
    title = title || 'Are you sure?';
    yesTitle = yesTitle || 'Yes';
    noTitle = noTitle || 'No';
    n = noty({
        text: title,
        type: 'confirm',
        layout: 'center',
        dismissQueue: false,
        theme: 'cryogen',
        buttons: [{
            addClass: 'btn btn-primary',
            text: yesTitle,
            onClick: ($noty) => {
                yes($noty.options.id);
            }
        }, closeButton(noTitle)]
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