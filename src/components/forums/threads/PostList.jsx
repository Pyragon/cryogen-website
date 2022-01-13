import React from 'react'
import PostAuthor from '../posts/PostAuthor';
import PostBlock from '../posts/PostBlock';

import '../../../styles/forums/PostList.css'

export default function PostList({ posts }) {
    return posts.map((data, index) => 
        <div key={index} className="post-block">
            <PostAuthor data={data} />
            <PostBlock data={data} />
        </div>
    );
}
