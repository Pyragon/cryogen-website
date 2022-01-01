import React from 'react'

import './../../../styles/Buttons.css'

export default function LoginWidget() {
    return (
        <div>
            <h4 className="title t-center">Account & Community</h4>
            <div className="mini-login">
                <div className="widget">
                    <div className="header">
                        <h4>Account Login</h4>
                    </div>
                    <div className="content">
                        <p>Username:</p>
                        <input className="username login-input input" type="text" placeholder="Username" readOnly/>
                        <p>Password:</p>
                        <input className="password login-input input" type="password" placeholder="Password" readOnly/>
                        <div id="toggle-tfa">
                            <span>
                                <i className="fa fa-plus-square"></i>
                            </span>
                            <span id="tfa-op">
                                Two-factor Authentication Options
                            </span>
                        </div>
                        <div id="tfa-block">
                            <div>
                                <span className="color-white">OTP: </span>
                                <input className="input" type="text" placeholder="Enter OTP" title="Enter One-Time Password" />
                            </div>
                        </div>
                        <div id="remember-me">
                            <input type="checkbox"/>
                            <span>Remember me</span>
                        </div>
                        <div id="login-buttons">
                            <button className="btn btn-default small-btn" id="login-btn">Login</button>
                            <button className="btn btn-default small-btn" id="forgot-btn">Forgot Password</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
