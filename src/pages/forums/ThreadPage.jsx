import React, { useState, useEffect, useMemo, useContext } from 'react';
import { useParams } from 'react-router-dom';

import axios from '../../utils/axios';
import generateBreadcrumbs from '../../utils/breadcrumbs';

import ViewThread from '../../components/forums/threads/ViewThread';

import ForumContainer from './ForumContainer';
import PageContext from '../../utils/contexts/PageContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

export default function ThreadPage() {
    let { threadId, page: pageParam } = useParams();
    let [ page, setPage ] = useState(Number(pageParam) || 1);
    let [ thread, setThread ] = useState(null);
    let [ breadcrumbs, setBreadcrumbs ] = useState([]);
    let pageProvider = useMemo(() => ({ page, setPage }), [ page, setPage ]);

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let loadThread = async () => {

            try {

                let res = await axios.get(`/forums/threads/${threadId}`);

                setThread(res.data.thread);
                setBreadcrumbs(generateBreadcrumbs(res.data));

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        loadThread();

    }, [ threadId ]);
    return (
        <ForumContainer breadcrumbs={breadcrumbs} thread={thread}>
            <PageContext.Provider value={pageProvider}>
                { thread && <ViewThread thread={thread} setThread={setThread}/> }
            </PageContext.Provider>
        </ForumContainer>
    )
}
