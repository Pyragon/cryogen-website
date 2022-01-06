import React, { useState, useEffect } from 'react'

import NewsPost from '../../utils/NewsPost'
import Button from '../../utils/Button';
import SubforumBlock from './SubforumBlock'
import ThreadBlock from '../threads/ThreadBlock';

export default function ViewForum({ forum, viewForum }) {
    let [ subforums, setSubforums ] = useState([]);
    let [ threads, setThreads ] = useState([]);
    let loggedIn = true;
    useEffect(() => {
        fetch('http://localhost:8081/forums/subforums/children/' + forum._id)
            .then(res => res.json())
            .then(res => setSubforums(res));
        fetch('http://localhost:8081/forums/threads/children/' + forum._id)
            .then(res => res.json())
            .then(res => setThreads(res));
    }, [forum]);
    return (
        <>
            { subforums.length > 0 && 
                <NewsPost 
                    title={'Subforums'}
                    index={0}
                >
                    <SubforumBlock forum={forum} viewForum={viewForum} />
                </NewsPost> 
            }
            { loggedIn && (
                <>
                    <Button title='New Thread' className='new-thread-btn' onClick={() => {}} />
                    <div style={{clear: 'both'}} />
                </>
             ) }
            <NewsPost 
                title={forum.name}
                description={forum.description}
                index={1}
            >
                { 
                    threads.length == 0 ?
                        <p className="t-center">No threads have been created yet.</p>
                    :
                        threads.map((thread, index) => (
                            <ThreadBlock key={index} thread={thread} index={index} />
                        ))
                }
            </NewsPost>
        </>
    )
}
