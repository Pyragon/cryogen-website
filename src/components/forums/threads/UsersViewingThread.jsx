import React, { useState, useEffect } from 'react';
import axios from '../../../utils/axios';

import Widget from '../../utils/Widget';
import DisplayUser from '../../utils/user/DisplayUser';

export default function UsersViewingThread({ thread }) {
    let [ activities, setActivities ] = useState([]);
    useEffect(() => {
        let interval = setInterval(() => {
            axios.get(`/forums/threads/${thread.id}/users`)
                .then(res => setActivities(res.data))
                .catch(console.error);
        }, 5000);
        return () => clearInterval(interval);
    }, [ thread ]);
    return (
        <Widget title="Users viewing thread" description="Real-Time stats about users viewing this thread">
            { activities.length === 0 && <p className='t-center'>No users are currently viewing this thread.</p> }
            { activities.length > 0 && 
                <div class="users-viewing-container">
                    { activities.map((activity, index) =>
                        <DisplayUser
                            key={index}
                            user={activity.user}
                            suffix={index === activities.length - 1 ? '' : ', '}
                        />
                    )}
                </div>
            }
        </Widget>
    )
}
