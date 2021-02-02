$(document).ready(() => {

    $('#player-reports-new').click(() => {

        let buttons = [];

        buttons.push({
            addClass: 'btn btn-success',
            text: 'Submit',
            onClick: () => {

                let verifyId = -1;
                if ($('#verify').length)
                    verifyId = $('#verify').val();

                let title = $('#title').val();
                let offender = $('#offender').val();
                let rule = $('#rule').val();
                let date = $('#date').val();
                let proof = $('#proof').val();
                let additional = $('#additional').val();
                if (isNullOrWhitespace([title, offender, rule, date, proof, additional])) {
                    sendAlert('All fields must be filled out.');
                    return false;
                }
                if (title.length < 5 || title.length > 20) {
                    sendAlert('Title must be between 5 and 20 characters.');
                    return false;
                }
                if (proof.length < 10 || proof.length > 200) {
                    sendAlert('Proof must be between 10 and 200 characters.');
                    return false;
                }
                if (additional.length < 5 || additional.length > 200) {
                    sendAlert('Additional information must be between 5 and 200 characters.');
                    return false;
                }
                let props = { title, offender, rule, date, proof, additional };
                if (verifyId != -1)
                    props.verifyId = verifyId;
                post('/support/player-reports/submit', props, data => {
                    closeNoty();
                    sendAlert('Report has been successfuly submitted.');
                    $('#player-reports-refresh').click();
                });

            }
        });

        buttons.push(closeButton());

        postNoty('/support/player-reports/new', {}, 'New Player Report', buttons, 'centerBig');

    });

    $(document).on('click', '.view-player-report', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/support/player-reports/view', { id }, 'View Player Report', [closeButton()], 'centerBig');

    });

    $(document).on('click', '.view-report-punishment', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/support/punishments/view', { id, report: true }, 'View Player Report', [closeButton()], 'centerBig');

    });

});