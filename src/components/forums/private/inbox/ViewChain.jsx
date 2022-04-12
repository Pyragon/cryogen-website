import React, { useContext, useEffect, useState, useMemo } from 'react';
import axios from '../../../../utils/axios';

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

async function clickedReply(sendErrorNotification, chain, reply, setReply, setMessages) {
    if(reply.length < 5 || reply.length > 2000) {
        sendErrorNotification('Reply must be between 5 and 2000 characters.');
        return;
    }
    let res = await axios.post('/forums/private/message', {
        chain: chain._id,
        content: reply,
    });
    if(res.data.error) {
        sendErrorNotification(res.data.error);
        return;
    }
    setReply('');
    console.log(res.data);
    setMessages(messages => [ ...messages, toPost({ message: res.data.message, thanks: res.data.thanks }) ]);
}

function toPost({ message, thanks }) {
    return {
        post: {
            ...message,
            isMessage: true,
        },
        thanks,
        postCount: 0,
        thanksReceived: 0,
        thanksGiven: 0,
        isMessage: true
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

    useEffect(() => {
        setSectionTitle('Inbox - Viewing Chain');
        setSectionDescription('Viewing message chain: ' + chain.subject+' (#'+chain._id+')');
        return () => setSectionSidebar(null);
    }, []);

    useEffect(() => {
        let loadMessages = async() => {
            let res = await axios.get('/forums/private/message/chain/'+chain._id+'/'+page);
            if(res.data.error) {
                console.error(res.data.error);
                return;
            }
            if(chain.notifyUsersWarning.some(notifyUser => notifyUser._id == user._id)) {
                console.log('removing user from notify');
                setChains(chains => chains.map(chain => chain._id == chain._id ? { ...chain, notifyUsersWarning: chain.notifyUsersWarning.filter(notifyUser => notifyUser._id != user._id) } : chain));
            }
            setPageTotal(res.data.pageTotal);
            setMessages(res.data.messages.map(toPost));
        };
        loadMessages();
    }, [ page ]);
    useEffect(() => setSectionSidebar(<ChainSidebar chain={chain} messages={messages} />), [ messages ]);
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
                        <Button title="Reply" onClick={async() => await clickedReply(sendErrorNotification, chain, reply, setReply, setMessages)}/>
                    </div>
                </div>
            </EditorContext.Provider>
        </>
    )
}
