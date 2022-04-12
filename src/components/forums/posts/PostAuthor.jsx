import React from 'react';

import DisplayUser from '../../utils/user/DisplayUser';

import SpanIcon from '../../utils/SpanIcon';

export default function PostAuthor({ data }) {
    return (
        <div className="post-author-block">
            <DisplayUser 
                user={data.post.author} 
                useUserTitle={true}
                avatar={data.post.author.avatar || '/images/default_avatar.png'}
            />
            <SpanIcon icon="fa-clipboard" className="post-author-block-line small">
                {' Post Count: '+data.postCount}
            </SpanIcon>
            <SpanIcon icon="fa-thumbs-up" className="post-author-block-line small">
                {' Thanks Received: '+data.thanksReceived}
            </SpanIcon>
            <SpanIcon icon="fa-thumbs-up" className="post-author-block-line small">
                {' Thanks Given: '+data.thanksGiven}
            </SpanIcon>
        </div>
    )
}
