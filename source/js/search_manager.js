//filter functionality

var search_filters = [];

function updateFilters(mod, filters) {
	search_filters[mod] = filters;
	var elem = $('.search-filters-' + mod);
	var search = '';
	elem.empty();
	for (var k in filters) {
		if (filters.hasOwnProperty(k)) {
			var name = k;
			var value = filters[k];
			var filter = $('<div></div>').addClass('search-filter').append(`${name}:${value}`);
			filter.attr('data-name', name);
			filter.attr('data-value', value);
			filter.attr('data-mod', mod);
			elem.append(filter);
		}
	}
	addFilterRemoval();
	return filtersToQuery(mod);
}

function filtersToQuery(mod) {
	var search = '';
	var filters = search_filters[mod];
	var i = 0;
	for (var k in filters) {
		var name = k;
		var value = filters[k];
		if (filters.hasOwnProperty(k)) {
			search += `${name}:${value}`;
			if (i++ != filters.length - 1)
				search += ', ';
		}
	}
	return search;
}

function addFilterRemoval() {
	$('.search-filter').append($('<i></i>').addClass('remove-filter fa fa-times-circle'));
}

function clickRemoveFilter(mod, elem, archive, loadListFunc, params) {
  var filter = elem.closest('.search-filter');
  if(typeof filter === 'undefined')
    return false;
  var filterName = filter.data('name');
  if(filterName === '')
    return false;
  console.log('removing filter')
  removeFilter(mod, filterName, archive, loadListFunc, params);
  return false;
}

function removeFilter(mod, name, archive, loadListFunc, params) {
	var filters = search_filters[mod];
	delete filters[name];
	var query = updateFilters(mod, filters);
  console.log(query+' '+name);
	search(mod, 1, archive, query, params, loadListFunc);
}

function removeFilters(mod) {
	delete search_filters[mod];
	$('.search-filters-' + mod).empty();
	updateSearchInput(mod, '');
}

//end filter functionality

//search icon

function clickSearchIcon(mod, archive, page, loadListFunc, params, searchname=null) {
	var icon = $(this);
	var search = $('#search-' + mod + '-pin');
	var input = search.find('input');
	var user = input.val();
	var att = search.attr('display');
	if(searchname == null)
		searchname = mod
	if (att === 'none') {
		icon.attr('title', CLOSE);
		search.attr('display', 'inline');
		search.show('slide', {
			direction: 'right'
		}, 1000, function () {
			input.focus();
		});
	} else {
		if (user === '') {
			search.hide('slide', {
				direction: 'right'
			}, 1000, function () {
				search.attr('display', 'none');
				icon.attr('title', OPEN);
			});
			if (changed) {
				loadListFunc(archive, page);
				changed = false;
			}
			return false;
		}
    console.log('searching for: '+user)
		this.search(mod, page, archive, user, params, loadListFunc, searchname);
	}
	return false;
}

function updateSearchInput(mod, input) {
	$('#search-' + mod + '-pin').find('input').val(input);
}

//actual search function

var searching = [];

function search(mod, page, archive, input=null, params=null, loadListFunc=null, searchname=null) {
  if(input === null)
    input = filtersToQuery(mod);
  if(input === '' || input === ',' || input === ', ') {
    loadListFunc(archive, page);
    updateSearchInput(mod, '')
    searching[mod] = false;
    return false;
  }
  searching[mod] = true;
  changed = true;
  paramStr = params === null ? '' : JSON.stringify(params)
  $.post('/search/'+mod, { query:input, page:page, archived:archive,params:paramStr,searchname:searchname}, (ret) => {
    var data = getJSON(ret)
    if(data == null) return null;
    update(false, data.html, mod)
    updateSearchInput(mod, updateFilters(mod, data.filters))
    updatePage(data.pageTotal, page, mod)
  })
}
