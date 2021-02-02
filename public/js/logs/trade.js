$(document).on('click', '.view-trade', function() {

    let id = $(this).closest('tr').data('id');

    postNoty('/logs/trade/view', { id }, 'View Trade Log', [closeButton()], 'centerReallyBig');

});