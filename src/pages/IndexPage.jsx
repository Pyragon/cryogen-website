import React from 'react'
import AccountWidget from '../components/index/AccountWidget'
import IndexButtons from '../components/index/IndexButtons'
import MiniHighscores from '../components/index/MiniHighscores'

import ForumRecents from './../components/index/ForumRecents'

export default function IndexPage() {
    return (
        <>
            <ForumRecents />
            <AccountWidget />
            <IndexButtons />
            <MiniHighscores />
        </>
    )
}
