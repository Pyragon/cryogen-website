import React, { useContext } from 'react'

import UserContext from '../../utils/contexts/UserContext';
import Chatbox from '../../components/forums/chatbox/Chatbox';
import ForumStats from '../../components/forums/stats/ForumStats';
import LatestThreads from '../../components/forums/latest/LatestThreads';
import UsersViewingThread from '../../components/forums/threads/UsersViewingThread';
import LoginWidget from '../../components/index/account/LoginWidget';
import OnlineUsers from '../../components/forums/OnlineUsers';
import Poll from '../../components/forums/threads/polls/Poll';

import Breadcrumbs from '../../components/forums/crumbs/Breadcrumbs';
import DiscordWidget from '../../components/utils/DiscordWidget';

export default function ForumContainer({ children, index=false, thread, breadcrumbs }) {
    let { user } = useContext(UserContext);
    return (
        <div className="container">
            <Chatbox />
            <div className="grid-span-2">
                <Breadcrumbs
                    breadcrumbs={breadcrumbs}
                />
                { children }
            </div>
            <div className="grid-col-3">
                { user === null && <LoginWidget header={false}/>}
                { thread && thread.poll && <Poll thread={thread}/> }
                { thread && <UsersViewingThread thread={thread}/> }
                <ForumStats />
                <DiscordWidget />
                <LatestThreads />
            </div>
            { index && <OnlineUsers />}
        </div>
    )
}
