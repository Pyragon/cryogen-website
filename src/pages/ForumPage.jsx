import React, { useState } from 'react';

import ForumStats from '../components/forums/stats/ForumStats';
import LatestThreads from '../components/forums/LatestThreads';
import ViewThread from '../components/forums/ViewThread';
import Subforums from '../components/forums/Subforums';
import ViewForum from '../components/forums/ViewForum';

export default function ForumPage() {
    let [ viewingThread, setViewingThread ] = useState(-1);
    let [ viewingForum, setViewingForum ] = useState(-1);
    return (
        <div className="container">
            <div className="grid-span-2">
                { viewingThread !== -1 && <ViewThread threadId={viewingThread} /> }
                { viewingForum !== -1 && <ViewForum forumId={viewingForum} /> }
                { viewingThread === -1 && viewingForum === -1 && <Subforums /> }
            </div>
            <div className="grid-col-3">
                <ForumStats />
                <LatestThreads />
            </div>
        </div>
    )
}
