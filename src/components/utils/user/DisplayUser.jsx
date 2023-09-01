import React from 'react';

import { Link } from 'react-router-dom';
import { escapeHtml } from '../../../utils/format';

export default function DisplayUser({ user, prefix='', suffix=' ', title=undefined, userTitleStyle={}, useUserTitle=false, avatar, width, height, fontSize, useATag=false, noLink=false }) {
    let style = {};
    let imgStyle = {};
    if(width)
        imgStyle.width = width;
    if(height)
        imgStyle.height = height;
    if(fontSize)
        style.fontSize = fontSize;
    if(user.displayGroup.colour)
        style.color = user.displayGroup.colour;
    title = title || user.display.name;
    return (
        <>
            {prefix}
            { noLink && 
                <WithCrowns 
                    name={user.display.name}
                    group={user.displayGroup} 
                    style={style} 
                    imgStyle={imgStyle}
                /> 
            }
            { !useATag && !noLink && 
                <Link to={"/forums/users/"+user._id} className="link" title={title}>
                    <WithCrowns
                        name={user.display.name}
                        group={user.displayGroup}
                        style={style}
                        imgStyle={imgStyle}
                    />
                </Link> 
            }
            { useATag && !noLink && 
                <a href={"/forums/users/"+user._id} className="link" title={title}>
                    <WithCrowns
                        name={user.display.name}
                        group={user.displayGroup}
                        style={style}
                        imgStyle={imgStyle}
                    />
                </a>
            }
            { suffix }
            { useUserTitle && user.displayGroup && user.displayGroup.title && <p style={userTitleStyle} className="small">{user.displayGroup.title}</p> }
            { avatar && <img className="user-avatar" src={avatar} alt='Avatar' /> }
        </>
    )
}

function WithCrowns({style, imgStyle, group, name}) {
    let html = group.htmlBefore || '';
    html += escapeHtml(name);
    html += group.htmlAfter || '';
    return (
        <span style={style} dangerouslySetInnerHTML={{ __html: html }}/>
    );
}
