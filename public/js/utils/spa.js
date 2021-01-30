var visitorId;

$.ajaxSetup({
    type: 'POST',
    timeout: 5000,
    error: function(xhr, status, e) {
        if (status == 'timeout') {
            sendAlert('We had an issue communicating with the server. Please check your connection settings.');
            return false;
        }
        sendAlert('We had an issue handling this link. Please refresh the page, and report this problem if it still persists.');
        console.error(e);
        return false;
    }
});

$(document).on('click', '.spa-link', function() {
    let link = $(this).attr('href');
    if (!link)
        link = $(this).data('link');
    let selector = $(this).data('selector') || '#main-content';
    $('.dropdown.open').each(function() {
        $(this).find('.dropdown-toggle').dropdown('toggle');
    });
    //TODO - allow other data to be added and passed to POST?
    if (!link) {
        sendAlert('We had an issue handling this link. Please report this problem by clicking on this alert.', function() {
            window.open('https://github.com/pyragon/cryogen-website/issues');
        });
        return false;
    }
    post(link, $(this).data(), selector);
    return false;
});

function post(link, data, selector, cb) {
    if (typeof selector === 'function') {
        cb = selector;
        selector = null;
    }
    selector = selector || '#main-content';
    if (visitorId)
        data.visitorId = visitorId;
    if (!visitorId) {
        setTimeout(() => {
            post(link, data, selector, cb);
        }, 100);
        return false;
    }
    try {
        $.post(link, data, ret => {
            data = parseJSON(ret);
            if (!data) return false;
            if (data['404']) {
                sendAlert('Unable to parse response from server. Please report this problem by clicking on this alert.', function() {
                    window.open('https://github.com/pyragon/cryogen-website/issues');
                });
                return null;
            }
            if (cb)
                cb(data);
            else {
                if (data.redirect) {
                    $(selector).html(data.html);
                    return false;
                }
                $(selector).html(data.html);
                if (selector == '#main-content') {
                    history.pushState({}, 'CryogenSPA', link);
                    $(document).off('click', '**');
                }
                $('.footer').height(function(index, height) {
                    return window.innerHeight - $(this).offset().top;
                });
            }
        });
    } catch (e) {
        console.error(e);
        console.log(link, data, cb);
    }
}

function parseJSON(data) {
    if (!data) {
        sendAlert('Unable to parse response from server. Please report this problem by clicking on this alert.', function() {
            window.open('https://github.com/pyragon/cryogen-website/issues');
        });
        return null;
    }
    try {
        data = JSON.parse(data);
    } catch (e) {
        console.error('Error from server: ' + e);
        console.error(data);
        sendAlert('Unable to parse response from server. Please report this problem by clicking on this alert.', function() {
            window.open('https://github.com/pyragon/cryogen-website/issues');
        });
        return null;
    }
    if (data.message)
        sendAlert(data.message);
    if (!data.success) {
        if (data.error)
            sendAlert(data.error);
        return null;
    }
    return data;
}

function createUUID() {
    if (!storageAvailable('localStorage'))
        return false;
    if (window.localStorage.getItem('cryogen_uuid'))
        visitorId = window.localStorage.getItem('cryogen_uuid');
    else {
        visitorId = uuid.v4();
        window.localStorage.setItem('cryogen_uuid', visitorId);
    }
}

createUUID();

function storageAvailable(type) {
    var storage;
    try {
        storage = window[type];
        var x = '__storage_test__';
        storage.setItem(x, x);
        storage.removeItem(x);
        return true;
    } catch (e) {
        return e instanceof DOMException && (
                // everything except Firefox
                e.code === 22 ||
                // Firefox
                e.code === 1014 ||
                // test name field too, because code might not be present
                // everything except Firefox
                e.name === 'QuotaExceededError' ||
                // Firefox
                e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
            // acknowledge QuotaExceededError only if there's something already stored
            (storage && storage.length !== 0);
    }
}