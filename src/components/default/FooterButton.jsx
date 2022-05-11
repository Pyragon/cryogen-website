import React, { useContext } from 'react';

import UserContext from '../../utils/contexts/UserContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

import { Link } from 'react-router-dom';

export default function FooterButton({ button, index }) {
    let { user, setUser } = useContext(UserContext);
    let staff = user && user.displayGroup.rights > 0;
    let loggedIn = user !== null;
    let { sendNotification, sendErrorNotification } = useContext(NotificationContext);
    if(button.requiresLogin !== undefined && button.requiresLogin !== loggedIn)
        return (<></>)
    if(button.requiresStaff && !staff)
        return (<></>)
    let onClick = null;
    if(button.onClick)
        onClick = async(e) => {
            e.preventDefault();
            await button.onClick(e, { setUser, sendNotification, sendErrorNotification });
        };

    return (
        <li>
            { button.isATag ? (
                <a href={button.link} onClick={onClick}>{button.title}</a>
            ) : (
                <Link to={button.link}>{button.title}</Link>
            )}
        </li>
    )
}
