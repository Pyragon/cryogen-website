import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import LoginWidget from './LoginWidget'
import UserContext from '../../../utils/UserContext';
import Widget from '../../utils/Widget';

import './../../../styles/index/AccountWidget.css'

export default function AccountWidget() {
    let { user } = useContext(UserContext);
    let loggedIn = user !== null;
    if(!loggedIn)
        return <LoginWidget/>
    return (
        <div>
            <h4 className="title t-center">Account & Community</h4>
            <Widget title="Account">
                <div className="account-node-btns">
                    <Link to="/account" className="btn account-node-btn">Overview</Link>
                    <Link to="/highscores" className="btn account-node-btn">My Highscores</Link>
                    <Link to="/account/vote" className="btn account-node-btn">Vote</Link>
                    <Link to="/account/shop" className="btn account-node-btn">Shop</Link>
                </div>
            </Widget>
        </div>
    )
}
