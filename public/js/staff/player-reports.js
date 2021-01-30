$(document).ready(() => {

    $(document).on('click', '.view-player-report', function() {

        let id = $(this).closest('tr').data('id');
        let archived = $(this).closest('tr').data('archived');

        let buttons = [];

        if (!archived) {

            buttons.push({
                addClass: 'btn btn-success',
                text: 'Continue',
                onClick: () => viewCreatePunishment(id)
            });

            buttons.push({
                addClass: 'btn btn-danger',
                text: 'Archive',
                onClick: () => {

                    post('/staff/player-reports/archive', { id }, data => {

                        closeNoty();
                        sendAlert('Player report has been successfully archived.');
                        $('#player-reports-refresh').click();

                    });

                }
            });
        }

        buttons.push(closeButton());

        postNoty('/staff/player-reports/view', { id }, 'View Player Report', buttons, 'centerBig');

    });

    function viewCreatePunishment(id) {
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
                let props = { id, name, type, appealable, reason, info };
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

        closeNoty();
        postNoty('/staff/punishments/new', { id }, 'New Punishment', buttons, 'centerBig');
    }

    $(document).on('click', '.view-report-punishment', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/staff/punishments/view', { id, report: true }, 'View Punishment', [closeButton()], 'center');

    });

});