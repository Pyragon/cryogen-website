import React, { useContext } from 'react';

import axios from '../../utils/axios';

import UserContext from '../../utils/contexts/UserContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

import { Link } from 'react-router-dom';

export default function FooterButton({ button, index }) {
    let { user, setUser } = useContext(UserContext);
    let { sendNotification, sendErrorNotification } = useContext(NotificationContext);
    let loggedIn = user !== null;
    let staff = loggedIn && user.displayGroup.rights > 0;
    if(button.requiresLogin !== undefined && button.requiresLogin !== loggedIn)
        return (<></>)
    if(button.requiresStaff && !staff)
        return (<></>)
    let onClick = null;
    if(button.link === '/logout')
        onClick = async(e) => {
            e.preventDefault();
            let res = await axios.post('/users/logout');
            if(res.data.error) {
                console.error(res.data.error);
                return;
            }
            setUser(null);
            localStorage.removeItem('sessionId');
            sessionStorage.removeItem('sessionId');
        };
    if(button.link === '/default') {
        onClick = async(e) => {
            e.preventDefault();
            sendNotification({ text: 'Test notification ' });
        };
    }
    if(button.apiCall) {
        onClick = async(e) => {
            e.preventDefault();
            let res = await axios.post(button.link);
            if(res.data.error) {
                sendErrorNotification(res.data.error);
                return;
            }
            sendNotification({ text: res.data.message });
        };
    }
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
