let tabs = [];

function buildPage(tabs, activeKey) {
    for (let key in tabs) {
        let active = key == activeKey;
        if (active)
            history.pushState({}, 'CryogenSPA', '/account/' + key);
        let link = $('<a></a>');
        link.attr('href', '#' + key);
        link.data('toggle', 'tab');
        link.html(tabs[key]);
        let item = $('<li class="nav tab-item"></li>');
        if (active)
            item.addClass('active');
        item.append(link);
        $('.tab-area').find('.nav-tabs').append(item);
        let div = $('<div class="tab-pane fade in" id="' + key + '"></div>');
        if (active)
            div.addClass('active');
        $('.tab-content').append(div);
    }
    $('.nav-tabs a').click(function () {
        showTab($(this).attr('href').replace('#', ''));
        return false;
    });
    showTab(activeKey);
}

function showTab(key) {
    if (!tabs[key]) {
        post('/account/' + key + '/load', {}, null, data => {
            tabs[key] = true;
            $('#' + key).html(data.html);
            $('.nav-tabs a[href="#' + key + '"]').tab('show');
            history.pushState({}, 'CryogenSPA', '/account/' + key);
            return;
        });
        return;
    }
    $('.nav-tabs a[href="#' + key + '"]').tab('show');
    history.pushState({}, 'CryogenSPA', '/account/' + key);
}