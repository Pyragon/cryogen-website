import React, { useState, useMemo, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../utils/axios';
import RegisterInfo from '../../components/account/RegisterInfo';
import RegisterInputs from '../../components/account/RegisterInputs';
import Widget from '../../components/utils/Widget';
import Button from '../../components/utils/Button';

import UserContext from '../../utils/contexts/UserContext';
import RegisterInputContext from '../../utils/contexts/RegisterInputContext';

import '../../styles/account/Register.css';

async function register({ username, password, passwordCheck }, navigate, setUser) {
    if(!username) {
        console.error('Username is required');
        return;
    }
    if(!password || !passwordCheck || password !== passwordCheck) {
        console.error('Passwords must be equal and filled out.');
        return;
    }
    if(username.length < 3 || username.length > 12) {
        console.error('Username must be between 3 and 12 characters.');
        return;
    }
    if(password.length < 8 || password.length > 50) {
        console.error('Password must be between 8 and 50 characters.');
        return;
    }
    try {
        axios.post('/users', { 
            username,
            password
        })
        .then(res => {
            if(!res.data.success) {
                console.error('Error creating user.');
                return;
            }
            let sessionId = res.data.sessionId;
            sessionStorage.setItem('sessionId', sessionId);
            setUser(res.data.user);
            navigate('/');
        })
        .catch(console.error);
    } catch(err) {
        console.error(err);
    }
}

export default function RegisterPage() {
    let [ registerInputs, setRegisterInputs ] = useState({});
    let { setUser } = useContext(UserContext);
    let navigate = useNavigate();

    let providerValue = useMemo(() => ({ registerInputs, setRegisterInputs }), [ registerInputs, setRegisterInputs ]);
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
                    onClick={() => register(registerInputs, navigate, setUser)} 
                />
            </div>
            <div style={{clear: 'both' }} />
        </Widget>
    )
}
