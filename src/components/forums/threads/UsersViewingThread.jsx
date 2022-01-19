import React, { useState, useEffect } from 'react';
import axios from '../../../utils/axios';

import Widget from '../../utils/Widget';
import DisplayUser from '../../utils/user/DisplayUser';

export default function UsersViewingThread({ thread }) {
    let [ users, setUsers ] = useState([]);
    useEffect(() => {
        let interval = setInterval(() => {
            axios.get(`/forums/threads/${thread.id}/users`)
                .then(res => setUsers(res.data))
                .catch(console.error);
        }, 5000);
        return () => clearInterval(interval);
    }, [ thread ]);
    return (
        <Widget title="Users viewing thread" description="Real-Time stats about users viewing this thread">
            { users.length === 0 && <p className='t-center'>No users are currently viewing this thread.</p> }
            { users.map((user, index) =>
                <DisplayUser
                    key={index}
                    user={user}
                    suffix={index === users.length - 1 ? '' : ', '}
                />
            )}
        </Widget>
    )
}
