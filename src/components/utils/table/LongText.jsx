import React, { useState } from 'react';

export default function LongText({ text }) {
    let [ open, setOpen ] = useState(false);
    let [ openedFromHover, setOpenedFromHover ] = useState(false);

    let onMouseEnter = (e) => {
        if(open) return false;
        setOpenedFromHover(true);
        setOpen(true);

    };

    let onMouseOut = (e) => {
        if(!openedFromHover) return false;
        setOpen(false);
    };

    return (
        <td>
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
                onMouseLeave={onMouseOut}
            >
                { text.substring(0, 20) + '...'}
            </span>
            <div
                className='long-text-container'
                style={{ display: open ? 'block' : 'none' }}
            >
                { <p className='long-text'>{text}</p> }
            </div>
        </td>
    )
}
