import React, { useContext } from 'react'

import UserContext from '../../utils/UserContext';
import Chatbox from '../../components/forums/chatbox/Chatbox';
import ForumStats from '../../components/forums/stats/ForumStats';
import LatestThreads from '../../components/forums/latest/LatestThreads';
import LoginWidget from '../../components/index/account/LoginWidget';

export default function ForumContainer({ children }) {
    let { user } = useContext(UserContext);
    return (
        <div className="container">
            <Chatbox />
            <div className="grid-span-2">
                { children }
            </div>
            <div className="grid-col-3">
                { user === null && <LoginWidget header={false}/>}
                <ForumStats />
                <LatestThreads />
            </div>
        </div>
    )
}
