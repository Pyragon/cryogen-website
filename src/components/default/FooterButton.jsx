import { React, useContext } from 'react';

import axios from '../../utils/axios';

import UserContext from '../../utils/UserContext';

import { Link } from 'react-router-dom';

export default function FooterButton({ button, index }) {
    let { user, setUser } = useContext(UserContext);
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
            axios.post('/users/logout').then(() => {
                setUser(null);
                localStorage.removeItem('sessionId');
                sessionStorage.removeItem('sessionId');
                console.log('Logged out');
            }).catch(console.error);
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
