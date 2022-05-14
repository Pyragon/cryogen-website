import React, { createRef } from 'react';

const map = new Map();

function setRef(key) {
    if (!key) return console.error('useDyanmicRefs: key is required');
    const ref = createRef();
    map.set(key, ref);
    console.log('Creating ref for: ' + key);
    return ref;
}

function getRef(key) {
    if (!key) return console.error('useDyanmicRefs: key is required');
    return map.get(key);
}

function useDyanmicRefs(defaultRefs) {
    if (defaultRefs) {
        for (let ref of defaultRefs)
            setRef(ref);
    }
    return [getRef, setRef];
}

export default useDyanmicRefs;