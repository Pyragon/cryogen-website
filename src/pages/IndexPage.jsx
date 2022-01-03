import React from 'react'
import AccountWidget from '../components/index/account/AccountWidget'
import IndexButtons from '../components/index/IndexButtons'
import MiniHighscores from '../components/index/highscores/MiniHighscores'

import ForumRecents from './../components/index/recents/ForumRecents'

import './../styles/index/Index.css'

export default function IndexPage() {
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
