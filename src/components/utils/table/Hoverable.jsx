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
                    if(typeof value === 'object') {
                        if(value.dangerous)
                            value = <span dangerouslySetInnerHTML={{ __html: value.value }}/>;
                    }
                    return (
                        <div key={index} className='hoverable-value'>
                            { value }
                        </div>
                    )
                })}
            </div>
        </>
    )
}
