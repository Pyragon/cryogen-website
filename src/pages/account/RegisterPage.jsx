import React, { useState, useMemo, useContext, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../utils/axios';
import constants from '../../utils/constants';
import { validate, validateUsername, validatePassword, validateEmail } from '../../utils/validate';

import RegisterInfo from '../../components/account/RegisterInfo';
import RegisterInputs from '../../components/account/RegisterInputs';
import Widget from '../../components/utils/Widget';
import Button from '../../components/utils/Button';

import ReCAPTCHA from 'react-google-recaptcha';

import UserContext from '../../utils/contexts/UserContext';
import RegisterInputContext from '../../utils/contexts/RegisterInputContext';

import '../../styles/account/Register.css';
import NotificationContext from '../../utils/contexts/NotificationContext';

export default function RegisterPage() {
    let [ registerInputs, setRegisterInputs ] = useState({});
    let { setUser } = useContext(UserContext);
    let navigate = useNavigate();

    let recaptchaRef = useRef();

    let providerValue = useMemo(() => ({ registerInputs, setRegisterInputs }), [ registerInputs, setRegisterInputs ]);

    let { sendNotification, sendErrorNotification } = useContext(NotificationContext);

    let register = async() => {
        let validateOptions = {
            username: validateUsername,
            password: validatePassword,
            email: validateEmail,
        };

        let username = registerInputs.username.toLowerCase().replaceAll(' ', '_');
        let password = registerInputs.password;
        let email = registerInputs.email;
    
        let error = validate(validateOptions, { email, username, password });
        if(error) {
            sendErrorNotification(error);
            return;
        }
    
        if(password !== registerInputs.passwordCheck) {
            sendErrorNotification('Passwords do not match.');
            return;
        }

        try {

            let recaptchaToken = await recaptchaRef.current.executeAsync();
    
            let res = await axios.post('/users', { username, password, email, token: recaptchaToken });
    
            let sessionId = res.data.sessionId;
            sessionStorage.setItem('sessionId', sessionId);
            setUser(res.data.user);
            navigate('/');
            sendNotification({ text: 'Registration successful!' });
    
        } catch(err) {
            sendErrorNotification(err);
        }
    }

    return (
        <Widget
            className='register-widget'
            title='Register Account'
            description='Registering an account is required to play the game. If you have already made an in-game account, login with that.'
        >
            <div className='register-container'>
                <RegisterInputContext.Provider value={providerValue}>
                    <RegisterInputs />
                    <RegisterInfo />
                    <ReCAPTCHA
                        ref={recaptchaRef}
                        size='invisible'
                        sitekey={constants['RECAPTCHA_SITE_KEY']}
                    />
                </RegisterInputContext.Provider>
            </div>
            <div className='register-btn-container'>
                <Button 
                    title='Create Account' 
                    className='register-btn' 
                    onClick={register} 
                />
            </div>
            <div style={{clear: 'both' }} />
        </Widget>
    )
}
