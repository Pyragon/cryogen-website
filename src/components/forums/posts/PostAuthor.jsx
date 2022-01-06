import React from 'react';

import DisplayUser from '../../utils/user/DisplayUser';

export default function PostAuthor({ user }) {
    return (
        <div className="author-block">
            <DisplayUser user={user} />
        </div>
    )
}
