import React, { useState, useEffect } from 'react';

import { useParams } from 'react-router-dom';

import ViewForum from '../../components/forums/subforums/ViewForum';
import ForumContainer from './ForumContainer';

export default function ForumPage() {
    let { forumId } = useParams();
    let [ forum, setForum ] = useState(null);
    let viewForum = async(forumId) => {
        let result = await fetch(`http://localhost:8081/forums/subforums/${forumId}`);
        if(result) {
            let data = await result.json();
            setForum(data);
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
