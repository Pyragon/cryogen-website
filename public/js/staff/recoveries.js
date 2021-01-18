$(document).ready(() => {

    $(document).on('click', '.view-recovery', function() {

        let id = $(this).closest('tr').data('id');
        let archived = $(this).closest('tr').data('archived');

        let buttons = [];

        let respondId;

        if (!archived) {
            //TODO = get active or not
            buttons.push({
                addClass: 'btn btn-primary',
                text: 'Respond',
                onClick: ($noty) => {
                    closeNoty($noty);

                    let types = [];

                    let respond = function(type) {
                        if (!types[type] || types[type] == false) {
                            types = [];
                            types[type] = true;
                            sendAlert('Please select again to confirm.');
                            return false;
                        }
                        let reason = $('#respond-area').val();
                        if (!reason || reason.length < 6 || reason.length > 200) {
                            sendAlert('Reason must be between 6 and 200 characters.');
                            return false;
                        }
                        post('/staff/recoveries/respond', { id, type, reason }, data => {

                            closeNoty(respondId);
                            sendAlert('Successfully responded.');
                            loadList();

                        });
                    };

                    let respondButtons = [];

                    respondButtons.push({
                        addClass: 'btn btn-success',
                        text: 'Accept',
                        onClick: ($noty) => respond('accept')
                    });

                    respondButtons.push({
                        addClass: 'btn btn-danger',
                        text: 'Decline',
                        onClick: ($noty) => respond('decline')
                    });

                    respondButtons.push(closeButton());

                    closeNoty($noty);

                    setTimeout(() => {
                        post('/staff/recoveries/respond/noty', { id }, null, data => {
                            respondId = openNoty('How would you like to respond?', data.html, respondButtons);
                        });
                    }, 500);
                }
            });
        }

        buttons.push(closeButton());

        postNoty('/staff/recoveries/view', { id }, 'View Recovery', buttons, 'centerReallyBig');

    });

});