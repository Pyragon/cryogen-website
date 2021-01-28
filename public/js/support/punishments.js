$(document).ready(() => {

    $(document).on('click', '.view-punishment', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/support/punishments/view', { id }, 'View Punishment', [closeButton()], 'centerBig');

    });

    $(document).on('click', '.appeal', function() {

        let id = $(this).closest('tr').data('id');

        let buttons = [];

        buttons.push({
            addClass: 'btn btn-success',
            text: 'Submit',
            onClick: () => {
                let title = $('#title').val();
                let additional = $('#additional').val();
                if (!title || !additional) {
                    sendAlert('All fields must be filled out.');
                    return false;
                }
                if (title.length < 5 || additional.length < 5) {
                    sendAlert('Both the title and the additional information must be at least 5 characters.');
                    return false;
                }

                post('/support/punishments/submit-appeal', { id, title, additional }, data => {

                    sendAlert('Appeal has been successfully submitted.');
                    closeNoty();
                    $('#punishments-refresh').click();

                });
            }
        })

        buttons.push(closeButton());

        postNoty('/support/punishments/create-appeal', { id }, 'Create Appeal', buttons, 'centerBig');

    });
});