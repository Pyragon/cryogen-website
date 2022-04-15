const moment = require('moment');

let formatNameForProtocol = (name) => {
    return name.toLowerCase().replace(' ', '_');
};

let crownUser = (user) => {
    return user;
};

let formatNumber = (number) => {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

let formatDate = (date, format = 'MMMM Do, YYYY h:mm:ss a') => {
    return moment(date).calendar(null, {
        sameDay: '[Today at] h:mm a',
        nextDay: '[Tomorrow at] h:mm a',
        nextWeek: format,
        lastDay: '[Yesterday at] h:mm a',
        lastWeek: format,
        sameElse: format
    });
};

let formatMessage = (message) => {
    //Uppercase first letter and all letters after a period
    // let formattedMessage = message.toLowerCase();
    let formattedMessage = message.charAt(0).toUpperCase() + message.slice(1);
    formattedMessage = formattedMessage.replace(/\. ?[a-z]{1}/g, c => c.toUpperCase());
    return formattedMessage;
};

module.exports = { formatNameForProtocol, crownUser, formatNumber, formatDate, formatMessage };