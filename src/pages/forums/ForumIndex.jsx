import React, { useEffect, useContext } from 'react';

import setUserActivity from '../../utils/user-activity';
import UserContext from '../../utils/contexts/UserContext';
import generateBreadcrumbs from '../../utils/breadcrumbs';

import Subforums from '../../components/forums/subforums/Subforums';
import ForumContainer from './ForumContainer';

export default function ForumIndex() {
    let { user } = useContext(UserContext);
    let breadcrumbs = generateBreadcrumbs({});
    useEffect(() => setUserActivity(user, 'Viewing Forum Index', 'subforum'), [ user ]);
    return (
        <ForumContainer index={true} breadcrumbs={breadcrumbs}>
            <Subforums />
        </ForumContainer>
    )
}
