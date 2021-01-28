$(document).ready(() => {

    $('#punishments-new').click(() => {

        let buttons = [];
        let rights = parseInt('!{user.getRights()}');
        if (rights > 1) {
            buttons.push({
                addClass: 'btn btn-success',
                text: 'Create',
                onClick: () => {
                    let name = $('#player-name').val();
                    let type = $('#type').find('option:selected').val();
                    let appealable = $('#appealable').is(':checked');
                    let expiry = $('#expiry').val();
                    let reason = $('#reason').val();
                    let info = $('#info').val();
                    if (!name || !type || !reason || !info) {
                        sendAlert('All fields other than expiry must be filled out.');
                        return false;
                    }
                    let props = { name, type, appealable, reason, info };
                    if (expiry)
                        props.expiry = expiry;
                    post('/staff/punishments/submit', props, () => {
                        closeNoty();
                        sendAlert('Punishment has been submitted.');
                        $('.refresh').click();
                    });
                }
            });
        }

        buttons.push(closeButton());

        postNoty('/staff/punishments/new', {}, 'New Punishment', buttons, 'centerBig');

    });

    $(document).on('click', '.view-punishment', function() {

        let id = $(this).closest('tr').data('id');
        let archived = $(this).closest('tr').data('archived');

        let buttons = [];

        let clicked = 0;

        let reverse = () => {
            if (clicked++ == 0) {
                sendAlert('Please press again to confirm.');
                return false;
            }
            post('/staff/punishments/reverse', { id }, data => {
                closeNoty();
                sendAlert('Punishment has been reversed.');
                $('#punishments-refresh').click();
            });
        };

        if (!archived) {
            buttons.push({
                addClass: 'btn btn-danger',
                text: 'Reverse',
                onClick: reverse
            });
        }

        buttons.push(closeButton());

        postNoty('/staff/punishments/view', { id }, 'View Punishment', buttons, 'centerBig');

    });

    $(document).on('click', '.view-appeal', function() {

    });

});