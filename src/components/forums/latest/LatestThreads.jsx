import React, { useEffect, useState } from 'react'

import Widget from '../../utils/Widget'

import LatestThread from './LatestThread';

async function fetchLatestThreads(setThreads) {
    setThreads(await (await fetch('http://localhost:8081/forums/threads/latest')).json());
}

export default function LatestThreads() {
    let [ threads, setThreads ] = useState([]);
    useEffect(() => {
        fetchLatestThreads(setThreads);
        let threadsInterval = setInterval(() => fetchLatestThreads(setThreads), 5000);
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
