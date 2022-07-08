import React from 'react';

import { escapeHtml } from '../../../utils/format';

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
    let html = group.htmlBefore || '';
    html += escapeHtml(name);
    html += group.htmlAfter || '';
    return (
        <span style={style} dangerouslySetInnerHTML={{ __html: html }}/>
    );
}

export { DisplayUsergroup, WithCrowns };
