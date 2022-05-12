import React from 'react';

export default function DisplayUsergroup({ prefix='', suffix='', group, width, height, fontSize }) {
    let style = {};
    let imgStyle = {};
    if(width)
        imgStyle.width = width;
    if(height)
        imgStyle.height = height;
    if(fontSize)
        style.fontSize = fontSize;
    if(group.colour)
        style.color = group.colour;
    return (
        <>
            {prefix}
            <WithCrowns
                name={group.name}
                group={group}
                style={style} 
                imgStyle={imgStyle}
            />
            { suffix }
        </>
    )
}

function WithCrowns({ style, imgStyle, group, name }) {
    return (
        <span style={style}>
            {group.imageBefore && <img style={imgStyle} src={group.imageBefore} alt='Prefix' />}
            {name}
            {group.imageAfter && <img style={imgStyle} src={group.imageAfter} alt='Suffix' />}
        </span>
    );
}
