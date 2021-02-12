function createStorageObject() {
    return {

        storageAvailable: function(type) {
            var storage;
            try {
                storage = window[type];
                var x = '__storage_test__';
                storage.setItem(x, x);
                storage.removeItem(x);
                return true;
            } catch (e) {
                return e instanceof DOMException && (
                        e.code === 22 ||
                        e.code === 1014 ||
                        e.name === 'QuotaExceededError' ||
                        e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
                    (storage && storage.length !== 0);
            }
        },

        getSetting: function(setting) {
            if (!storageAvailable('localStorage') || typeof localStorage.getItem(setting) === 'undefined') {
                console.log('GETTING FROM DEFAULTS');
                return defaults[setting];
            }
            return localStorage.getItem(setting);
        },

        setSetting: function(setting, value) {
            if (!this.storageAvailable('localStorage')) return;
            localStorage.setItem(setting, value);
        },

        defaults: function() {
            return {
                'scroll': 10,
                'keyboard-state': 0,
                'volume': .5,
                'muted': false
            };
        }

    };
}