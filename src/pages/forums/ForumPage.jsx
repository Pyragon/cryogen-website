import React, { useState, useEffect } from 'react';
import axios from '../../utils/axios';
import Permissions from '../../utils/permissions';

import { useParams } from 'react-router-dom';

import ViewForum from '../../components/forums/subforums/ViewForum';
import ForumContainer from './ForumContainer';

export default function ForumPage() {
    let { forumId } = useParams();
    let [ forum, setForum ] = useState(null);
    useEffect(() => {
        if(!forumId) return;
        axios.get(`/forums/subforums/${forumId}`)
            .then(results => {
                let forum = results.data;
                forum.permissions = new Permissions(forum.permissions);
                setForum(forum);
            })
            .catch(console.error);
    }, [ forumId ]);
    return (
        <ForumContainer>
            { forum && <ViewForum forum={forum} /> }
        </ForumContainer>
    )
}
