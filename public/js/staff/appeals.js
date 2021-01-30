$(document).ready(() => {

    $(document).on('click', '.view-appeal-punishment', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/staff/punishments/view', { id, appeal: true }, 'View Punishment', [closeButton()], 'centerBig');

    });

    $(document).on('click', '.view-appeal', function() {

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
                    post('/staff/appeals/respond', { id, reason, response }, data => {
                        closeNoty();
                        sendAlert('Successfully responded to appeal.');
                        $('#appeals-refresh').click();
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

        postNoty('/staff/appeals/view', { id }, 'View Appeal', buttons, 'centerBig');

    });

});