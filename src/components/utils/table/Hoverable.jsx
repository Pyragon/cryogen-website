import React, { useState } from 'react'

export default function Hoverable({ shortTitle=false, values }) {
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

    let onClick = () => {
        if(!open) {
            setOpen(true);
            setOpenedFromHover(false);
        } else if(!openedFromHover)
            setOpen(false);
        else
            setOpenedFromHover(false);
    };  

    return (
        <>
            <span
                className='link'
                onClick={onClick}
                onMouseEnter={onMouseEnter}
                onMouseOut={onMouseOut}
            >
                { 'Hover/Click' + (shortTitle ? '' : ' for more') }
            </span>
            <div
                className='hoverable-container'
                style={{ display: open ? 'block' : 'none' }}
            >
                { open && values.map((value, index) => {
                    return (
                        <div key={index} className='hoverable-value'>
                            { typeof value !== 'object' && value }
                            { typeof value === 'object' && 
                                value.dangerous &&
                                <span dangerouslySetInnerHTML={{ __html: value.value }}/>
                            }
                        </div>
                    )
                })}
            </div>
        </>
    )
}
