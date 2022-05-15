import React, { useContext, useEffect, useState, useMemo } from 'react';
import axios from '../../../../utils/axios';
import { validate } from '../../../../utils/validate';

import EditorContext from '../../../../utils/contexts/EditorContext';
import SectionContext from '../../../../utils/contexts/SectionContext';
import NotificationContext from '../../../../utils/contexts/NotificationContext';
import PageContext from '../../../../utils/contexts/PageContext';

import RichTextEditor from '../../../utils/editor/RichTextEditor';
import Button from '../../../utils/Button';
import PostList from '../../threads/PostList';
import Pages from '../../../utils/Pages';

import './ViewChain.css';
import ChainSidebar from './ChainSidebar';
import UserContext from '../../../../utils/contexts/UserContext';

function toPost({ message, thanks }) {
    return {
        ...message,
        isMessage: true,
        thanks,
        postCount: 0,
        thanksReceived: 0,
        thanksGiven: 0
    }
}

export default function ViewChain({ chain, setViewingChain, setChains }) {
    let { setSectionTitle, setSectionDescription, setSectionSidebar } = useContext(SectionContext);
    let { sendErrorNotification } = useContext(NotificationContext);
    let [ messages, setMessages ] = useState([]);
    let [ pageTotal, setPageTotal ] = useState(1);
    let [ reply, setReply ] = useState('');
    let { page } = useContext(PageContext);
    let { user } = useContext(UserContext);
    
    let providerValue = useMemo(() => ({ reply, setReply }), [ reply, setReply ]);

    useEffect(() => setSectionSidebar(<ChainSidebar chain={chain} messages={messages} />), [ messages ]);

    useEffect(() => {
        setSectionTitle('Inbox - Viewing Chain');
        setSectionDescription('Viewing message chain: ' + chain.subject+' (#'+chain._id+')');
        return () => setSectionSidebar(null);
    }, []);

    useEffect(() => {
        let loadMessages = async() => {

            try {

                let res = await axios.get(`/forums/private/message/chain/${chain._id}/${page}`);

                if(chain.notifyUsersWarning.some(notifyUser => notifyUser._id === user._id))
                    setChains(chains => chains.map(c => c._id === chain._id ? { ...chain, notifyUsersWarning: chain.notifyUsersWarning.filter(notifyUser => notifyUser && notifyUser._id !== user._id) } : c));

                setMessages(res.data.messages.map(toPost));
                setPageTotal(res.data.pageTotal);

            } catch(error) {
                sendErrorNotification(error);
            }
        };
        loadMessages();
    }, [ page ]);
    
    let replyToPost = async () => {
        let validateOptions = {
            reply: {
                type: 'string',
                required: true,
                name: 'Message',
                min: 5,
                max: 2000,
            }
        };

        let error = validate(validateOptions, { reply });
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.post('/forums/private/message', {
                chain: chain._id,
                content: reply,
            });

            setReply('');
            setMessages(messages => [ ...messages, toPost({ message: res.data.message, thanks: res.data.thanks }) ]);
        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let goBack = () => {
        setViewingChain(null);
        setSectionTitle(null);
        setSectionDescription(null);
    };
    return (
        <>
            <div className="go-back-button" onClick={goBack}>
                <span style={{marginRight: '5px'}} className='fas fa-arrow-left' />
                {'Click here to go back'}
            </div>
            <h3 style={{textAlign: 'center'}}>{chain.subject}</h3>
            <EditorContext.Provider value={providerValue}>
                <div className="view-message-container">
                    <PostList posts={messages} />
                </div>
                <div className='chain-pages'>
                    <Pages 
                        pageTotal={pageTotal}
                        base={`/forums/private/message/chain/${chain._id}`}
                    />
                </div>
                <div className='view-chain-reply-container'>
                    <RichTextEditor value={reply} setState={setReply}/>
                    <div className='view-chain-reply-btn-container'>
                        <Button title="Reply" onClick={replyToPost}/>
                    </div>
                </div>
            </EditorContext.Provider>
        </>
    )
}
