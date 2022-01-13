import React, { useEffect } from 'react';
import axios from '../../utils/axios';

import CollapsibleWidget from '../utils/CollapsibleWidget';
import DisplayUser from '../utils/user/DisplayUser';

export default function OnlineUsers() {
    let [ users, setUsers ] = React.useState([]);
    useEffect(async() => {
        // let interval = setInterval(async() => {
        //     let response = await axios.get('/forums/stats/online');
        //     if(response.data.message) {
        //         console.error(response.data.message);
        //         return;
        //     }
        //     setUsers(response.data);
        // }, 1000);
        // return () => clearInterval(interval);
    }, []);
    return (
        <CollapsibleWidget
            className="grid-span-3"
            title="Online users"
            description={"There "+(users.length == 1 ? "is" : "are")+" currently "+(users.length == 0 ? "no" : users.length)+" user"+(users.length == 1 ? "" : "s")+" online."}
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
