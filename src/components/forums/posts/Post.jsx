import React, { useContext } from 'react';

import UserContext from '../../../utils/UserContext';

import { formatDate } from '../../../utils/format';

export default function Post({ post }) {
    let { user } = useContext(UserContext);
    let style = post.style || {};
    let loggedIn = user !== null, canEdit = post.author._id === user._id, canPost = true;
    return (
        <div key={post._id}>
            <div className="post" style={style}>
                {post.content}
            </div>
            <div className="edit-options">
                { post.edited && <div className="last-edited">{'Lasted Edited: '+formatDate(post.edited)}</div> }
                { loggedIn && canEdit && <div className="edit-post link">Edit</div> }
                { loggedIn && canPost && <div className="quote-post" onClick={() => {/* quote */}}>Quote</div> }
                {/* { loggedIn && user._id !== post.author?._id && 
                    <div 
                        className="thank-post" 
                        onClick={() => {}}
                    >
                        { post.thanks.find(post => post.author._id == userId) ? 'Remove thanks' : 'Thanks' }
                    </div> 
                } */}
            </div>
        </div>
    )
}
