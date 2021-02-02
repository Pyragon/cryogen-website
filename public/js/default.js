var n;

function isNullOrWhitespace(str) {
    function check(line) {
        return line === null || line.match(/^ *$/) !== null;
    }
    if (!Array.isArray(str))
        return check(str);
    for (let s of str)
        if (check(s)) return true;
    return false;
}