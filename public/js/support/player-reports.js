$(document).ready(() => {

    $(document).on('click', '.view-player-report', function() {

        let id = $(this).closest('tr').data('id');

        postNoty('/support/player-reports/view', { id }, 'View Player Report', [closeButton()], 'centerBig');

    });

});