//AJAX WITH PROMISE GLOBAL FUNCTION + SEND ALERT GLOBAL FUNCTION
function reloadOverview() {

}
String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};

const CLOSE = 'Click to close search bar.';
const OPEN = 'Click to open search bar.';

var changed = false;

var n = null
var comment_n = null

function getPages(page_t, page) {
    var pages = [];
    //always show first page
    pages.push(1);
    while (true) {
        //if only 1 page, we only display the 1
        if (page_t <= 1)
            break;
        //if under than 5 pages, we'll only ever display the 5
        if (page_t <= 5) {
            for (var i = 2; i < page_t; i++)
                pages.push(i);
            break;
        }
        if (page_t - page <= 3) {
            for (var i = 4; i > 0; i--)
                pages.push(page_t - i);
            break;
        }
        //second-value, if value is less than 1 we don't print. to avoid [1] [1] 2 3 4 if you were on page 2
        var p = page - 1;
        if (p > 1)
            pages.push(p);
        //show current page, don't show if page is 1, as we've already placed page 1 in array
        if (page != 1)
            pages.push(page);
        //show next page, once again don't show if page 1 to avoid above
        if (page + 1 <= page_t && page != 1)
            pages.push(page + 1);
        //if we have leftover spaces, add next pages to them until we're out of pages or space
        //vvvv needs work, this will cause issues down the line most likely
        if (pages.length < 4)
            for (var i = pages.length; i < (4 > page_t ? page_t : 4); i++)
                pages.push(i + 1);
        break;
    }
    //last value = last page
    if (page_t > 1)
        pages.push(page_t);
    var elem = $('<div></div>');
    for (var i = 0; i < pages.length; i++) {
        var show_page = pages[i];
        var span = $(`<span data-page=${show_page}></span>`);
        var html = (show_page == page ? '[' : '') + '' + show_page + '' + (show_page == page ? ']' : '');
        var next = i == pages.length - 1 ? null : pages[i + 1];
        var last = i == 0 ? null : pages[i - 1];
        if (show_page == 1) {
            html = 'First';
            if (next !== null && next !== 2)
                html += '...';
        } else if (show_page == page_t) {
            html = 'Last';
            if (last != null && last != show_page - 1)
                html = '...Last';
        }
        if (i != (pages.length - 1))
            html += ' ';
        span.html(html);
        if (show_page != page)
            span.addClass('vis-link page-link');
        elem.append(span);
    }
    return elem;
}

function isEmpty(...args) {
    for (var i = 0; i < args.length; i++) {
        var val = args[i].toString();
        if (val.replace(/\s/g, "") == "")
            return true;
    }
    return false;
}

function getJSON(ret) {
    var data = JSON.parse(ret);
    if (!data) {
        sendAlert('Error with return from server!');
        console.error('Error with return from server!', ret);
        return null;
    }
    if (!data.success) {
        sendAlert('Session expired! Please reload the page to login again.');
        return null;
    }
    if (!data.success) {
        if (data.error !== '')
            sendAlert(data.error);
        return null;
    }
    return data;
}

function closeNoty(noty) {
    n = null
    noty.close();
    if (comment_n != null)
        comment_n.close()
    comment_n = null
}

function loadPunishment(id, appeal, mod) {
    $.post('/staff', {
        mod: 'punish',
        action: 'view-punish',
        id: id,
        appeal: appeal
    }, function(ret) {
        var data = getJSON(ret);
        if (data == null) return;
        update(false, data.html, mod);
        update(true, 'Currently viewing punishment', mod);
        hidePages(mod);
    }).fail(function() {
        sendAlert('Error connecting to the website server. Please try again later.');
    });
}

function loadAppeal(id, mod) {
    $.post('/staff', {
        mod: 'appeal',
        action: 'view-appeal',
        id: id
    }, function(ret) {
        var data = getJSON(ret);
        if (data == null) return;
        update(false, data.html, mod);
        update(true, 'Currently viewing appeal', mod);
        hidePages(mod);
    }).fail(function() {
        sendAlert('Error connecting to the website server. Please try again later.');
    });
}

function loadReport(mod, archive, id) {
    $.post('/staff', {
        mod: mod,
        action: 'view-report',
        id: id,
        archived: archive
    }, function(ret) {
        var data = getJSON(ret);
        if (data == null) return;
        update(true, 'Currently viewing report', mod);
        update(false, data.html, mod);
        hidePages(mod);
    }).fail(function() {
        sendAlert('Error connecting to the website server. Please try again later.');
    });
}

function loadRecovery(id) {
    $.post('/staff', {
        mod: 'recover',
        action: 'view',
        id: id
    }, function(ret) {
        var data = getJSON(ret);
        if (data == null) return;
        update(true, 'Currently viewing recovery', 'recover');
        update(false, data.html, 'recover');
        hidePages('recover');
    }).fail(function() {
        sendAlert('Error connecting to the website server. Please try again later.');
    });
}

function loadRecoveryList() {
    loadList('recover', archive, recover_page);
    return false;
}

function loadList(mod, archive, page) {
    $.post('/staff', {
        mod: mod,
        action: 'view-list',
        archived: archive,
        page: page
    }, function(ret) {
        var data = getJSON(ret);
        if (data == null) return;
        update(false, data.html, mod);
        update(true, getDescription(archive), mod);
        updatePage(data.pageTotal, page, mod);
        removeFilters(mod);
    }).fail(function() {
        sendAlert('Error connecting to the website server. Please try again later.');
    });
}

function update(info, data, mod) {
    $(`#${mod}-${info ? 'info' : 'main'}`).html(data);
    if (info)
        $('#archive-' + mod).html(' View ' + (!archive ? 'Archive' : 'Active'));
}

function hidePages(mod) {
    $('#' + mod + '-pages').closest('.pages').css('display', 'none');
}

function updatePage(page_t, page, mod) {
    $('#' + mod + '-pages').closest('.pages').css('display ', '');
    $('#' + mod + '-pages').html('Pages: ' + getPages(page_t, page).html());
}

function sendAlert(text) {
    var n = noty({
        text: text,
        layout: 'topRight',
        timeout: 5000,
        theme: 'cryogen'
    });
}

//account search section

//staff section search
var searching = [];

function search(mod, page, archive, input = null) {
    if (input === null)
        input = filtersToQuery(mod);
    if (input === '' || input === ',' || input === ', ') {
        loadList(mod, archive, 1);
        updateSearchInput(mod, '');
        searching[mod] = false;
        return false;
    }
    searching[mod] = true;
    changed = true;
    $.post('/staff', {
        mod: mod,
        action: 'search',
        search: input,
        page: page,
        archive: archive
    }, function(ret) {
        var data = getJSON(ret);
        if (data == null) return false;
        update(false, data.html, mod);
        updateSearchInput(mod, updateFilters(mod, data.filters));
        updatePage(data.pageTotal, page, mod);
    });
}

//shutdown timer

var shutdown = 0;

var shutdown_timer = null;

var restart_timer = null;

var reconnect_timer = null;

function checkRestart() {
    if (shutdown_timer) {
        clearInterval(restart_timer);
        return;
    };
    $.ajax({
        url: 'http://66.70.190.195:8085/utils?action=get-restart-time',
        type: 'POST',
        error: () => {
            clearInterval(restart_timer);
            restarted();
        },
        success: (ret) => {
            var data = getJSON(ret);
            if (data == null) return;
            var delay = data.delay;
            if (delay > 0) {
                shutdown = delay;
                shutdown_timer = setInterval(decreaseRestart, 1000);
                clearInterval(restart_timer);
            }
        },
        timeout: 900
    });
}

function decreaseRestart() {
    if (shutdown == 0) {
        restarted();
        return;
    }
    shutdown--;
    $('#shutdown-timer').html('Website restarting in: ' + shutdown + ' seconds');
}

function reconnect() {
    $.ajax({
        url: 'http://66.70.190.195:8080/',
        type: 'GET',
        error: function(data) {
            setTimeout(reconnect, 1000);
        },
        success: function(data) {
            $('#shutdown-timer').html('Website has restarted. Click here to refresh the page.');
            $('#shutdown-timer').addClass('restart-page').css('cursor', 'pointer');
        },
        timeout: 900
    });
}

function restarted() {
    clearInterval(shutdown_timer);
    $('#shutdown-value').html('');
    $('#shutdown-timer').html('Website is being restarted. Attempting to reconnect.');
    setTimeout(reconnect, 1000);
}

function ping(ip, callback) {

    this.callback = callback;
    this.ip = ip;

    var _that = this;

    this.img = new Image();

    this.img.onload = function() {
        _that.callback('onload');
    };
    this.img.onerror = function(e) {
        _that.callback('onerror', e);
    };

    this.start = new Date().getTime();
    this.img.src = 'http://' + ip;
    this.timer = setTimeout(function() {
        _that.callback('timeout');
    }, 900);
}

var valid = ['_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
    '8', '9', ' '
];

function containsInvalidChars(name) {
    for (var i = 0; i < name.length; i++) {
        var n = name.charAt(i);
        if (!valid.includes(n)) {
            return true;
        }
    }
    return false;
}

function validName(name) {
    name = name.toLowerCase()
    if (containsInvalidChars(name))
        return 'Display name contains invalid characters';
    if (name.length < 3 || name.length > 12)
        return 'Display name must be between 3 and 12 characters';
    if (/"\w*(-{2}|_{2}|-_|_-)\w*"/.test(name))
        return 'Name cannot contain two spaces, underscores, or hyphens in a row';
    if (name.startsWith("-") || name.endsWith("-"))
        return 'Name cannot start or end with a hyphen';
    if (name.startsWith("_") || name.endsWith("_"))
        return 'Display name cannot start or end with an underscore';
    if (name.startsWith(" ") || name.endsWith(" "))
        return 'Display name cannot start or end with a space';
    if (name.toLowerCase().includes("mod") || name.toLowerCase().includes("admin"))
        return 'Display name contains invalid words';
    return null;
}

function post(link, options, callback) {
    $.post(link, options, function(ret) {
        callback(ret);
    }).fail(function() {
        sendAlert('Error connecting to website server. Please try again.');
    });
}

//static

$(document).ready(function() {

    shutdown = $('#shutdown').val(); //TODO - redo
    if (shutdown > 0)
        shutdown_timer = setInterval(decreaseRestart, 1000);
    else restart_timer = setInterval(checkRestart, 5000);

    $(document).on('click', '.restart-page', function() {
        location.reload();
    });

});