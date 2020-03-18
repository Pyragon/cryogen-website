let helper;
let filters = [];
let changed = false;

export class SearchManager {

    constructor(helper) {
        this.helper = helper;
    }

    setup() {
        this.filters = [];
        this.changed = false;
        let that = this;
        $(`#${this.helper.getName()} #search-pin input`).keyup(function(e) {
            if (e.which == 13) return that.search($(this).val());
        });
    }

    search(input, params, page = 1, archive = false) {
        if (!input) input = this.filtersToQuery();
        if (!input || input.replace(/[, ]/g, '') == '') {
            this.helper.loadList(page, archive);
            this.updateInput('');
            return false;
        }
        let searchName = this.helper.getSearchName() || this.helper.getName();
        let paramStr = !params ? "" : JSON.stringify(params);
        if (!this.filters || this.filters.length == 0) page = 1;
        $.post(
            `/search/${this.helper.getName()}`, {
                query: input,
                page,
                archived: archive,
                params: paramStr,
                searchName
            },
            ret => {
                let data = getJSON(ret);
                if (data == null) return false;
                $(`#${this.helper.getName()} #main`).html(data.html);
                this.updateInput(this.updateFilters(data.filters));
                this.helper.updatePage(data.pageTotal, page);
            }
        );
    }

    updateInput(input) {
        $(`#${this.helper.getName()} #search-pin input`).val(input);
    }

    updateFilters(filters) {
        this.filters = filters;
        let element = $(`#${this.helper.getName()} .search-filters`);
        let search = "";
        element.empty();
        for (let k in this.filters) {
            let name = k;
            let value = this.filters[k];
            let filter = $(`<div>${name}:${value}</div>`).addClass("search-filter");
            filter.data("name", name);
            filter.data("value", value);
            element.append(filter);
        }
        this.addFilterRemoval();
        return this.filtersToQuery();
    }

    addFilterRemoval() {
        $(`#${this.helper.getName()} .search-filter`).append(
            $("<i></i>").addClass("remove-filter fa fa-times-circle")
        );
    }

    clickRemoveFilter(element, params) {
        let filter = element.closest(".search-filter");
        if (!filter) return false;
        let filterName = filter.data("name");
        if (!filterName) return false;
        this.removeFilter(filterName, params);
    }

    removeFilter(filterName, params) {
        delete this.filters[filterName];
        let query = this.updateFilters(filters);
        this.search(query, params, 1, this.helper.isArchive());
    }

    removeFilters() {
        this.filters = [];
        $(`#${this.helper.getName()} .search-filters`).empty();
        this.updateInput("");
    }

    clickSearchIcon(params) {
        let icon = $(`#${this.helper.getName()} #search-icon`);
        let searchDiv = icon.next();
        let input = searchDiv.find("input");
        let value = input.val();
        let display = searchDiv.css("display");
        let searchName = this.helper.getSearchName() || this.helper.getName();
        if (display === "none") {
            icon.attr("title", CLOSE);
            searchDiv.show(
                "slide", {
                    direction: "right"
                },
                1000,
                () => input.focus()
            );
        } else {
            if (value === "") {
                searchDiv.hide(
                    "slide", {
                        direction: "right"
                    },
                    1000,
                    () => {
                        searchDiv.css("display", "none");
                        icon.attr("title", OPEN);
                    }
                );
                if (this.changed) {
                    this.helper.loadList(this.helper.isArchive(), this.helper.getPage());
                    this.changed = false;
                }
                return false;
            }
            this.search(value, params);
        }
    }

    filtersToQuery() {
        let search = '';
        let i = 0;
        for (let k in this.filters) {
            let name = k;
            let value = this.filters[k];
            search += `${name}:${value}`;
            let size = Object.size(this.filters);
            if (i++ != size - 1) search += ', ';
        }
        return search;
    }

}