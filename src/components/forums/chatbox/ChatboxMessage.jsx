import React from 'react';
import { formatDate } from '../../../utils/format';
import DisplayUser from '../../utils/user/DisplayUser';

export default function ChatboxMessage({ message }) {
    return (
        <div key={message._id} className="chatbox-message">
            <span className="chatbox-time">{'[ '+formatDate(message.createdAt, 'MMM Do, h:mma')+ ' ] '}</span>
            <span className="chatbox-author">
                <DisplayUser user={message.author} suffix=": "/>
            </span>
            <span className="chatbox-text white">{message.content}</span>
        </div>
    )
}
