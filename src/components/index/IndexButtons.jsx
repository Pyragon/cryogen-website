import React from 'react'

import '../../styles/index/IndexButtons.css'

export default function IndexButtons() {
    let loggedIn = true;
    return (
        <div className="index-btns">
            { loggedIn && <button className="btn index-btn">Create Account</button>}
            <button className="btn index-btn">Download Cryogen</button>
        </div>
    )
}
