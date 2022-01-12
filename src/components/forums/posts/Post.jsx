import React from 'react';

export default function Post({ post }) {
    let style = post.style || {};
    return (
        <div key={post._id}>
            <div className="post" style={style}>
                {post.content}
            </div>
        </div>
    )
}
