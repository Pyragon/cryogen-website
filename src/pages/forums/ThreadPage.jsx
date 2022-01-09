import React, { useState, useEffect } from 'react';

import { useParams } from 'react-router-dom';

import ViewThread from '../../components/forums/threads/ViewThread';

import ForumContainer from './ForumContainer';

export default function ThreadPage() {
    let { threadId } = useParams();
    let [ thread, setThread ] = useState(null);
    useEffect(async() => {
        if(!threadId) return;
        let result = await fetch(`http://localhost:8081/forums/threads/${threadId}`);
        if(result) {
            let data = await result.json();
            setThread(data);
        }
    }, []);
    return (
        <ForumContainer>
            { thread && <ViewThread thread={thread}/> }
        </ForumContainer>
    )
}
