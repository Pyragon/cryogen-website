import React, { useState, useEffect } from 'react';
import axios from '../../utils/axios';

import { useParams } from 'react-router-dom';

import ViewThread from '../../components/forums/threads/ViewThread';

import ForumContainer from './ForumContainer';

export default function ThreadPage() {
    let { threadId } = useParams();
    let [ thread, setThread ] = useState(null);
    useEffect(async() => {
        if(!threadId) return;
        let results = await axios.get(`http://localhost:8081/forums/threads/${threadId}`);
        if(results)
            setThread(results.data);
    }, []);
    return (
        <ForumContainer>
            { thread && <ViewThread thread={thread}/> }
        </ForumContainer>
    )
}
