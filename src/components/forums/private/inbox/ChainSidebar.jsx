import React from 'react';

import { formatDate } from '../../../../utils/format';

import Widget from '../../../utils/Widget';
import DisplayUser from '../../../utils/user/DisplayUser';

import './ChainSidebar.css';

export default function ChainSidebar({ chain, messages }) {
    let lastMessage = messages.sort((a, b) => a.post.createdAt - b.post.createdAt)[0];
    return (
        <Widget
            title='Chain Stats'
            description='View stats about this chain.'
            className='chain-stats-widget'
        >
            <div className='chain-stats-title'>
                Users:
                <span className='chain-stats-value'>
                    { chain.recipients.map((user, index) =>
                        <DisplayUser 
                            key={index} 
                            user={user}
                            width={15}
                            height={15}
                            fontSize={15}
                        />
                    )}
                </span>
                <span className='chain-stats-value fa fa-plus-circle' />
            </div>
            <div className='chain-stats-title'>Messages: { messages.length }</div>
            { lastMessage && 
                <div className='chain-stats-title'>Last Message: 
                    <span>{ formatDate(lastMessage.createdAt) }</span>
                </div> 
            }
        </Widget>
    )
}
