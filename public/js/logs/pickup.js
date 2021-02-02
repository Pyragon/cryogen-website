$(document).on('mouseover', '.pickup-world-tile', function() {

    if ($('.world-tile-hover').length) return;

    let link = $(this).closest('tr').data('extra');

    if (!link) return false;

    let container = $('<div></div>');
    container.addClass('world-tile-hover');

    let image = $('<img></img>');
    image.attr('src', link);

    container.append(image);

    container.css('top', $(this).position().top + 10);
    container.css('left', $(this).position().left + $(this).width());

    $('#pickup').append(container);

});

$(document).on('mouseleave', '.pickup-world-tile', function() {
    if (!$('.world-tile-hover').length) return;

    $('.world-tile-hover').remove();

});