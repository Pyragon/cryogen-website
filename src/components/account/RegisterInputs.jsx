import React, { useContext, useState } from 'react';
import LabelInput from '../utils/LabelInput';

import RegisterInputContext from '../../utils/contexts/RegisterInputContext';
import { useEffect } from 'react';

export default function RegisterInputs() {
    let { registerInputs, setRegisterInputs } = useContext(RegisterInputContext);
    let [ username, setUsername ] = useState(registerInputs.username || '');
    let [ password, setPassword ] = useState(registerInputs.password || '');
    let [ passwordCheck, setPasswordCheck ] = useState(registerInputs.passwordCheck || '');
    let [ email, setEmail ] = useState(registerInputs.email || '');
    function updateInputs() {
        setRegisterInputs({ ...registerInputs, username, password, passwordCheck, email });
    }
    useEffect(() => {
        updateInputs();
    }, [ username, password, passwordCheck, email ]);
    return (
        <div className='register-inputs'>
            <LabelInput
                title='Username'
                placeholder='Enter desired username'
                value={username}
                setState={setUsername}
            />
            <LabelInput
                title='Email'
                placeholder='Enter email'
                value={email}
                setState={setEmail}
            />
            <LabelInput
                title='Password'
                placeholder='Enter desired password'
                value={password}
                setState={setPassword}
                type='password'
            />
            <LabelInput
                title='Retype Password'
                placeholder='Enter desired password'
                value={passwordCheck}
                setState={setPasswordCheck}
                type='password'
            />
        </div>
    )
}
