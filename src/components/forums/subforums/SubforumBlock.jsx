import React, { useState, useEffect } from 'react';

import { formatDate } from '../../../utils/format';

import { Link } from 'react-router-dom';

import DisplayUser from '../../utils/user/DisplayUser';

import '../../../styles/forums/Subforum.css';

export default function SubforumBlock({ forum }) {
    let [ subforums, setSubforums ] = useState([]);
    useEffect(() => {
        fetch('http://localhost:8081/forums/subforums/' + forum._id)
            .then(res => res.json())
            .then(res => setSubforums(res));
    }, []);
    return (
        <>
            { subforums.map((subforum, index) => {
                console.log(subforum);
                return (
                    <div key={index} className="subforum">
                        <div className="subforum-description">
                            { subforum.link ?
                                <a className="link" href={subforum.link}>
                                    {subforum.name}
                                </a>
                                : <Link to={"/forums/"+subforum._id} className="link">{subforum.name} </Link> }               
                            <div className="m-top-5 small grey">{subforum.description}</div>
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
                            { 
                            subforum.extraData && subforum.extraData?.lastPost && 
                                <>
                                    <Link to={'/forums/thread/'+subforum.extraData?.lastPost?.thread?._id} className="link">
                                        {subforum.extraData?.lastPost.thread.title}
                                    </Link>
                                    <div className="m-top-5 small">
                                        <DisplayUser 
                                            user={subforum.extraData?.lastPost.author} 
                                            prefix='by '
                                            suffix={ formatDate(subforum.extraData?.lastPost.createdAt)}
                                        />
                                    </div>
                                </>
                            }
                        </div>
                        <div className="subforum-stats">
                            <p className="small">{'Threads: '+subforum.extraData?.totalThreads || 0}</p>
                            <div className="m-top-5 small">{'Posts: '+subforum.extraData?.totalPosts || 0}</div>
                        </div>
                    </div>
                )
            }) }
        </>
    )
}
