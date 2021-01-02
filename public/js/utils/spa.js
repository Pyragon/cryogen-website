$.ajaxSetup({
    type: 'POST',
    timeout: 5000,
    error: function(xhr, status, e) {
        if(status == 'timeout') {
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
    if(!link)
        link = $(this).data('link');
    let selector = $(this).data('selector') || '#main-content';
    //TODO - allow other data to be added and passed to POST?
    if(!link) {
        sendAlert('We had an issue handling this link. Please report this problem by clicking on this alert.', function() {
            window.open('https://github.com/pyragon/cryogen-website/issues');
        });
        return false;
    }
    post(link, $(this).data(), selector);
    return false;
});

function post(link, data, selector, cb) {
    selector = selector || $('#main-content');
    $.post(link, data, ret => {
        data = parseJSON(ret);
        if(!data) return false;
        if(cb)
            cb(data);
        else {
            $(selector).html(data.html);
            history.pushState({}, 'CryogenSPA', link);
            console.log('pushing state: '+link);
        }
    });
}

function parseJSON(data) {
    if(!data) {
        sendAlert('Unable to parse response from server. Please report this problem by clicking on this alert.', function() {
            window.open('https://github.com/pyragon/cryogen-website/issues');
        });
        return null;
    }
    try {
        data = JSON.parse(data);
    } catch(e) {
        console.error('Error from server: '+e);
        console.error(data);
        sendAlert('Unable to parse response from server. Please report this problem by clicking on this alert.', function() {
            window.open('https://github.com/pyragon/cryogen-website/issues');
        });
        return null;
    }
    if(data.redirect) {
        document.write(data.redirect);
        return data;
    }
    if(data.message)
        sendAlert(data.message);
    if(!data.success) {
        if(data.error)
            sendAlert(data.error);
        return null;
    }
    return data;
}