import React, { useState, useEffect } from 'react';
import axios from 'axios';

import NewsPost from '../../utils/NewsPost';
import Input from '../../utils/Input';
import Button from '../../utils/Button';

import '../../../styles/forums/Chatbox.css';
import ChatboxMessage from './ChatboxMessage';

async function fetchMessages(messages, setMessages) {
    let results = await axios.get('http://localhost:8081/forums/chatbox');
    let newMessages = results.data;
    if(JSON.stringify(messages) === JSON.stringify(newMessages.messages))
        return;
    setMessages(newMessages.messages);
    //scroll to bottom if already there, if not, leave it alone
}

export default function Chatbox() {
    let [ messages, setMessages ] = useState([]);
    useEffect(() => {
        fetchMessages(messages, setMessages);
        let interval = setInterval(() => fetchMessages(messages, setMessages), 1000);
        return () => clearInterval(interval);
    }, []);
    let loggedIn = true;
    let [ text, setText ] = useState('');
    let submitMessage = async() => {
        let results = await axios.post('http://localhost:8081/forums/chatbox', {
            author: 'cody',
            message: text
        });
        if(!results || results.message) {
            console.error(results.message || 'Error submitting message');
            return;
        }
        setText('');
        fetchMessages(messages, setMessages);
    };
    return (
        <NewsPost
            title="Chatbox"
            description="Chat with other users in real-time"
            className="grid-span-3"
            collapsed={true}
            style={{ marginBottom: '0px'}}>
                <div className="message-container">
                    { messages.map((data, index) => 
                        <ChatboxMessage 
                            key={index} 
                            index={data._id}
                            author={data.author}
                            message={data.message}
                            time={data.createdAt}
                    />) }
                </div> 
                { loggedIn && <div className="input-container">
                    <Input style={{ marginLeft: '0px' }} value={text} className="chat-input" type="text" placeholder="Type a message..." setState={setText} onEnter={submitMessage}/>
                    <Button className="chat-button" title="Send" onClick={submitMessage}/>
                </div> }
        </NewsPost>
    )
};
