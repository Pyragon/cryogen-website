import React, { useState, useMemo, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../utils/axios';
import { validate, validateUsername, validatePassword } from '../../utils/validate';

import RegisterInfo from '../../components/account/RegisterInfo';
import RegisterInputs from '../../components/account/RegisterInputs';
import Widget from '../../components/utils/Widget';
import Button from '../../components/utils/Button';

import UserContext from '../../utils/contexts/UserContext';
import RegisterInputContext from '../../utils/contexts/RegisterInputContext';

import '../../styles/account/Register.css';
import NotificationContext from '../../utils/contexts/NotificationContext';

export default function RegisterPage() {
    let [ registerInputs, setRegisterInputs ] = useState({});
    let { setUser } = useContext(UserContext);
    let navigate = useNavigate();

    let providerValue = useMemo(() => ({ registerInputs, setRegisterInputs }), [ registerInputs, setRegisterInputs ]);

    let { sendErrorNotification } = useContext(NotificationContext);

    let register = async() => {
        let validateOptions = {
            username: validateUsername,
            password: validatePassword,
        };

        let username = registerInputs.username.toLowerCase().replaceAll(' ', '_');
        let password = registerInputs.password;
    
        let [ validated, error ] = validate(validateOptions, { username, password });
        if(!validated) {
            sendErrorNotification(error);
            return;
        }
    
        if(password !== registerInputs.passwordCheck) {
            sendErrorNotification('Passwords do not match.');
            return;
        }
        try {
    
            let res = await axios.post('/users', { username, password });
    
            let sessionId = res.data.sessionId;
            sessionStorage.setItem('sessionId', sessionId);
            setUser(res.data.user);
            navigate('/');
    
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
