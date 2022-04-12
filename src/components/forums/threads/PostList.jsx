import React from 'react'
import PostAuthor from '../posts/PostAuthor';
import PostBlock from '../posts/PostBlock';

import '../../../styles/forums/PostList.css'
import ViewMessageBlock from '../private/inbox/ViewMessageBlock';

export default function PostList({ posts, setPosts }) {
    return posts.map(data => 
        <div key={data.post._id} className="post-block">
            <PostAuthor data={data} />
            { !data.isMessage && <PostBlock data={data} setPosts={setPosts} /> }
            { data.isMessage && <ViewMessageBlock message={data} /> }
        </div>
    );
}
