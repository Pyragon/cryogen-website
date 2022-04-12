import React from 'react';

import { Link } from 'react-router-dom';

export default function DisplayUser({ user, prefix='', suffix=' ', title=undefined, useUserTitle=false, avatar, width, height, fontSize }) {
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
            <Link to={"/users/"+user._id} className="link" title={title}>
                <span style={style}>
                    { user.displayGroup.imageBefore && <img style={imgStyle} src={user.displayGroup.imageBefore} alt='Prefix' /> }
                    {user.displayName}
                    { user.displayGroup.imageAfter && <img style={imgStyle} src={user.displayGroup.imageAfter} alt='Suffix'/> }
                </span>
            </Link>
            { suffix }
            { useUserTitle && user.displayGroup && user.displayGroup.title && <p className="small">{user.displayGroup.title}</p> }
            { avatar && <img className="user-avatar" src={avatar} alt='Avatar' /> }
        </>
    )
}
