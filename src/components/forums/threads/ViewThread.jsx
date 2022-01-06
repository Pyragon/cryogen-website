import React, { useState, useEffect } from 'react';
import NewsPost from '../../utils/NewsPost';

import PostList from './PostList';

export default function ViewThread({ thread }) {
    let [ posts, setPosts ] = useState([]);
    useEffect(() => {
        fetch('http://localhost:8081/forums/posts/children/'+thread._id)
        .then(res => res.json())
        .then(data => setPosts(data));
    }, []);
    return (
        <>
        <NewsPost
            title={thread.title}
            minimizable={false}
        >
            { posts && <PostList posts={posts} /> }
        </NewsPost>
        </>
    )
}
