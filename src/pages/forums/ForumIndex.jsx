import React from 'react';

import ForumStats from '../../components/forums/stats/ForumStats';
import LatestThreads from '../../components/forums/latest/LatestThreads';
import Subforums from '../../components/forums/subforums/Subforums';
import Chatbox from '../../components/forums/chatbox/Chatbox';

export default function ForumIndex() {
    return (
        <div className="container">
            <Chatbox />
            <div className="grid-span-2">
                <Subforums />
            </div>
            <div className="grid-col-3">
                <ForumStats />
                <LatestThreads />
            </div>
        </div>
    )
}
