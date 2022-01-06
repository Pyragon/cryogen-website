import React, { useState, useEffect } from 'react';

import { useParams } from 'react-router-dom';

import ForumStats from '../../components/forums/stats/ForumStats';
import LatestThreads from '../../components/forums/latest/LatestThreads';
import ViewThread from '../../components/forums/threads/ViewThread';
import Subforums from '../../components/forums/subforums/Subforums';
import ViewForum from '../../components/forums/subforums/ViewForum';
import Chatbox from '../../components/forums/chatbox/Chatbox';

export default function ForumPage() {
    let { forumId } = useParams();
    let [ forum, setForum ] = useState(null);
    let viewForum = async(forumId) => {
        let result = await fetch(`http://localhost:8081/forums/subforums/${forumId}`);
        if(result) {
            let data = await result.json();
            setForum(data);
        }
    };
    useEffect(async() => {
        if(!forumId) return;
        await viewForum(forumId);
    }, []);
    return (
        <div className="container">
            <Chatbox />
            <div className="grid-span-2">
                { forum && <ViewForum forum={forum} viewForum={viewForum} /> }
            </div>
            <div className="grid-col-3">
                <ForumStats />
                <LatestThreads />
            </div>
        </div>
    )
}
