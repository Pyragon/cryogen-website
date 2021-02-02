$(document).on('click', '.view-login', function() {

    let id = $(this).closest('tr').data('id');

    postNoty('/logs/login/view', { id }, 'View Login', [closeButton()], 'centerBig');

});