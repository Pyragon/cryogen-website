import React from 'react';

import DisplayUser from '../../utils/user/DisplayUser';

import SpanIcon from '../../utils/SpanIcon';

export default function PostAuthor({ user }) {
    return (
        <div className="post-author-block">
            <DisplayUser 
                user={user} 
                useTitle={true}
                avatar={user.avatar || '/images/default_avatar.png'}
            />
            <SpanIcon icon="fa-clipboard" className="post-author-block-line small">
                {' Post Count: '+user.postCount}
            </SpanIcon>
            <SpanIcon icon="fa-thumbs-up" className="post-author-block-line small">
                {' Thanks Received: '+user.thanksReceived}
            </SpanIcon>
            <SpanIcon icon="fa-thumbs-up" className="post-author-block-line small">
                {' Thanks Given: '+user.thanksGiven}
            </SpanIcon>
            <div className="total-level small">
            </div>
        </div>
    )
}
