import React, { useMemo, useState } from 'react';

export default function TileImage({ tile: data }) {
    let [ open, setOpen ] = useState(false);
    let [ openedFromHover, setOpenedFromHover ] = useState(false);
    
    let tile = useMemo(() => {
        let coords = data.split(',');
        return {
            x: parseInt(coords[0]),
            y: parseInt(coords[1]),
            plane: coords.length > 2 ? parseInt(coords[2]) : 0
        }
    }, [ data ]);

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
                {data}
            </span>
            <div
                className='tile-image-container' 
                style={{ display: open ? 'block' : 'none' }}
            >
                { open && <img className='tile-image' alt='In-game map with red X to pinpoint tile' src={`http://localhost:9000/?x=${tile.x}&y=${tile.y}&plane=${tile.plane}`} /> }
            </div>
        </td>
    )
}
