import React, { useState, useEffect } from 'react'

import RecentPost from './RecentPost'

import './../../../styles/index/ForumRecents.css'

export default function ForumRecents() {
    let [ threads, setThreads ] = useState([]);
    useEffect(() => {
        fetch('http://localhost:8081/forums/thread/news')
            .then(res => res.json())
            .then(res => {
                setThreads(res);
            });
    }, []);
    return (
        <>
            <h4 className="title">Recent News:</h4>
            { threads.map((thread, index) => ( <RecentPost key={index} thread={thread} index={index}/> ))}
        </>
    )
}
