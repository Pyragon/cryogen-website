let formatNameForProtocol = (name) => {
    return name.toLowerCase().replace(' ', '_');
};

let crownUser = (user) => {
    return user;
};

let formatNumber = (number) => {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

module.exports = { formatNameForProtocol, crownUser, formatNumber };