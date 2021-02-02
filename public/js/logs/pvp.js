$(document).on('click', '.view-pvp', function() {

    let id = $(this).closest('tr').data('id');

    postNoty('/logs/pvp/view', { id }, 'View PVP Log', [closeButton()], 'centerBig');

});

$(document).on('mouseover', '.pvp-world-tile', function() {

    if ($('.world-tile-hover').length) return;

    let link = $(this).closest('tr').data('extra');

    let container = $('<div></div>');
    container.addClass('world-tile-hover');

    let image = $('<img></img>');
    image.attr('src', link);

    container.append(image);

    container.css('top', $(this).position().top + 10);
    container.css('left', $(this).position().left + $(this).width());

    $('#pvp').append(container);

});

$(document).on('mouseleave', '.pvp-world-tile', function() {
    if (!$('.world-tile-hover').length) return;

    $('.world-tile-hover').remove();

});