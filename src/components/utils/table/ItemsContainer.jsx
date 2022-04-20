import React, { useState } from 'react'

export default function ItemsContainer({ items, short }) {
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
                onMouseOut={onMouseOut}
            >
                { short && 'Hover/Click for more'}
                { !short && items.length+' Item'+(items.length == 0 || items.length > 1 ? 's' : '')+' (Hover/Click for more)' }
            </span>
            <div
                className='items-container'
                style={{ display: open ? 'block' : 'none' }}
            >
                { open && items.map((item, index) => (
                    <div key={index}>
                        { item.defs.name+' ('+item.id+') x'+item.amount }
                    </div>
                ))}
            </div>
        </td>
    )
}
