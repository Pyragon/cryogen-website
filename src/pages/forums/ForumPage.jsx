import React, { useState, useEffect } from 'react';
import axios from '../../utils/axios';
import Permissions from '../../utils/permissions';
import generateBreadcrumbs from '../../utils/breadcrumbs';

import { useParams } from 'react-router-dom';

import ViewForum from '../../components/forums/subforums/ViewForum';
import ForumContainer from './ForumContainer';

export default function ForumPage() {
    let { forumId } = useParams();
    let [ forum, setForum ] = useState(null);
    let [ breadcrumbs, setBreadcrumbs ] = useState([]);
    useEffect(() => {
        if(!forumId) return;
        axios.get(`/forums/subforums/${forumId}`)
            .then(results => {
                let forum = results.data;
                let breadcrumbs = generateBreadcrumbs({ subforum: forum });
                forum.permissions = new Permissions(forum.permissions);
                setForum(forum);
                setBreadcrumbs(breadcrumbs);
                console.log(breadcrumbs);
            })
            .catch(console.error);
    }, [ forumId ]);
    return (
        <ForumContainer breadcrumbs={breadcrumbs}>
            { forum && <ViewForum forum={forum} /> }
        </ForumContainer>
    )
}
