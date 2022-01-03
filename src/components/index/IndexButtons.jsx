import React from 'react'

import Button from '../utils/Button';

import '../../styles/index/IndexButtons.css'

export default function IndexButtons() {
    let loggedIn = true;
    return (
        <div className="index-btns">
            { loggedIn && <Button className="index-btn" title="Create Account" />}
            <Button className="index-btn" title="Download Cryogen" />
        </div>
    )
}
