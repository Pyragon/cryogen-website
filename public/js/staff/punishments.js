$(document).ready(() => {

    $('#punishments-new').click(() => {

        let buttons = [];
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

    $(document).on('click', '.view-punishment-appeal', function() {

        let id = $(this).closest('tr').data('id');
        let archived = $(this).closest('tr').data('archived');

        let buttons = [];

        function loadResponseNoty(response) {
            closeNoty();
            let title = response == 'accept' ? 'Accepting appeal' : 'Declining appeal';

            let buttons = [];

            buttons.push({
                addClass: 'btn btn-success',
                text: 'Respond',
                onClick: () => {
                    let reason = $('#reason').val();
                    if (!reason || reason.length < 5) {
                        sendAlert('Reason must be at least 5 characters.');
                        return false;
                    }
                    post('/staff/appeals/respond', { id, punishment: true, reason, response }, data => {
                        closeNoty();
                        sendAlert('Successfully responded to appeal.');
                        $('#punishments-refresh').click();
                        console.log('twice');
                    });
                }
            });

            buttons.push(closeButton());

            postNoty('/staff/appeals/view-respond', {}, title, buttons, 'centerBig');
        }

        if (!archived) {
            buttons.push({
                addClass: 'btn btn-success',
                text: 'Accept',
                onClick: () => loadResponseNoty('accept')
            });
            buttons.push({
                addClass: 'btn btn-danger',
                text: 'Decline',
                onClick: () => loadResponseNoty('decline')
            });
        }

        buttons.push(closeButton());

        postNoty('/staff/appeals/view', { id, punishment: true }, 'View Appeal', buttons, 'centerBig');

    });

    $(document).on('click', '.view-punishment-report', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/staff/player-reports/view', { id, punishment: true }, 'View Player Report', [closeButton()], 'centerBig');

    });

});