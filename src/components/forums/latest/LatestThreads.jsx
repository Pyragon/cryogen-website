import React, { useEffect, useState, useContext } from 'react'

import axios from '../../../utils/axios';
import NotificationContext from '../../../utils/contexts/NotificationContext';

import Widget from '../../utils/Widget'

import LatestThread from './LatestThread';

export default function LatestThreads() {
    let [ threads, setThreads ] = useState([]);

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let loadThreads = async() => {

            try {
    
                let res = await axios.get('/forums/threads/latest');

                setThreads(res.data.threads);
    
            } catch(error) {
                sendErrorNotification(error);
            }
        };

        loadThreads();

        let threadsInterval = setInterval(loadThreads, 5000);
        return () => clearInterval(threadsInterval);
    }, []);
    return (
        <Widget title="Latest Threads">
            { threads.map(thread => 
                <LatestThread key={thread._id} thread={thread} />
            )}
        </Widget>
    )
}
