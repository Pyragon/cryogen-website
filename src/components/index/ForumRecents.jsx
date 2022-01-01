import React from 'react'

import RecentPost from './RecentPost'

import './../../styles/index/ForumRecents.css'

const posts = require("./posts.json");

export default function ForumRecents() {
    return (
        <div className="grid-span-2">
            <h4 className="title">Recent News:</h4>
            { posts.map((post, index) => {
                return (
                    <RecentPost key={index} post={post} index={index}/>
                )
            })}
        </div>
    )
}
