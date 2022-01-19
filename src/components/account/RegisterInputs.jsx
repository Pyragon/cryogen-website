import React, { useContext, useState } from 'react';
import LabelInput from '../utils/LabelInput';

import RegisterInputContext from '../../utils/contexts/RegisterInputContext';
import { useEffect } from 'react';

export default function RegisterInputs() {
    let { registerInputs, setRegisterInputs } = useContext(RegisterInputContext);
    let [ username, setUsername ] = useState(registerInputs.username || '');
    let [ password, setPassword ] = useState(registerInputs.password || '');
    let [ passwordCheck, setPasswordCheck ] = useState(registerInputs.passwordCheck || '');
    function updateInputs() {
        setRegisterInputs({ ...registerInputs, username, password, passwordCheck });
    }
    useEffect(() => {
        updateInputs();
    }, [ username, password, passwordCheck ]);
    return (
        <div className='register-inputs'>
            <LabelInput
                title='Username'
                placeholder='Enter desired username'
                value={username}
                setState={setUsername}
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
