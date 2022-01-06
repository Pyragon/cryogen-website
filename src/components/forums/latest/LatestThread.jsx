import React from 'react';

import { Link } from 'react-router-dom';

import { formatDate } from '../../../utils/format';

import DisplayUser from '../../utils/user/DisplayUser';

import '../../../styles/forums/LatestThreads.css';

export default function LatestThread({ thread }) {
    return (
        <div key={thread._id} className="latest-thread">
            <Link to={"/forums/thread/"+thread._id} className="latest-thread-title white link">
                {thread.title}
            </Link>
            <div className="latest-thread-author small">
                By
                <DisplayUser user={thread.author} prefix=' ' suffix=', ' />
                {formatDate(thread.createdAt)}
            </div>
        </div>
    )
}
