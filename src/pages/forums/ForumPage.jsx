import React, { useState, useEffect } from 'react';
import axios from '../../utils/axios';
import Permissions from '../../utils/permissions';

import { useParams } from 'react-router-dom';

import ViewForum from '../../components/forums/subforums/ViewForum';
import ForumContainer from './ForumContainer';

export default function ForumPage() {
    let { forumId } = useParams();
    let [ forum, setForum ] = useState(null);
    let viewForum = async(forumId) => {
        let result = await axios.get(`/forums/subforums/${forumId}`);
        if(result) {
            let forum = result.data;
            forum.permissions = new Permissions(forum.permissions);
            setForum(forum);
        }
    };
    useEffect(async() => {
        if(!forumId) return;
        await viewForum(forumId);
    }, []);
    return (
        <ForumContainer>
            { forum && <ViewForum forum={forum} viewForum={viewForum} /> }
        </ForumContainer>
    )
}
