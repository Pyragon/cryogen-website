import React from 'react'
import PostAuthor from '../posts/PostAuthor';
import PostBlock from '../posts/PostBlock';

import '../../../styles/forums/PostList.css'
import ViewMessageBlock from '../private/inbox/ViewMessageBlock';

export default function PostList({ posts, setPosts }) {
    return posts.map(post => {
        return (
            <div key={post._id} className="post-block">
                <PostAuthor post={post} />
                { !post.isMessage && <PostBlock post={post} setPosts={setPosts} /> }
                { post.isMessage && <ViewMessageBlock message={post} /> }
            </div>
        )
    });
}
