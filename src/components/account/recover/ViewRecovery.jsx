import React, { useContext, useEffect, useState } from 'react';
import axios from '../../../utils/axios';

import Input from '../../utils/Input';
import Button from '../../utils/Button';

import NotificationContext from '../../../utils/contexts/NotificationContext';
import SectionContext from '../../../utils/contexts/SectionContext';

export default function ViewRecovery() {

    let { viewKey, setViewKey, created } = useContext(SectionContext);
    let { sendErrorNotification } = useContext(NotificationContext);

    let [ viewKeyText, setViewKeyText ] = useState('');

    let [ status, setStatus ] = useState('');
    let [ comments, setComments ] = useState([]);
    let [ recoveredPassword, setRecoveredPassword ] = useState('');

    let submitViewKey = () => setViewKey(viewKeyText);

    useEffect(() => {

        let load = async() => {

            if(!viewKey) return;

            try {

                let res = await axios.get(`/account/recovery/view/${viewKey}`);

                setStatus(res.data.status);
                setComments(res.data.comments);
                if(status === 'APPROVED')
                    setRecoveredPassword(res.data.password);

            } catch(error) {
                sendErrorNotification(error);
                setViewKey(null);
                setViewKeyText('');
            }

        };

        load();

    }, [ viewKey ]);

    if(!status) return <EnterRecoveryKey viewKeyText={viewKeyText} setViewKeyText={setViewKeyText} submitViewKey={submitViewKey} />;

    let colour = status === 'PENDING' ? 'yellow' : status === 'APPROVED' ? 'green' : 'red';

    return (
        <>
            <div className='view-recovery-container'>
                <h1>View Recovery Status</h1>
                { created && 
                    <div className='view-recovery-key'>
                        <p>Your recovery status has been created.</p>
                        <p>You can view this recovery later on by entering the code shown below.</p>
                        <p className='red'>Write this key down! You will need it to check the status again later!</p>
                        <p className='yellow'>{viewKey}</p>
                    </div>
                }
                <h1>
                    {'Recovery Status: '}
                    <span className={colour}>{status}</span>
                </h1>
                { status === 'APPROVED' &&
                    <div className='view-recovery-password'>
                        <p>Your recovery has been approved!</p>
                        <p>You can use the password below to login to your account.</p>
                        <p>Please note, you will be required to change your password before you can do anything.</p>
                        <h1>
                            New Password:
                            <span className='yellow'>{recoveredPassword}</span>
                        </h1>
                    </div>
                }
                { status !== 'PENDING' && 
                    <div className='view-recovery-comments'>
                    </div>
                }
                { comments.map(comment => 'temp')}
            </div>
        </>
    )
}

function EnterRecoveryKey({ viewKeyText, setViewKeyText, submitViewKey }) {
    return (
        <>
            <div className='enter-recovery-key'>
                <h1>View Recovery Status</h1>
                <p>Enter your recovery key to view your recovery status.</p>
                <div className='recovery-key-input-container'>
                    <Input value={viewKeyText} setState={setViewKeyText} className='recovery-key-input' type='text' placeholder='Recovery Key' />
                    <Button onClick={submitViewKey}>View</Button>
                </div>
            </div>
        </>
    )
}
