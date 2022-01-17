import React, { useState, useEffect } from 'react';
import axios from '../../utils/axios';

import { useParams } from 'react-router-dom';

import ViewThread from '../../components/forums/threads/ViewThread';

import ForumContainer from './ForumContainer';

export default function ThreadPage() {
    let { threadId } = useParams();
    let [ thread, setThread ] = useState(null);
    useEffect(() => {
        if(!threadId) return;
        axios.get(`http://localhost:8081/forums/threads/${threadId}`)
            .then(results => setThread(results.data))
            .catch(console.error);
    }, [ threadId ]);
    return (
        <ForumContainer>
            { thread && <ViewThread thread={thread}/> }
        </ForumContainer>
    )
}
