import React from 'react'
import LoginWidget from './LoginWidget'

import './../../../styles/index/AccountWidget.css'

let loggedIn = false;

export default function AccountWidget() {
    if(!loggedIn)
        return <LoginWidget/>
    return (
        <p>Test</p>
    )
}
