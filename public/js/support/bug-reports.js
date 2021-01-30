$(document).ready(() => {

    $(document).on('click', '.view-bug-report', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/support/bug-reports/view', { id }, 'View Bug Report', [closeButton()], 'center');

    });

    $('#bug-reports-new').click(() => {

        postNoty('/support/bug-reports/create', {}, 'Create Bug Report', [{
            addClass: 'btn btn-success',
            text: 'Submit',
            onClick: () => {
                let title = $('#title').val();
                let type = $('#type').find('option:selected').val();
                let replicate = $('#replicate').is(':checked');
                let date = $('#seen').val();
                let additional = $('#additional').val();
                if (!title || !additional || !date) {
                    sendAlert('All fields must be filled out.');
                    return false;
                }
                if (title.length < 5 || additional.length < 5) {
                    sendAlert('Both title and additional information must be at least 5 characters.');
                    return false;
                }

                post('/support/bug-reports/submit', { title, type, replicate, date, additional }, data => {

                    closeNoty();
                    sendAlert('Bug report successfully submitted!');
                    $('#bug-reports-refresh').click();

                });
            }
        }, closeButton()], 'centerBig');

    });

});