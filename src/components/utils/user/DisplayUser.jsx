import React from 'react';

import { Link } from 'react-router-dom';

export default function DisplayUser({ user, prefix='', suffix=' ', title=undefined, useUserTitle=false, avatar, width, height, fontSize, useATag=false }) {
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
    title = title || user.displayName;
    return (
        <>
            {prefix}
            { !useATag && 
                <Link to={"/users/"+user._id} className="link" title={title}>
                    <UserWithCrowns
                        user={user}
                        style={style}
                        imgStyle={imgStyle}
                    />
                </Link> 
            }
            { useATag && 
                <a href={"/users/"+user._id} className="link" title={title}>
                    <UserWithCrowns
                        user={user}
                        style={style}
                        imgStyle={imgStyle}
                    />
                </a>
            }
            { suffix }
            { useUserTitle && user.displayGroup && user.displayGroup.title && <p className="small">{user.displayGroup.title}</p> }
            { avatar && <img className="user-avatar" src={avatar} alt='Avatar' /> }
        </>
    )
}

function UserWithCrowns({style, imgStyle, user}) {
    return (
        <span style={style}>
            { user.displayGroup.imageBefore && <img style={imgStyle} src={user.displayGroup.imageBefore} alt='Prefix' /> }
            {user.displayName}
            { user.displayGroup.imageAfter && <img style={imgStyle} src={user.displayGroup.imageAfter} alt='Suffix'/> }
        </span>
    );
}
