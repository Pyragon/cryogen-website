import React, { useState, useContext } from 'react';
import { formatDate } from '../../../utils/format';
import axios from '../../../utils/axios';

import NotificationContext from '../../../utils/contexts/NotificationContext';

import DisplayUser from '../../../components/utils/user/DisplayUser';
import UserContext from '../../../utils/contexts/UserContext';
import ViewingUserContext from '../../../utils/contexts/ViewingUserContext';

export default function VisitorMessage({ message, setMessages }) {
    let { user } = useContext(UserContext);
    let { viewingUser } = useContext(ViewingUserContext);
    let [ showDelete, setShowDelete ] = useState(false);
    let [ shownFromHover, setShownFromHover ] = useState(false);

    let { sendNotification, sendErrorNotification, sendConfirmation } = useContext(NotificationContext);

    let onMouseEnter = (e) => {
        if(!user || (message.author._id !== user._id && user._id !== viewingUser._id)) return;
        if(showDelete) return false;
        setShownFromHover(true);
        setShowDelete(true);
    };

    let onMouseOut = (e) => {
        if(!user || (message.author._id !== user._id && user._id !== viewingUser._id)) return;
        if(!shownFromHover) return false;
        setShowDelete(false);
    };

    let deleteMessage = message => {
        sendConfirmation(
            { 
                text: 'Are you sure you wish to delete this message?', 
                onSuccess: async(close) => {
                    try {
                        
                        await axios.delete(`/users/messages/${message._id}`);
            
                        setMessages(messages => messages.filter(m => m._id !== message._id));
                        sendNotification({ text: 'Message successfully deleted.' });
                        close();
            
                    } catch(error) {
                        sendErrorNotification(error);
                    }
                }
            }
        );
    };
    return (
        <div className='visitor-message' 
            onClick={() => {
                if(!user || (message.author._id !== user._id && user._id !== viewingUser._id)) return;
                if(!showDelete) {
                    setShowDelete(true);
                    setShownFromHover(false);
                } else if(!shownFromHover)
                    setShowDelete(false);
                else
                    setShownFromHover(false);
            }}
            onMouseEnter={onMouseEnter}
            onMouseLeave={onMouseOut}
        >
            <div className='visitor-message-header'>
                Left by
                <DisplayUser
                    user={message.author}
                />
                <span>{formatDate(message.createdAt)}</span>
            </div>
            <div className='visitor-message-body'>
                { message.content }
                { showDelete && <i className='visitor-message-delete red fas fa-times' onClick={() => deleteMessage(message)} /> }
            </div>
        </div>
    )
}
