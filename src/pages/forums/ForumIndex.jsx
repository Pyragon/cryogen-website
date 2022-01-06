import React, { useState, useEffect } from 'react';

import { useParams } from 'react-router-dom';

import ForumStats from '../../components/forums/stats/ForumStats';
import LatestThreads from '../../components/forums/latest/LatestThreads';
import ViewThread from '../../components/forums/ViewThread';
import Subforums from '../../components/forums/subforums/Subforums';
import ViewForum from '../../components/forums/ViewForum';
import Chatbox from '../../components/forums/chatbox/Chatbox';

export default function ForumPage() {
    let { forumId } = useParams();
    let [ forum, setForum ] = useState({});
    useEffect(async() => {
        let result = await fetch(`http://localhost:8081/forums/subforums/${forumId}`);
        if(result)
            setForum(await result.json());
    }, []);
    return (
        <div className="container">
            <Chatbox />
            <div className="grid-span-2">
                { forum && <ViewForum forum={forum} /> }
                { !forum && <Subforums /> }
            </div>
            <div className="grid-col-3">
                <ForumStats />
                <LatestThreads />
            </div>
        </div>
    )
}
