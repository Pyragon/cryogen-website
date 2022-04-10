import React from 'react';
import DisplayUser from '../../utils/user/DisplayUser';

export default function InboxMessage({ message }) {
    return (
        <div>
            <p className='message-author'>
                <DisplayUser user={message.author} />
            </p>
            <p className='message-subject'>Subject: {message.subject}</p>
            <p className='message-body'>{message.body}</p>
        </div>
    );
}
