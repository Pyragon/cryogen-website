import React, { useState, useEffect, useMemo } from 'react';
import { useParams } from 'react-router-dom';

import axios from '../../utils/axios';
import generateBreadcrumbs from '../../utils/breadcrumbs';

import ViewThread from '../../components/forums/threads/ViewThread';

import ForumContainer from './ForumContainer';
import PageContext from '../../utils/contexts/PageContext';

export default function ThreadPage() {
    let { threadId, page: pageParam } = useParams();
    if(!pageParam)
        pageParam = 1;
    let [ page, setPage ] = useState(Number(pageParam));
    let [ thread, setThread ] = useState(null);
    let [ breadcrumbs, setBreadcrumbs ] = useState([]);
    let pageProvider = useMemo(() => ({ page, setPage }), [ page, setPage ]);
    useEffect(() => {
        if(!threadId) return;
        axios.get(`/forums/threads/${threadId}`)
            .then(results => {
                let thread = results.data;
                let breadcrumbs = generateBreadcrumbs({ thread });
                setBreadcrumbs(breadcrumbs);
                setThread(thread);
            })
            .catch(console.error);
    }, [ threadId ]);
    return (
        <ForumContainer breadcrumbs={breadcrumbs} thread={thread}>
            <PageContext.Provider value={pageProvider}>
                { thread && <ViewThread thread={thread} setThread={setThread}/> }
            </PageContext.Provider>
        </ForumContainer>
    )
}
