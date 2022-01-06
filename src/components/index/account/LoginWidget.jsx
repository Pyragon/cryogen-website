import React, { useState } from 'react'

import axios from 'axios';

import LabelInput from '../../utils/LabelInput';
import Checkbox from '../../utils/Checkbox';
import SpanIcon from '../../utils/SpanIcon';
import Button from '../../utils/Button';
import Widget from '../../utils/Widget';

import './../../../styles/Buttons.css'

export default function LoginWidget() {
    let [ tfaToggled, setTfaToggled ] = useState(false);
    let [ username, setUsername ] = useState("");
    let [ password, setPassword ] = useState("");
    let [ rememberMe, setRememberMe ] = useState(false);
    let [ otp, setOtp ] = useState("");

    let submitAuth = async() => {
        try {
            let response = await axios.post('http://localhost:8081/users/auth', {
                username,
                password,
                rememberMe,
                otp
            });
        } catch(err) {
            console.error(err);
        }
    };

    let forgotPassword = () => {

    };
    return (
        <div>
            <h4 className="title t-center">Account & Community</h4>
            <Widget title="Login">
                <LabelInput title="Username" placeholder="Enter username" value={username} setState={setUsername}/>
                <LabelInput title="Password" placeholder="Enter password" type="password" value={password} setState={setPassword} />
                <SpanIcon className="toggle-tfa" onClick={() => setTfaToggled(!tfaToggled)} icon={"fa-"+(tfaToggled ? "minus": "plus")+"-square"} content={'Two-factor Authentication Options'}/>
                { tfaToggled &&
                    <LabelInput title="One-time password" placeholder="Enter OTP" type="text" value={otp} setState={setOtp}/>
                }
                <Checkbox title="Keep me logged in" className="remember-me-block" value={rememberMe} setState={setRememberMe}/>
                <div className="login-buttons">
                    <Button title="Login" onClick={submitAuth}/>
                    <Button title="Forgot Password" onClick={forgotPassword}/>
                </div>
            </Widget>
        </div>
    )
}
