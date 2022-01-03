import React, { useState, useEffect } from 'react'

import NewsPost from '../utils/NewsPost';

import { crownUser } from '../../utils/format';

export default function ForumRecents() {
    let [ threads, setThreads ] = useState([]);
    useEffect(() => {
        fetch('http://localhost:8081/forums/thread/news')
            .then(res => res.json())
            .then(res => setThreads(res));
    }, []);
    return (
        <>
            <h4 className="title">Recent News:</h4>
            { threads.map((data, index) => ( <NewsPost key={data.thread._id} title={data.thread.title} description={"Posted Today by "+crownUser(data.posts[0].author)}  index={index}>
                {data.posts[0].content}
            </NewsPost> ) )}
        </>
    )
}
