import React, { useState, useEffect, useContext } from 'react';
import axios from '../../../utils/axios';

import Widget from '../../utils/Widget';
import DisplayUser from '../../utils/user/DisplayUser';

import NotificationContext from '../../../utils/contexts/NotificationContext';

export default function UsersViewingThread({ thread }) {
    let [ activities, setActivities ] = useState([]);

    let { sendErrorNotification } = useContext(NotificationContext);

    console.log(thread._id);

    useEffect(() => {

        let load = async () => {

            try {

                let res = await axios.get(`/forums/threads/${thread._id}/users`);

                setActivities(res.data.activities);

            } catch(error) {
                sendErrorNotification(error);
            }
        };

        load();

        let interval = setInterval(load, 5000);

        return () => clearInterval(interval);
    }, [ thread ]);
    return (
        <Widget title="Users viewing thread" description="Real-Time stats about users viewing this thread">
            { activities.length === 0 && <p className='t-center'>No users are currently viewing this thread.</p> }
            { activities.length > 0 && 
                <div className="users-viewing-container">
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
