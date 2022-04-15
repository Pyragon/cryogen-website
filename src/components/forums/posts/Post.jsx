import React, { useContext, useState, useEffect } from 'react';
import ReactDOMServer from 'react-dom/server';

import axios from '../../../utils/axios';

import NotificationContext from '../../../utils/contexts/NotificationContext';
import UserContext from '../../../utils/contexts/UserContext';

import DisplayUser from '../../utils/user/DisplayUser';
import Quote from './Quote';

let quoteRegex = '\\[quote="([a-z0-9]+)"\\]';

export default function Post({ post }) {
    let { user } = useContext(UserContext);

    let [ formatted, setFormatted ] = useState(post.formatted);

    let { sendErrorNotification } = useContext(NotificationContext);
    let style = post.style || {};

    useEffect(() => {

        setFormatted(formatted.replaceAll('[username]', !user ? 'Guest' : ReactDOMServer.renderToString(<DisplayUser user={user} useATag={true} />)));
    
        let regexp = new RegExp(quoteRegex);
        let match;
        while((match = regexp.exec(formatted))) {
            let quoteId = match[1];
    
            let loadPost = async(quoteId) => {

                if(post._id === quoteId) return null;
    
                let res = await axios.get('/forums/posts/'+quoteId);
                if(res.data.error) {
                    sendErrorNotification(res.data.error);
                    return null;
                }
    
                return res.data.post;
    
            };
    
            loadPost(quoteId).then(quote => {
                setFormatted(formatted => formatted.replace(regexp, ReactDOMServer.renderToString(<Quote quote={quote} />)));
            });
            break;
        }
    }, [ post ]);

    return (
        <div key={post._id}>
            <div className="post" style={style} dangerouslySetInnerHTML={{ __html: formatted }}>
            </div>
        </div>
    )
}
