import React, { useState, useContext } from 'react';
import { formatDate } from '../../../../utils/format';
import axios from '../../../../utils/axios';

import UserContext from '../../../../utils/contexts/UserContext';
import EditorContext from '../../../../utils/contexts/EditorContext';

import Post from '../../posts/Post';
import DisplayUser from '../../../utils/user/DisplayUser';

async function clickedThanks(add, message, setThanks) {
    let link = '/forums/private/message/' + message._id + '/thanks';
    if(!add)
        link += '/remove';
    try {
        let results = await axios.post(link);
        results = results.data;
        if(results.message) {
            console.error(results.message);
            return;
        }
        setThanks(results.thanks);
    } catch(err) {
        console.error(err.message || err);
    }
}

function clickedQuote(message, setReply) {
    let reply = `[quote="${message._id}"]${message.content}[/quote]`;
    setReply((prev) => prev + (prev ? '\n' : '') + reply);
}

export default function ViewMessageBlock({ message }) {
    let [ thanks, setThanks ] = useState(message.thanks);
    let { user } = useContext(UserContext);
    let { setReply } = useContext(EditorContext);

    return (
        <div key={message._id} className="post-content-block">
            <div className="post-date-block">
                <div className="post-date small">{formatDate(message.createdAt)}</div>
                <div className="post-id small link">{'#'+message._id}</div>
            </div>
            <div className="post-message-block">
                    <Post post={message} />
                    <div className="edit-options"> 
                        { thanks.find(thank => thank.user._id === user._id) ? 
                            <div className="link edit-option" onClick={() => clickedThanks(false, message, setThanks)}>Remove Thanks</div> : 
                            <div className="link edit-option" onClick={() => clickedThanks(true, message, setThanks)}>Thanks</div> 
                        }
                        { <div className="quote-post link edit-option" onClick={() => clickedQuote(message, setReply)}>Quote</div> }
                     </div>
            </div>
            <div className="post-thanks-block small">
                <p style={{margin: '0'}}>
                    { thanks.length === 0 && <span>No users have thanked this post.</span>}
                    { thanks && thanks.length > 0 && 
                        <>
                            <span>{thanks.length + ' user'+(thanks.length === 1 ? '' : 's')+' '+(thanks.length === 1 ? 'has' : 'have')+' thanked this post:'}</span>
                            { thanks.slice(0, 10).map((thank, index) =>
                                <DisplayUser
                                    key={index}
                                    user={thank.user}
                                    suffix={index === thanks.length-1 ? '' : ', '}
                                />
                            )}
                            { thanks.length > 10 && <span>...</span> }

                        </>
                    }
                </p>
            </div>
        </div>
    )
}
