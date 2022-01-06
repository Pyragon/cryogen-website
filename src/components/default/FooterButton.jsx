import { React } from 'react'

import { Link } from 'react-router-dom';

export default function FooterButton({ button, index }) {
    let loggedIn = false;
    let staff = false;
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
