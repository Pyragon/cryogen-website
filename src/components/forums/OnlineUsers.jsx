import React, { useEffect, useState, useContext } from 'react';
import axios from '../../utils/axios';
import NotificationContext from '../../utils/contexts/NotificationContext';

import CollapsibleWidget from '../utils/CollapsibleWidget';
import DisplayUser from '../utils/user/DisplayUser';

export default function OnlineUsers() {
    let [ users, setUsers ] = useState([]);

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let fetch = async () => {

            try {

                let res = await axios.get('/forums/stats/online');
    
                setUsers(res.data.users);

            } catch(error) {
                sendErrorNotification(error);
            }
        };

        fetch();

        let interval = setInterval(fetch, 5000);

        return () => clearInterval(interval);
    }, []);

    return (
        <CollapsibleWidget
            className="grid-span-3"
            title="Online users"
            description={"There "+(users.length === 1 ? "is" : "are")+" currently "+(users.length === 0 ? "no" : users.length)+" user"+(users.length === 1 ? "" : "s")+" online."}
            collapsed={true}
        >
            { users.map((activity, index) =>
                <DisplayUser 
                    key={index} 
                    user={activity.user} 
                    suffix={index === users.length - 1 ? '' : ', '}
                    title={activity.activity}
                />
            )}
        </CollapsibleWidget>
    )
}
