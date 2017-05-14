//AJAX WITH PROMISE GLOBAL FUNCTION + SEND ALERT GLOBAL FUNCTION
function reloadOverview() {

}

function getJSON(ret) {
    var data = JSON.parse(ret);
    if(data.success == null) {
        sendAlert('Session expired! Please reload the page to login again.');
        return null;
    }
    if(!data.success) {
        sendAlert(data.error);
        return null;
    }
    return data;
}

function sendAlert(text) {
    var n = noty({
        text: text,
        layout: 'topRight',
        timeout: 5000,
        theme: 'cryogen'
    });
}
