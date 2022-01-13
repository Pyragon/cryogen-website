import React, { useEffect, useState } from 'react'

import axios from '../../../utils/axios';

import Widget from '../../utils/Widget'

import LatestThread from './LatestThread';

async function fetchLatestThreads(setThreads) {
    let response = await axios.get('/forums/threads/latest');
    if(!response || !response.data) return;
    setThreads(response.data);
}

export default function LatestThreads() {
    let [ threads, setThreads ] = useState([]);
    useEffect(() => {
        fetchLatestThreads(setThreads);
        let threadsInterval = setInterval(() => fetchLatestThreads(setThreads), 5000);
        return () => clearInterval(threadsInterval);
        //not clearing like it should be
    }, []);
    return (
        <Widget title="Latest Threads">
            { threads.map(thread => 
                <LatestThread key={thread._id} thread={thread} />
            )}
        </Widget>
    )
}
