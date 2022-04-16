import React from 'react';
import DisplayUser from '../../../components/utils/user/DisplayUser';
import { formatDate } from '../../../utils/format';

export default function VisitorMessage({ message }) {
    return (
        <div className='visitor-message'>
            <div className='visitor-message-header'>
                Left by
                <DisplayUser
                    user={message.author}
                />
                <span>{formatDate(message.createdAt)}</span>
            </div>
            <div className='visitor-message-body'>
                { message.content }
            </div>
        </div>
    )
}
