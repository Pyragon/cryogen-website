let CENSORED = [{
    regex: [/https?:\/\//,
        /.(com)|(ca)|(.co.uk)/
    ],
    error: 'Links are not allowed to be posted. Please click here to learn more',
    link: true
}];

//Enable chat filter button, add many more

function censorChatMessage(message, rights) {
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
                } else if (censor.replace) {
                    message = message.replace(regex, censor.replace);
                    sendAlert('A portion of your chat message has been censored due to our movie night rules. Please click this alert to learn more.',
                        () => window.open('http://cryogen-rsps.com/forums/'));
                }
            }
        }
    }
    return message;
}