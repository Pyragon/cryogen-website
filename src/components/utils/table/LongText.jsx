import React, { useState } from 'react';

export default function LongText({ text, style, className }) {
    let [ open, setOpen ] = useState(false);
    let [ openedFromHover, setOpenedFromHover ] = useState(false);

    if(!style)
        style= {}

    style.display = open ? 'block' : 'none';

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
                className={'long-text-container '+className}
                style={style}
            >
                { <p className='long-text'>{text}</p> }
            </div>
        </td>
    )
}
