import React from 'react';

import { formatDate } from '../../utils/format';

import { Link } from 'react-router-dom';

export default function SubforumBlock({ forum }) {
    return (
        <div className="forum">
            <div className="subforum-description">
                { forum.isLink ?
                    <a href={forum.link} target="_blank">
                        {forum.name}
                    </a>
                    : <Link to={"/forum/"+forum._id} >{forum.name} </Link> }               
                <p className="small grey">{forum.description}</p>
                { forum.subforums?.length > 0 && (
                    <>
                        <p className="small">Subforums:</p>
                        { forum.subforums.slice(0, 4).map((subforum) => (
                            <>
                                <Link className="small white" to={"/forum/"+subforum._id} key={subforum._id}>{subforum.name}</Link>
                            </>
                        )) }
                    </>
                ) }
            </div>
            <div className="subforum-last-post">
                <Link to="" className="subforum-last-post-title">{forum.lastPost?.title}</Link>
                <div>
                    <span className="small"> by </span>
                    <Link to={"/user/"+forum.lastPost?.author} className="subforum-last-post-author">{forum.lastPost?.author}</Link>
                </div>
                <div className="subforum-last-post-date small">
                    {formatDate(forum.lastPost?.createdAt)}
                </div>
            </div>
            <div className="subforum-stats">
                <p className="small">{'Threads: '+forum.totalThreads}</p>
                <p className="small">{'Posts: '+forum.totalPosts}</p>
            </div>
        </div>
    )
}
