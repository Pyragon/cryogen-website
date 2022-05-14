import React, { useMemo, useState } from 'react';

import DisplayUsergroup from '../user/DisplayUsergroup';

const DEFAULT_NAMES = {
    '-1': 'Everyone',
    '-2': 'Logged In Users',
    '-3': 'Only the Author',
    '-4': 'If author is Staff',
};

export default function UserGroupsHover({ allowed, groups }) {
    let [ open, setOpen ] = useState(false);
    let [ openedFromHover, setOpenedFromHover ] = useState(false);
    let [ formatted, setFormatted ] = useState([]);

    let onMouseEnter = (e) => {
        if(open) return false;
        setOpenedFromHover(true);
        setOpen(true);

    };

    let onMouseOut = (e) => {
        if(!openedFromHover) return false;
        setOpen(false);
    };

    useMemo(() => {

        setFormatted(allowed.map(group => {
            if(!groups[group])
                return DEFAULT_NAMES[group];
            return <DisplayUsergroup group={groups[group]} />
        }));

    }, [ allowed ]);
    
    return (
        <>
            <span
                onClick={() => {
                    if(!open) {
                        setOpen(true);
                        setOpenedFromHover(false);
                    } else if(!openedFromHover)
                        setOpen(false);
                    else
                        setOpenedFromHover(false);
                }}
                onMouseEnter={onMouseEnter}
                onMouseOut={onMouseOut}
            >
                Hover/Click
            </span>
            <div
                className='permissions-groups-container'
                style={{ display: open ? 'block' : 'none' }}
            >
                { open && formatted.map((name, index) => {
                    return (
                        <div key={index}>
                            { name }
                        </div>
                    )
                })}
            </div>
        </>
    )
}
