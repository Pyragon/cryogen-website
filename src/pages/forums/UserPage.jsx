import React, { useEffect, useState, useContext } from 'react';

import axios from '../../utils/axios';
import generateBreadcrumbs from '../../utils/breadcrumbs';

import { useParams } from 'react-router-dom';

import ForumContainer from './ForumContainer';

import NotificationContext from '../../utils/contexts/NotificationContext';
import ViewingUserContext from '../../utils/contexts/ViewingUserContext';
import CollapsibleWidget from '../../components/utils/CollapsibleWidget';
import VisitorMessages from '../../components/account/profile/VisitorMessages';
import Button from '../../components/utils/Button';
import About from '../../components/account/profile/About';
import UserPosts from '../../components/account/profile/UserPosts';
import UserThreads from '../../components/account/profile/UserThreads';

import SpanIcon from '../../components/utils/SpanIcon';

import '../../styles/account/UserProfile.css';
import DisplayUser from '../../components/utils/user/DisplayUser';

const buttons = [
    {
        title: 'Visitor Messages',
        description: 'A list of visitors messages that can be left on profiles.',
        content: <VisitorMessages />
    },
    {
        title: 'About',
        description: 'An about section for this user',
        content: <About />
    },
    {
        title: 'Posts',
        description: 'Posts made by this user',
        content: <UserPosts />
    },
    {
        title: 'Threads',
        description: 'Threads started by this user',
        content: <UserThreads />
    }
];

export default function UserPage() {
    let { id } = useParams();
    let [ user, setUser ] = useState(null);
    let [ breadcrumbs, setBreadcrumbs ] = useState([]);
    let { sendErrorNotification } = useContext(NotificationContext);

    let [ widgetTitle, setWidgetTitle ] = useState('Visitor Messages');
    let [ widgetDescription, setWidgetDescription ] = useState('A list of visitors messages that can be left on profiles.');
    let [ widgetContent, setWidgetContent ] = useState(<VisitorMessages />);

    //debating whether or not to allow a '/section' param to be passed to this page
    let [ activeButton, setActiveButton ] = useState(0);

    let avatar = user && user.avatar ? user.avatar : '/images/default_avatar.png'

    useEffect(() => {

        let loadUser = async() => {

            try {

                let res = await axios.get(`/users/${id}`);
    
                setUser(res.data.user);
                setBreadcrumbs(generateBreadcrumbs(res.data));
            } catch(error) {
                sendErrorNotification(error);
            }
        };

        loadUser();

    }, []);

    useEffect(() => {

        let button = buttons[activeButton];
        if(!button) return;

        setWidgetTitle(button.title);
        setWidgetDescription(button.description);
        setWidgetContent(button.content);

    }, [ activeButton ]);
    
    if(!user)
        return <></>
    return (
        <ForumContainer breadcrumbs={breadcrumbs} user={user}>
            <ViewingUserContext.Provider value={{ viewingUser: user }}>
                <div className='user-info-container'>
                    <div className='user-avatar-container'>
                        { user && <img className="user-avatar" src={avatar} alt='Avatar' /> }
                    </div>
                    <div className="user-info-block">
                        <DisplayUser 
                            user={user}
                            fontSize={'1.5em'}
                            width={20}
                            height={20}
                            useUserTitle={true}
                            userTitleStyle={{
                                margin: '0'
                            }}
                        />
                        <div className="user-info-stats">
                            <SpanIcon icon="fa-clipboard" className="small white">
                                {' Posts: '+user.postCount}
                            </SpanIcon>
                            <SpanIcon icon="fa-thumbs-up" className="small white">
                                {' Threads Created: '+user.threadsCreated}
                            </SpanIcon>
                            <SpanIcon icon="fa-thumbs-up" className="small white">
                                {' Thanks Given: '+user.thanksGiven}
                            </SpanIcon>
                            <SpanIcon icon="fa-thumbs-up" className="small white">
                                {' Thanks Received: '+user.thanksReceived}
                            </SpanIcon>
                            <SpanIcon icon="fa-thumbs-up" className="small white">
                                {' In-game Total: '+(user.totalLevel === -1 ? 'N/A' : user.totalLevel)}
                            </SpanIcon>
                        </div>
                    </div>
                </div>
                <div className='user-widget-btns' style={{gridTemplateColumns: `repeat(${buttons.length}, auto)`}}>
                    { buttons.map((button, index) => (
                        <Button
                            title={button.title}
                            key={index}
                            className={index === activeButton ? 'active-btn' : ''}
                            onClick={() => setActiveButton(index)}
                        />
                    ))}
                </div>
                <CollapsibleWidget
                    title={widgetTitle}
                    description={widgetDescription}
                >
                    { widgetContent }
                </CollapsibleWidget>
            </ViewingUserContext.Provider>
        </ForumContainer>
    )
}
