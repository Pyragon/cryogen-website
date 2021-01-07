$(document).on('click', '.increase-quantity, .decrease-quantity', function() {
    let id = $(this).closest('.item').data('id');

    let action = $(this).attr('class').replace('-quantity link', '');

    post('/account/cart/' + action, { id }, null, data => update($(this).closest('.item'), data));
});

$(document).on('click', '.add-item', function() {

    let id = $(this).closest('.item').data('id');

    post('/account/cart/increase', { id }, null, data => update($(this).closest('.item'), data));

});

$(document).on('click', '.checkout', function() {
    post('/account/cart/view', {}, null, data => {
        openNoty('Review Cart', data.html, [{
                addClass: 'btn btn-primary',
                text: 'Continue',
                onClick: function($noty) {
                    post('/account/cart/checkout', {}, null, data => {
                        window.location = data.link;
                        return false;
                    });
                }
            },
            {
                addClass: 'btn btn-danger',
                text: 'Cancel',
                onClick: closeNoty
            }
        ]);
    });
});

function update(item, data) {

    item.find('.shop-quantity').find('input').val(data.quantity);

    if (data.quantity <= 0) {
        item.find('button').css('display', '');
        item.find('.shop-quantity').css('display', 'none');
    } else {
        item.find('button').css('display', 'none');
        item.find('.shop-quantity').css('display', '');
    }

    $('.cart-info').find('span').html(` Cart (${data.totalItems}): $${data.totalPrice}`)
}