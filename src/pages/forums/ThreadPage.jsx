import React, { useState, useEffect, useMemo } from 'react';
import axios from '../../utils/axios';

import { useParams } from 'react-router-dom';

import ViewThread from '../../components/forums/threads/ViewThread';

import ForumContainer from './ForumContainer';
import PageContext from '../../utils/PageContext';

export default function ThreadPage() {
    let { threadId, page: pageParam } = useParams();
    if(!pageParam)
        pageParam = 1;
    let [ page, setPage ] = useState(Number(pageParam));
    let [ thread, setThread ] = useState(null);
    let providerValue = useMemo(() => ({ page, setPage }), [ page, setPage ]);
    useEffect(() => {
        if(!threadId) return;
        axios.get(`http://localhost:8081/forums/threads/${threadId}`)
            .then(results => setThread(results.data))
            .catch(console.error);
    }, [ threadId ]);
    return (
        <ForumContainer thread={thread}>
            <PageContext.Provider value={providerValue}>
                { thread && <ViewThread thread={thread}/> }
            </PageContext.Provider>
        </ForumContainer>
    )
}
