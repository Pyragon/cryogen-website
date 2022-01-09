import { React, useContext } from 'react'

import UserContext from '../../utils/UserContext';

import { Link } from 'react-router-dom';

export default function FooterButton({ button, index }) {
    let { user } = useContext(UserContext);
    let loggedIn = user !== null;
    let staff = user.displayGroup.rights > 0;
    if(button.requiresLogin !== undefined && button.requiresLogin !== loggedIn)
        return (<></>)
    if(button.requiresStaff && !staff)
        return (<></>)
    return (
        <li>
            { button.isATag ? (
                <a href={button.link}>{button.title}</a>
            ) : (
                <Link to={button.link}>{button.title}</Link>
            )}
        </li>
    )
}
