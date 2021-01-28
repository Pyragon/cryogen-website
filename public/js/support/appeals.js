$(document).ready(() => {

    $(document).on('click', '.view-appeal-punishment', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/support/punishments/view', { id, appeal: true }, 'View Punishment', [closeButton()], 'centerBig');

    });

    $(document).on('click', '.view-appeal', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/support/appeals/view', { id }, 'View Appeal', [closeButton()], 'centerBig');

    });

});