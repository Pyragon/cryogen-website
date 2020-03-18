export class ModuleHelper {
    constructor(name, endpoint, hasArchive = true, searchName = undefined) {
        this.name = name;
        this.endpoint = endpoint;
        this.hasArchive = hasArchive;
        this.searchName = searchName;
        this.page = 1;
        this.archive = false;
    }

    async setup(query) {
        let sm = await import("/js/search-manager.js");
        this.searchManager = new sm.SearchManager(this);
        this.searchManager.setup();
        this.utils = await import("/js/utils.js");
        this.loadList();

        if (this.hasArchive)
            $(`#${this.name} #archive-btn`).click(() => this.switchArchive());
        $(`#${this.name} #search-icon`).click(() =>
            this.searchManager.clickSearchIcon()
        );
        let that = this;
        $(document).on("click", `#${this.name} #pages span`, function () {
            let page = $(this).data("page");
            that.changePage(page);
        });
        $(document).on("click", `#${this.name} .remove-filter`, function () {
            that.searchManager.clickRemoveFilter($(this));
        });
        if (query) this.searchManager.search(query);
    }

    loadList(archive, page) {
        this.archive = archive || this.hasArchive ? this.archive : false;
        this.page = page || this.page;
        if ($(`#${this.name} .search-filter`).length > 0)
            return this.searchManager.search(null, null, this.page, this.archive);
        $.post(
            this.endpoint + "/load-list", {
                archive: this.archive,
                page: this.page
            },
            ret => {
                let data = getJSON(ret);
                if (data == null) return false;
                $(`#${this.name} #main`).html(data.html);
                this.updatePage(data.pageTotal, this.page);
            }
        );
    }

    updatePage(total, page) {
        $(`#${this.name} #pages`).html(
            "Pages: " + this.utils.getPages(total, page).html()
        );
    }

    switchArchive() {
        if (!this.hasArchive) return false;
        this.archive = !this.archive;
        this.page = 1;
        this.searchManager.removeFilters();
        $(`#${this.name} #archive`).html(` View ${this.archive ? "Active" : "Archive"}`);
        this.loadList();
    }

    changePage(page) {
        if (this.page == page) return false;
        this.page = page;
        this.loadList();
    }

    getSearchName() {
        return this.searchName;
    }

    getPage() {
        return this.page;
    }

    isArchive() {
        return this.archive;
    }

    getName() {
        return this.name;
    }
}