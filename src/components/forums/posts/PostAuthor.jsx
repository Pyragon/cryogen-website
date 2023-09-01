import React from 'react';

import DisplayUser from '../../utils/user/DisplayUser';

import SpanIcon from '../../utils/SpanIcon';

export default function PostAuthor({ post }) {
    return (
        <div className="post-author-container">
            <div className='post-author-block'>
                <DisplayUser 
                    user={post.author} 
                    useUserTitle={true}
                    avatar={post.author.settings.avatar || '/images/default_avatar.png'}
                />
                <SpanIcon icon="fa-clipboard" className="post-author-block-line small">
                    {' Post Count: '+post.postCount}
                </SpanIcon>
                <SpanIcon icon="fa-thumbs-up" className="post-author-block-line small">
                    {' Thanks Received: '+post.thanksReceived}
                </SpanIcon>
                <SpanIcon icon="fa-thumbs-up" className="post-author-block-line small">
                    {' Thanks Given: '+post.thanksGiven}
                </SpanIcon>
                <SpanIcon icon="fa-thumbs-up" className="post-author-block-line small">
                    {' In-game Total: '+(post.totalLevel === -1 ? 'N/A' : post.totalLevel)}
                </SpanIcon>
            </div>
        </div>
    )
}
