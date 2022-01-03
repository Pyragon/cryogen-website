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

let formatDate = (date) => {
    return moment(date).format('MMMM Do YYYY, h:mm:ss a');
};

module.exports = { formatNameForProtocol, crownUser, formatNumber, formatDate };