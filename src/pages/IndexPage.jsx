import React from 'react'
import AccountWidget from '../components/index/account/AccountWidget'
import IndexButtons from '../components/index/IndexButtons'
import MiniHighscores from '../components/index/MiniHighscores'

import ForumRecents from './../components/index/ForumRecents'

import './../styles/index/Index.css'

export default function IndexPage() {
    return (
        <div className="container">
            <ForumRecents />
            <AccountWidget />
            <IndexButtons />
            <MiniHighscores />
        </div>
    )
}
