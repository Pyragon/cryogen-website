export function getPages(page_t, page) {
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