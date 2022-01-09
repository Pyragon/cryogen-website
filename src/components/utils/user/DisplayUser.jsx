import React from 'react'

export default function DisplayUser({ user, prefix='', suffix=' ', useTitle=false, avatar }) {
    let style = {};
    if(user.displayGroup?.colour)
        style.color = user.displayGroup.colour;
    return (
        <>
            {prefix}
            <span style={style}>
                { user.displayGroup?.imageBefore && <img src={user.displayGroup?.imageBefore} alt='Prefix' /> }
                {user.displayName}
                { user.displayGroup?.imageAfter && <img src={user.displayGroup?.imageAfter} alt='Suffix'/> }
            </span>
            { suffix }
            { useTitle && user.displayGroup && user.displayGroup.title && <p className="small">{user.displayGroup.title}</p> }
            { avatar && <img className="user-avatar" src={avatar} alt='Avatar' /> }
        </>
    )
}
