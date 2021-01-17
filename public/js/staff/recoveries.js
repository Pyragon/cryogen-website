$(document).ready(() => {

    $(document).on('click', '.view-recovery', function() {

        let id = $(this).closest('tr').data('id');
        let archived = $(this).closest('tr').data('archived');

        let buttons = [closeButton()];

        if (!archived) {
            //TODO = get active or not
            buttons.push({
                addClass: 'btn btn-primary',
                text: 'Respond',
                onClick: ($noty) => {}
            });
        }

        postNoty('/staff/recoveries/view', { id }, 'View Recovery', buttons, 'centerReallyBig');

    });

});