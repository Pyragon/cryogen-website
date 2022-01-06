import React, { useState, useEffect } from 'react'

import NewsPost from '../utils/NewsPost';
import DisplayUser from '../utils/user/DisplayUser';

export default function ForumRecents() {
    let [ threads, setThreads ] = useState([]);
    useEffect(() => {
        fetch('http://localhost:8081/forums/threads/news')
            .then(res => res.json())
            .then(res => setThreads(res));
    }, []);
    return (
        <>
            <h4 className="title">Recent News:</h4>
            { threads.map((data, index) => ( 
                <NewsPost 
                    key={data.thread._id} 
                    title={data.thread.title} 
                    link={`/forums/thread/${data.thread._id}`}
                    description={
                        <DisplayUser 
                            user={data.posts[0].author} 
                            prefix='Posted Today by '
                        />
                    }  
                    index={index}
                    collapsed={index !== 0 && index !== 1}
                >
                    {data.posts[0].content}
                </NewsPost> ) )
            }
        </>
    )
}
