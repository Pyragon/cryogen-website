import React, { useEffect, useContext } from 'react'
import AccountWidget from '../components/index/account/AccountWidget'
import IndexButtons from '../components/index/IndexButtons'
import MiniHighscores from '../components/index/highscores/MiniHighscores'

import ForumRecents from '../components/index/ForumRecents'

import setUserActivity from '../utils/user-activity'

import './../styles/index/Index.css'
import UserContext from '../utils/contexts/UserContext'

export default function IndexPage() {
    let { user } = useContext(UserContext);
    useEffect(() => setUserActivity(user, 'Viewing Index', 'index'), [ user ]);
    return (
        <div className="container">
            <div className="grid-span-2">
                <ForumRecents />
            </div>
            <div className="grid-col-3">
                <AccountWidget />
                <IndexButtons />
                <MiniHighscores />
            </div>
        </div>
    )
}
