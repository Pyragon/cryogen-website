export class ListManager {

    constructor(module, id) {
        this.module = module;
        this.id = id;
        this.page = 1;
        this.archived = false;
    }

    setup() {
        this.loadList();

        let that = this;

        $(document).on('click', '#' + this.id + '-sort-reset-btn', function() {
            $('.sortable').each(function() {
                $(this).find('option:selected').removeAttr('selected');
                $(this).find('option[value="none"]').attr('selected', true);
            });
            that.loadList();
            $('.sorted').css('display', 'none');
            return false;
        });

        $(document).on('click', '#' + this.id + '-filter-reset-btn', function() {
            $('.filterable').each(function() {
                $(this).find('input').val('');
                $(this).find('option:selected').removeAttr('selected');
            });
            that.loadList();
            $('.filtered').css('display', 'none');
            return false;
        });

        $(document).on('click', '#' + this.id + '-filter-btn', function() {
            that.loadList();
            $('.filtered').css('display', 'none');
            return false;
        });

        $(document).on('click', '#' + this.id + '-sort-btn', function() {
            that.loadList();
            $('.sorted').css('display', 'none');
            return false;
        });
    }

    onLoad() {

        let that = this;
        $(`#${this.id}-sort`).sortable();

        $('.archive-btn').click(function() {
            that.archived = !that.archived;
            that.loadList();
            $(this).find('span').html(' View ' + (that.archived ? 'Active' : 'Archive'));
            return false;
        });

        $('.refresh').click(() => {
            this.loadList(() => sendAlert('Refreshed.'))
        });

        $('.sort').click(function() {
            let sort = $(`#${that.id}-sort`);
            let display = sort.css('display');
            if (display == 'block') {
                sort.css('display', 'none');
                return false;
            }
            $('.filtered').css('display', 'none');
            sort.css('display', 'block');
            sort.css('top', $(this).position().top + 20 + 'px');
            sort.css('left', $(this).position().left - 110 + 'px');

            return false;
        });

        $('.filter').click(function() {
            let filter = $(`#${that.id}-filter`);
            let display = filter.css('display');
            if (display == 'block') {
                filter.css('display', 'none');
                return false;
            }
            $('.sorted').css('display', 'none');
            filter.css('display', 'block');
            filter.css('top', $(this).position().top + 20 + 'px');
            filter.css('left', $(this).position().left - 110 + 'px');
            return false;
        });
    }

    loadList(cb, toPage) {
        toPage = toPage || this.page;
        let sortValues = [];
        $('.sortable').each(function(i) {
            let name = $(this).find('.sort-name').html();
            let optionValue = $(this).find('option:selected').attr('value');
            sortValues.push([name, optionValue, i]);
        });

        let filterValues = [];
        $('.filterable').each(function(i) {
            let name = $(this).find('.filter-name').html();
            let filterValue = $(this).find('input').val();
            if (typeof filterValue === 'undefined') {
                let option = $(this).find('option:selected');
                filterValue = option.attr('value');
            }
            filterValues.push([name, filterValue, i]);
        });
        post(this.module + '/table', { archived: this.archived, sortValues: JSON.stringify(sortValues), filterValues: JSON.stringify(filterValues), page: toPage }, data => {
            $(`#${this.id}-table`).html(data.html);
            if (data.sort)
                $(`#${this.id}-sort`).html(data.sort);
            if (data.filter)
                $(`#${this.id}-filter`).html(data.filter);
            $(`#${this.id}-active-filter`).css('display', data.activeFilter ? 'inline' : 'none');

            this.page = parseInt(data.page);

            this.buildPages(this.page, data.total);

            if (cb) cb(data);
        });
    }

    buildPages(current, total) {
        let pages = [];
        for (let i = current - 2; i <= current + 2; i++) {
            if (i < 1 || i > total) continue;
            pages.push(i);
        }
        if (current > 3)
            pages.unshift(1);
        if (current < total - 2)
            pages.push(total);

        let conatiner = $(`#${this.id}-pages`).find('.page-container');
        conatiner.empty();
        for (let page of pages) {

            let ele = $('<span class="page-button"></span>');
            ele.data('id', page);
            let value = page;
            if (page == current)
                value = `[${page}]`;
            else
                ele.addClass('vis-link');
            ele.html(value);

            ele.click(() => this.changePage(page));

            conatiner.append(ele);
        }
    }

    changePage(pageNumber) {

        this.loadList(undefined, pageNumber);

        return false;
    }

}