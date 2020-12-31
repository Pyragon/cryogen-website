function sendAlert(text, onClick) {
    let options = {
        text: text,
        layout: 'topRight',
        timeout: 5000,
        theme: 'cryogen'
    };
    if(onClick) {
        options.closeWith = [];
        options.callback = {
            onClick
        };
    }
    let n = noty(options);
    return n.options.id;
}

function closeAlert(id) {
    let n = $.noty.store[id];
    if(!n) return false;
    n.close();
    return true;
}