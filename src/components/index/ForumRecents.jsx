import React, { useState, useEffect } from 'react'

import CollapsibleWidget from '../utils/CollapsibleWidget';
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
            { threads.map((thread, index) => ( 
                <CollapsibleWidget 
                    key={thread._id} 
                    title={thread.title} 
                    link={`/forums/thread/${thread._id}`}
                    description={
                        <DisplayUser 
                            user={thread.firstPost?.author} 
                            prefix='Posted Today by '
                        />
                    }  
                    index={index}
                    collapsed={index !== 0 && index !== 1}
                >
                    {thread.firstPost?.content}
                </CollapsibleWidget> ) )
            }
        </>
    )
}
