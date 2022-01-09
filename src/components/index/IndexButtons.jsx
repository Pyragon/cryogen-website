import React, { useState, useEffect, useContext } from 'react';

import UserContext from '../../utils/UserContext';

import Button from '../utils/Button';

import '../../styles/index/IndexButtons.css'

export default function IndexButtons() {
    let { user } = useContext(UserContext);
    let loggedIn = user !== null;
    return (
        <div className="index-btns">
            { !loggedIn && <Button className="index-btn" title="Create Account" />}
            <Button className="index-btn" title="Download Cryogen" />
        </div>
    )
}
