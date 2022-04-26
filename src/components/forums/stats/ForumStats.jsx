import React, { useState, useEffect, useContext } from 'react';
import axios from '../../../utils/axios';

import Widget from '../../utils/Widget'
import ForumStat from './ForumStat';

import NotificationContext from '../../../utils/contexts/NotificationContext';

import '../../../styles/forums/ForumStats.css';

export default function ForumStats() {
    let [ stats, setStats ] = useState({});

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let fetch = async () => {

            try {

                let res = await axios.get('/forums/stats');

                setStats(res.data.stats);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        fetch();

        let interval = setInterval(fetch, 5000);

        return () => clearInterval(interval);

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
