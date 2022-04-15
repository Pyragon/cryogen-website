import React from 'react';

import { formatDate } from '../../../utils/format';

import DisplayUser from '../../utils/user/DisplayUser';

export default function Quote({ quote }) {
    return (
        <div className="quote">
            <div className="quote-header">
                { !quote && <span className='red'>Error getting post.</span> }
                { quote && 
                    <>
                        Quote By 
                        <span>
                            <DisplayUser user={quote.author} useATag={true} />
                        </span>
                        <span>on</span>
                        <span>
                            <a href={`/forums/threads/${quote.thread._id}`} className='link'>{quote.thread.title},</a>
                        </span>
                        <span style={{fontSize: '.8rem'}}>
                            { formatDate(quote.createdAt, 'MMMM Do, YYYY [at] h:mm a') }
                        </span>
                    </>
                }
            </div>
            { quote && <div className="quote-content">{quote.content}</div> }
        </div>
    )
}
