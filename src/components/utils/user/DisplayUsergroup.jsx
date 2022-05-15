import React from 'react';

export default function DisplayUsergroup({ prefix='', suffix='', group, width, height, fontSize }) {
    let style = {};
    let imgStyle = {};
    return (
        <>
            {prefix}
            <WithCrowns
                name={group.name}
                group={group}
                style={style} 
                imgStyle={imgStyle}
                width={width}
                height={height}
                fontSize={fontSize}
            />
            { suffix }
        </>
    )
}

function WithCrowns({ style, imgStyle, group, name, width, height, fontSize }) {
    style = style || {};
    imgStyle = imgStyle || {};
    if(width)
        imgStyle.width = width;
    if(height)
        imgStyle.height = height;
    if(fontSize)
        style.fontSize = fontSize;
    if(group.colour)
        style.color = group.colour;
    return (
        <span style={style}>
            {group.imageBefore && <img style={imgStyle} src={group.imageBefore} alt='Prefix' />}
            {name}
            {group.imageAfter && <img style={imgStyle} src={group.imageAfter} alt='Suffix' />}
        </span>
    );
}

export { DisplayUsergroup, WithCrowns };
