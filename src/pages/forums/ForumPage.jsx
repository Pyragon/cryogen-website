import React, { useState, useEffect, useContext } from 'react';
import axios from '../../utils/axios';
import Permissions from '../../utils/permissions';
import generateBreadcrumbs from '../../utils/breadcrumbs';
import setUserActivity from '../../utils/user-activity';

import { useParams } from 'react-router-dom';

import ViewForum from '../../components/forums/subforums/ViewForum';
import ForumContainer from './ForumContainer';
import UserContext from '../../utils/contexts/UserContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

export default function ForumPage() {
    let { user } = useContext(UserContext);
    let { forumId } = useParams();
    let [ forum, setForum ] = useState(null);
    let [ breadcrumbs, setBreadcrumbs ] = useState([]);

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let loadForum = async () => {

            try {

                let res = await axios.get(`/forums/subforums/${forumId}`);

                let forum = res.data.forum;
                let breadcrumbs = generateBreadcrumbs({ subforum: forum });
                forum.permissions = new Permissions(forum.permissions);
                setForum(forum);
                setBreadcrumbs(breadcrumbs);
                setUserActivity(user, 'Viewing forum: '+forum.name, 'forum', forum._id);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        loadForum();
        
    }, [ forumId, user ]);

    return (
        <ForumContainer breadcrumbs={breadcrumbs}>
            { forum && <ViewForum forum={forum} /> }
        </ForumContainer>
    )
}
