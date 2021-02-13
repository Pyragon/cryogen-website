function createStorageObject() {
    return {

        warned: false,

        storageAvailable: function(type) {
            var storage;
            try {
                storage = window[type];
                var x = '__storage_test__';
                storage.setItem(x, x);
                storage.removeItem(x);
                return true;
            } catch (e) {
                if (!this.warned) {
                    sendAlert('Unable to use local storage. Some features will be unavailable on this page.');
                    this.warned = true;
                }
                return e instanceof DOMException && (
                        e.code === 22 ||
                        e.code === 1014 ||
                        e.name === 'QuotaExceededError' ||
                        e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
                    (storage && storage.length !== 0);
            }
        },

        getSetting: function(setting) {
            if (!storageAvailable('localStorage'))
                return this.defaults()[setting];
            else if (localStorage.getItem(setting) == null || typeof localStorage.getItem(setting) === 'undefined')
                return this.defaults()[setting];
            return localStorage.getItem(setting);
        },

        setSetting: function(setting, value) {
            if (!this.storageAvailable('localStorage'))
                return false;
            localStorage.setItem(setting, value);
        },

        defaults: function() {
            return {
                'scroll': 10,
                'keyboard-state': 0,
                'volume': .5,
                'muted': false,
                'filter': false,
            };
        }

    };
}