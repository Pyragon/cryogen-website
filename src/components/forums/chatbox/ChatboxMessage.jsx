import React from 'react';
import { formatDate } from '../../../utils/format';
import DisplayUser from '../../utils/user/DisplayUser';

export default function ChatboxMessage({ author, time, message, index }) {
    return (
        <div key={index} className="chatbox-message">
            <span className="chatbox-time">{'[ '+formatDate(time, 'MMM Do, h:mma')+ ' ] '}</span>
            <span className="chatbox-author">
                <DisplayUser user={author} suffix=": "/>
            </span>
            <span className="chatbox-text white">{message}</span>
        </div>
    )
}
