import React, { useEffect, useState, useContext } from 'react'
import axios from '../../../../utils/axios';
import useDyanmicRefs from '../../../../utils/hooks/use-dynamic-refs';

import NotificationContext from '../../../../utils/contexts/NotificationContext';

import Input from '../../../utils/Input';
import Button from '../../../utils/Button';

const CreateUser = React.forwardRef(({ create, isCreate }, ref) => {
    console.log(ref.current);
    let usergroupsValue = ref.current.usergroups ? ref.current.usergroups.map(group => group._id) : [ '' ];
    if(usergroupsValue.length === 0) usergroupsValue = [ '' ];
    let [ username, setUsername ] = useState(ref.current.username || '');
    let [ displayName, setDisplayName ] = useState(ref.current.displayName || '');
    let [ email, setEmail ] = useState(ref.current.email || '');
    let [ discord, setDiscord ] = useState(ref.current.discord || '');
    let [ displayGroup, setDisplayGroup ] = useState(ref.current.displayGroup ? ref.current.displayGroup._id : '');
    let [ usergroups, setUsergroups ] = useState(usergroupsValue);
    let [ tfaEnabled, setTfaEnabled ] = useState(ref.current.tfaEnabled || false);
    let [ newPassword, setNewPassword ] = useState('');

    let [ groups, setGroups ] = useState([]);
    let [ defaultGroup, setDefaultGroup ] = useState('');

    let [ getRef ] = useDyanmicRefs([ 'username', 'displayName', 'email', 'discord', 'displayGroup', 'usergroups_0', 'tfaEnabled', 'newPassword' ]);

    let { sendErrorNotification, sendConfirmation, sendNotification, closeModal } = useContext(NotificationContext);

    let revokeLogins = () => {

        let onSuccess = async() => {

            try {

                await axios.post(`/users/revoke/${ref.current._id}`);

                sendNotification({ text: 'Users logins revoked.' });

                closeModal();

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        sendConfirmation({ text: 'Are you sure you wish to revoke their logins?', onSuccess });
    };
    
    useEffect(() => ref.current = {
            ...ref.current,
            username,
            displayName,
            email,
            discord,
            displayGroup,
            usergroups,
            tfaEnabled,
            password: newPassword,
    }, [ username, displayName, email, discord, displayGroup, usergroups, tfaEnabled, newPassword ]);

    useEffect(() => {

        let loadGroups = async () => {

            try {

                let res = await axios.get('/forums/usergroups');

                setGroups(res.data.usergroups.reverse());
                setDefaultGroup(res.data.defaultGroup);

            } catch(error) {
                sendErrorNotification(error);
            }
        };

        loadGroups();
    }, []);

    return (
        <div className='create-modal-container'>
            <h1>Create User</h1>
            <div className='create-modal-values'>
                <p>Username</p>
                { !isCreate && <p>{ref.current.username}</p>}
                { isCreate &&
                    <Input
                        className='m-auto'
                        ref={getRef('username')}
                        placeholder='Username'
                        value={username}
                        setState={setUsername}
                        next={getRef('displayName')}
                    />
                }
                <p>Display Name</p>
                <Input
                    ref={getRef('displayName')}
                    className='m-auto'
                    placeholder='Display Name'
                    value={displayName}
                    setState={setDisplayName}
                    next={getRef('email')}
                />
                <p>Email</p>
                <Input
                    ref={getRef('email')}
                    className='m-auto'
                    placeholder='Email'
                    value={email}
                    setState={setEmail}
                    next={getRef('discord')}
                />
                <p>Discord</p>
                <Input
                    ref={getRef('discord')}
                    className='m-auto'
                    placeholder='Discord'
                    value={discord}
                    setState={setDiscord}
                    next={getRef('displayGroup')}
                />
                <p>Display Group</p>
                <select
                    ref={getRef('displayGroup')}
                    className='m-auto input'
                    style={{ height: '2rem'}}
                    value={displayGroup || defaultGroup}
                    onChange={e => setDisplayGroup(e.target.value)}
                >
                    { groups.map(group => (
                        <option 
                            style={{
                                color: '#999',
                                background: '#111',
                            }}
                            key={group._id} 
                            value={group._id}
                        >
                            {group.name}
                        </option>
                    ))}
                </select>
                <p>
                    Usergroups
                    <span className='m-left-5 fa fa-plus-circle link' onClick={() => setUsergroups([ ...usergroups, '' ])}/>
                </p>
                <div className='create-user-usergroups'>
                    { usergroups.map((usergroup, i) => {
                        return (
                            <>
                                <select
                                    key={i}
                                    className='m-auto input m-top-not-first'
                                    style={{ height: '2rem'}}
                                    value={usergroup !== '' ? usergroup : defaultGroup}
                                    onChange={e => {
                                        let newGroups = [ ...usergroups ];
                                        newGroups[i] = e.target.value;
                                        setUsergroups(newGroups);
                                    }}
                                >
                                    <option style={{ color: '#999', background: '#111' }} value='none'>None</option>
                                    { groups.map(group => (
                                        <option 
                                            style={{
                                                color: '#999',
                                                background: '#111',
                                            }}
                                            key={group._id} 
                                            value={group._id}
                                        >
                                            {group.name}
                                        </option>
                                    ))}
                                </select>
                            </>
                        );
                    }) }
                </div>
                <p>TFA Enabled</p>
                <Input
                    ref={getRef('tfaEnabled')}
                    type='boolean'
                    className='m-auto'
                    placeholder='TFA Enabled'
                    value={tfaEnabled}
                    setState={setTfaEnabled}
                    next={getRef('newPassword')}
                />
                <p>New Password</p>
                <Input
                    ref={getRef('newPassword')}
                    type='password'
                    className='m-auto'
                    placeholder='New Password'
                    value={newPassword}
                    setState={setNewPassword}
                    onEnter={create}
                />
                { !isCreate && <p>Revoke Logins</p>}
                { !isCreate && 
                    <Button 
                        className='m-auto'
                        onClick={revokeLogins}
                        title='Revoke'
                    />
                }
            </div>
        </div>
    );

});

export default CreateUser;
