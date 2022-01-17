import React, { useEffect, useContext } from 'react';

import setUserActivity from '../../utils/user-activity';
import UserContext from '../../utils/UserContext';

import Subforums from '../../components/forums/subforums/Subforums';
import ForumContainer from './ForumContainer';

export default function ForumIndex() {
    let { user } = useContext(UserContext);
    useEffect(() => {
        setUserActivity(user, 'Viewing Index');
    }, [ user ]);
    return (
        <ForumContainer index={true}>
            <Subforums />
        </ForumContainer>
    )
}
