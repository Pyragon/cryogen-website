import React, { useState, useEffect } from 'react';

import { useParams } from 'react-router-dom';

import Chatbox from '../../components/forums/chatbox/Chatbox';
import ViewThread from '../../components/forums/threads/ViewThread';
import ForumStats from '../../components/forums/stats/ForumStats';
import LatestThreads from '../../components/forums/latest/LatestThreads';

export default function ThreadPage() {
    let { threadId } = useParams();
    let [ thread, setThread ] = useState(null);
    useEffect(async() => {
        if(!threadId) return;
        let result = await fetch(`http://localhost:8081/forums/threads/${threadId}`);
        if(result) {
            let data = await result.json();
            setThread(data);
        }
    }, []);
    return (
        <div className="container">
            <Chatbox />
            <div className="grid-span-2">
                { thread && <ViewThread thread={thread}/> }
            </div>
            <div className="grid-col-3">
                <ForumStats />
                <LatestThreads />
            </div>
        </div>
    )
}
