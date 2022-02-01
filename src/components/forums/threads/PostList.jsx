import React from 'react'
import PostAuthor from '../posts/PostAuthor';
import PostBlock from '../posts/PostBlock';

import '../../../styles/forums/PostList.css'

export default function PostList({ posts, setPosts }) {
    return posts.map(data => 
        <div key={data.post._id} className="post-block">
            <PostAuthor data={data} />
            <PostBlock data={data} setPosts={setPosts} />
        </div>
    );
}
