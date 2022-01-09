import React, { useState, useEffect, useContext } from 'react';
import axios from '../../../utils/axios';
import LoginWidget from './LoginWidget'
import UserContext from '../../../utils/UserContext';

import './../../../styles/index/AccountWidget.css'

export default function AccountWidget() {
    let { user } = useContext(UserContext);
    let loggedIn = user !== null;
    if(!loggedIn)
        return <LoginWidget/>
    return (
        <p>Test</p>
    )
}
