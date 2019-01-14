function loadList(endpoint, archived, page, extra) {
    var options = {
        action: 'load-list',
        archive: archived,
        page
    }
    $.post(name, endpoint, options, (ret) => {
        var data = getJSON(ret);
        if (data == null) return false;
        $(`#${name}-main`).html(data.html);
        updatePage(data.pageTotal, page, name);
    });
}