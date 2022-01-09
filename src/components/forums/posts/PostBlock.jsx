import React from 'react';

import { formatDate } from '../../../utils/format';
import Post from './Post';

import DisplayUser from '../../utils/user/DisplayUser';

export default function PostBlock({ post }) {
    return (
        <div key={post._id} className="post-content-block">
            <div className="post-date-block">
                <div className="post-date small">{formatDate(post.createdAt)}</div>
                <div className="post-id small link">{'#'+post._id}</div>
            </div>
            <div className="post-message-block">
                <Post post={post} />
            </div>
            <div className="post-thanks-block small">
                <p style={{margin: '0'}}>
                    { (!post.thanks || post.thanks.length == 0) && <span>No users have thanked this post.</span>}
                    { post.thanks && post.thanks.length > 0 && 
                        <>
                            <span>Users who have thanked this post:</span>
                            { post.thanks.map((user, index) =>
                                <DisplayUser
                                    key={index}
                                    user={user}
                                    suffix={index == post.thanks.length-1 ? '' : ', '}
                                />
                            )}
                        </>
                    }
                </p>
            </div>
            
        </div>
    )
}
