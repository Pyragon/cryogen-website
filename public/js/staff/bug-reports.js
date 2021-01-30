$(document).ready(() => {

    $(document).on('click', '.view-bug-report', function() {

        let id = $(this).closest('tr').data('id');
        let archived = $(this).closest('tr').data('archived');

        let buttons = [];

        if (!archived)
            buttons.push({
                addClass: 'btn btn-danger',
                text: 'Archive',
                onClick: () => {

                    post('/staff/bug-reports/archive', { id }, data => {

                        closeNoty();
                        sendAlert('Bug Report has been successfully archived.');
                        $('#bug-reports-refresh').click();

                    });

                }
            })

        buttons.push(closeButton());

        postNoty('/staff/bug-reports/view', { id }, 'View Bug Report', buttons, 'center');

    });

});