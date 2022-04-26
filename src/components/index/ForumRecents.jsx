import axios from '../../utils/axios';
import React, { useState, useEffect, useContext } from 'react'

import CollapsibleWidget from '../utils/CollapsibleWidget';
import DisplayUser from '../utils/user/DisplayUser';
import NotificationContext from '../../utils/contexts/NotificationContext';

export default function ForumRecents() {
    let [ threads, setThreads ] = useState([]);

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let loadThreads = async() => {

            try {

                let res = await axios.get(`/forums/threads/news`);

                setThreads(res.data.threads);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        loadThreads();
    }, []);
    return (
        <>
            <h4 className="title">Recent News:</h4>
            { threads.map((thread, index) => ( 
                <CollapsibleWidget 
                    key={thread._id} 
                    title={thread.title} 
                    link={`/forums/threads/${thread._id}`}
                    description={
                        <DisplayUser 
                            user={thread.firstPost.author} 
                            prefix='Posted Today by '
                        />
                    }  
                    index={index}
                    collapsed={index !== 0 && index !== 1}
                >
                    {thread.firstPost.content}
                </CollapsibleWidget> ) )
            }
        </>
    )
}
