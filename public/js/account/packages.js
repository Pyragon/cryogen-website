$(document).on('click', '.redeem-btn', function() {
    let id = $(this).closest('tr').data('id');

    openConfirmationNoty(null, (notyId) => {

        closeNoty(notyId);
        post('/account/packages/redeem', { id }, '.list-table');

    });

});