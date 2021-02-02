$(document).on('click', '.view-duel', function() {

    let id = $(this).closest('tr').data('id');

    postNoty('/logs/duel/view', { id }, 'View Duel Logs', [closeButton()], 'centerBig');

});