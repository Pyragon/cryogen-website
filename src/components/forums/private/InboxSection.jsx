import React, { useState } from 'react';
import { useEffect } from 'react';

import axios from '../../../utils/axios';

export default function InboxSection() {
    let [ messages, setMessages ] = useState([]);
    useEffect(() => {
        axios.get('http://localhost:8081/forums/private/inbox')
            .then(res => setMessages(res.data))
            .catch(console.error);
    }, [ messages ]);
    return (
        <div style={{height: '500px'}}>
            { messages.length > 0 && messages.map(message => <span key={message._id}>{message.subject}</span>)}
        </div>
    );
}
