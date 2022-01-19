import React from 'react';
import { formatDate } from '../../../utils/format';
import { Link } from 'react-router-dom';

import DisplayUser from '../../utils/user/DisplayUser';

export default function ThreadBlock({ thread, index }) {
    return (
        <div className="thread">
            <div className="thread-header">
                <div className="thread-title">
                    { thread.pinned && <span className="fa fa-thumb-tack green" title='This thread has been pinned.' /> }
                    { !thread.open && <span className="fa fa-lock red" title='This thread has been locked.'/> }
                    <span>
                        <Link to={`/forums/threads/${thread._id}`} className="link">{' '+thread.title}</Link>
                    </span>
                </div>
                <div className="thread-info small">
                    <DisplayUser 
                        user={thread.author}
                        prefix='Started by '
                        suffix={ ', '+formatDate(thread.createdAt)}
                    />
                </div>
            </div>
            <div className="thread-last-post small">
                <DisplayUser
                    user={thread.lastPost?.author}
                    prefix='Last post by '
                    suffix={ ', '+formatDate(thread.lastPost?.createdAt)}
                />
            </div>
            <div className="thread-view-info">
                <p className="small">{'Replies: '+thread.postCount}</p>
                <div className="small m-top-5">{'Views: '+thread.views}</div>
            </div>
        </div>
    )
}
