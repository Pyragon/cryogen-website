let storage = createStorageObject();

let warning = false;

let CENSORED = [{
        regex: [/https?:\/\//,
            /.(com)|(ca)|(.co.uk)/
        ],
        error: 'Links are not allowed to be posted. Please click here to learn more',
        link: true
    },
    {
        regex: /faggot/,
        replace: true,
        filter: true
    },
    {
        regex: /fuck/,
        replace: true,
        filter: true
    },
    {
        regex: /shit/,
        replace: true,
        filter: true
    }
];

function censorSentMessage(message, rights) {
    for (let censor of CENSORED) {
        let regexes = censor.regex;
        if (!Array.isArray(regexes))
            regexes = [regexes];
        for (regex of regexes) {
            if (message.match(regex)) {
                if (censor.error) {
                    let cb = undefined;
                    if (censor.link)
                        cb = () => window.open('http://cryogen-rsps.com/forums/');
                    sendAlert(censor.error, cb);
                    return false;
                }
            }
        }
    }
    return message;
}

//Enable chat filter button, add many more

function censorChatMessage(message, rights) {
    // if (rights == 2) return message;
    for (let censor of CENSORED) {
        let regexes = censor.regex;
        if (!Array.isArray(regexes))
            regexes = [regexes];
        for (regex of regexes) {
            if (message.match(regex)) {
                if (censor.filter) {
                    let filter = storage.getSetting('filter') == 'true';
                    if (!filter) continue;
                }
                if (censor.replace) {
                    while (message.match(regex)) {
                        message = message.replace(regex, '******');
                        if (!warning) {
                            sendAlert('A portion of a chat message has been censored due to our movie night rules and your filter settings. Please click this alert to learn more.',
                                () => window.open('http://cryogen-rsps.com/forums/'));
                            warning = true;
                        }
                    }
                }
            }
        }
    }
    return message;
}