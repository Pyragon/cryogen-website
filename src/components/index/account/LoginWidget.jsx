import React, { useState, useContext, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

import axios from '../../../utils/axios';
import { validate, validateUsername, validatePassword } from '../../../utils/validate';

import LabelInput from '../../utils/LabelInput';
import Checkbox from '../../utils/Checkbox';
import SpanIcon from '../../utils/SpanIcon';
import Button from '../../utils/Button';
import Widget from '../../utils/Widget';

import NotificationContext from '../../../utils/contexts/NotificationContext';
import UserContext from '../../../utils/contexts/UserContext';

import './../../../styles/Buttons.css';

const validateOtp = {
    required: true,
    name: 'One Time Password',
    min: 6,
    max: 6,
};

export default function LoginWidget( { header=true } ) {
    let { setUser } = useContext(UserContext);
    let [ tfaToggled, setTfaToggled ] = useState(false);
    let [ username, setUsername ] = useState("");
    let [ password, setPassword ] = useState("");
    let [ rememberMe, setRememberMe ] = useState(false);
    let [ otp, setOtp ] = useState("");

    let { sendErrorNotification } = useContext(NotificationContext);

    let passwordRef = useRef();
    let tfaRef = useRef();

    let navigate = useNavigate();

    let submitAuth = async() => {

        let [ validated, error ] = validate({ username: validateUsername, password: validatePassword, otp: validateOtp }, { username, password, otp });
        if(!validated) {
            sendErrorNotification(error);
            return;
        }

        try {
            let res = await axios.post('/users/auth', {
                username,
                password,
                otp,
                remember: rememberMe
            });
            
            let sessionId = res.data.sessionId;
            let storage = rememberMe ? localStorage : sessionStorage;
            storage.setItem('sessionId', sessionId);
            setUser(res.data.user);
        } catch(err) {
            sendErrorNotification(err);
        }
    };

    let forgotPassword = () => {
        navigate('/recover?username='+username);
    };
    return (
        <div>
            { header && <h4 className="title t-center">Account & Community</h4> }
            <Widget title="Login">
                <LabelInput 
                    title="Username" 
                    placeholder="Enter username" 
                    value={username} 
                    setState={setUsername} 
                    next={passwordRef} 
                />
                <LabelInput 
                    ref={passwordRef} 
                    title="Password" 
                    placeholder="Enter password" 
                    type="password" 
                    value={password} 
                    setState={setPassword} 
                    onEnter={submitAuth}
                    next={tfaToggled ? tfaRef : null}
                />
                <SpanIcon 
                    className="toggle-tfa" 
                    onClick={() => setTfaToggled(!tfaToggled)} 
                    icon={"fa-"+(tfaToggled ? "minus": "plus")+"-square"}
                >
                    Two-factor Authentication Options
                </SpanIcon>
                { tfaToggled &&
                    <LabelInput
                        ref={tfaRef}
                        title="One-time password" 
                        placeholder="Enter OTP" 
                        type="text" 
                        value={otp} 
                        setState={setOtp}
                    />
                }
                <Checkbox 
                    title="Keep me logged in" 
                    className="remember-me-block" 
                    value={rememberMe} 
                    setState={setRememberMe}
                />
                <div className="login-buttons">
                    <Button title="Login" onClick={submitAuth}/>
                    <Button title="Forgot Password" onClick={forgotPassword}/>
                </div>
            </Widget>
        </div>
    )
}
