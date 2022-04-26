import React, { useState, useEffect, useContext } from 'react';
import axios from '../../../utils/axios';
import { validate } from '../../../utils/validate';

import CollapsibleWidget from '../../utils/CollapsibleWidget';
import Input from '../../utils/Input';
import Button from '../../utils/Button';

import '../../../styles/forums/Chatbox.css';
import ChatboxMessage from './ChatboxMessage';
import NotificationContext from '../../../utils/contexts/NotificationContext';
import UserContext from '../../../utils/contexts/UserContext';

export default function Chatbox() {
    let [ messages, setMessages ] = useState([]);
    let [ message, setMessage ] = useState('');
    let { user } = useContext(UserContext);

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let loadMessages = async () => {

            try {

                let res = await axios.get('/forums/chatbox');

                setMessages(res.data.messages);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        loadMessages();

        let interval = setInterval(loadMessages, 5000);
        return () => clearInterval(interval);

    }, [ messages ]);

    let submitMessage = async() => {

        let validateOptions = {
            message: {
                required: true,
                type: 'string',
                name: 'Message',
                min: 4,
                max: 200
            }
        };

        let [ validated, error ] = validate(validateOptions, { message });
        if(!validated) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.post('/forums/chatbox', { message });

            setMessage('');
            setMessages(messages => [ ...messages, res.data.message ]);

        } catch(error) {
            sendErrorNotification(error);
        }
    };

    return (
        <CollapsibleWidget
            title="Chatbox"
            description="Chat with other users in real-time"
            className="grid-span-3"
            collapsed={true}
            style={{ marginBottom: '0px'}}>
                <div className="message-container">
                    { messages.map(message => 
                        <ChatboxMessage 
                            key={message._id} 
                            message={message}
                        />
                    ) }
                </div> 
                { user && <div className="input-container">
                    <Input style={{ marginLeft: '0px' }} value={message} className="chat-input" placeholder="Type a message..." setState={setMessage} onEnter={submitMessage}/>
                    <Button className="chat-button" title="Send" onClick={submitMessage}/>
                </div> }
        </CollapsibleWidget>
    )
};
