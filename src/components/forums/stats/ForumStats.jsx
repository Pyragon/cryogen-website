import React, { useState, useEffect } from 'react'

import Widget from '../../utils/Widget'

import '../../../styles/forums/ForumStats.css';
import ForumStat from './ForumStat';

async function fetchStats(stats, setStats) {
    let newStats = await fetch('http://localhost:8081/forums/stats');
    newStats = await newStats.json();
    if(JSON.stringify(stats) === JSON.stringify(newStats))
        return;
    setStats(newStats);
}

export default function ForumStats() {
    let [ stats, setStats ] = useState({});
    useEffect(() => {
        let statsInterval = setInterval(() => fetchStats(stats, setStats), 5000);
        fetchStats(stats, setStats);
        return () => clearInterval(statsInterval);
    }, []);
    return (
        <Widget title="Forum Stats" description="Real-Time stats about the forums">
            <ForumStat title="Registered Users" value={stats.registered || 0} />
            <ForumStat title="Users Online" value={stats.online || 0} />
            <ForumStat title="Most Online" value={stats.mostOnline || 0} />
            <ForumStat title="Total Threads" value={stats.threads || 0} />
            <ForumStat title="Total Posts" value={stats.posts || 0} />
        </Widget>
    )
}
