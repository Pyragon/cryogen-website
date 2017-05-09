//AJAX WITH PROMISE GLOBAL FUNCTION + SEND ALERT GLOBAL FUNCTION
function reloadOverview() {

}

function sendAlert(text) {
    var n = noty({
        text: text,
        layout: 'topRight',
        timeout: 5000,
        theme: 'cryogen'
    });
}
