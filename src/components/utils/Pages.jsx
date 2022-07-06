import React, { useContext } from 'react';

import PageContext from '../../utils/contexts/PageContext';

function getPages(pageTotal, currentPage) {
    currentPage = parseInt(currentPage);
    if(pageTotal === 1) return [ 1 ];
    let pages = [];
    //-1 = prev, -2 = next, -3 = first, -4 = last
    if(currentPage !== 1)
        pages.push(-3);
    if(currentPage > 2)
        pages.push(-1);
    let start = currentPage-2, end = currentPage + 2;
    if(end > pageTotal) {
        start -= (end-pageTotal);
        end = pageTotal;
    }
    if(start <= 0) {
        end += ((start-1)*(-1));
        start = 1;
    }
    end = end > pageTotal ? pageTotal : end;
    for(let i = start; i <= end; i++)
        pages.push(i);
    if(currentPage < pageTotal-1)
        pages.push(-2);
    if(currentPage !== pageTotal)
        pages.push(-4);
    return pages;
}

export default React.forwardRef(({ pageTotal, base, scroll }, ref) => {
    let { page: currentPage, setPage } = useContext(PageContext);
    currentPage = parseInt(currentPage);
    let pages = getPages(pageTotal, currentPage);

    async function gotoPage(page) {
        setPage(page);
        if(base)
            window.history.replaceState(null, '', `${base}/${page}`);
        if(scroll)
            scroll();
    }

    return (
        <div>
            <div className='pages'>
                <div>
                    <span>Pages: </span>
                    { pages.map(page => (
                        <span key={page}>
                            {page === -3 && <span className='link' onClick={() => gotoPage(1)}>First </span>}
                            {page === -1 && <span className='link' onClick={() => gotoPage(currentPage-1)}>Prev </span>}
                            {page === -2 && <span className='link' onClick={() => gotoPage(currentPage+1)}>Next </span>}
                            {page === -4 && <span className='link' onClick={() => gotoPage(pageTotal)}>Last </span>}
                            {page === currentPage && <span>{'['+page+'] '}</span>}
                            {page > 0 && page !== currentPage && <span className='link' onClick={() => gotoPage(page)}>{page+' '}</span>}
                        </span>
                    ))}
                </div>
            </div>
            <div style={{clear: 'both' }} />
        </div>
    )
});
