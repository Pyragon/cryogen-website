import React, { useState, useEffect, useContext, useMemo } from 'react';

import UserContext from '../../../utils/UserContext';
import setUserActivity from '../../../utils/user-activity';
import axios from '../../../utils/axios';
import CollapsibleWidget from '../../utils/CollapsibleWidget';
import RichTextEditor from '../../utils/editor/RichTextEditor';

import EditorContext from '../../../utils/EditorContext';

import Button from '../../utils/Button';

import PostList from './PostList';

async function clickedReply(thread, reply, setReply, setPosts) {
    let link = '/forums/posts/';
    try {
        let results = await axios.post(link, { threadId: thread._id, content: reply });
        results = results.data;
        if(results.message) {
            console.error(results.message);
            return;
        }
        setReply('');
        setPosts(prev => [...prev, results.post]);
    } catch(err) {
        console.error(err.message || err);
    }
}

export default function ViewThread({ thread }) {
    let [ posts, setPosts ] = useState([]);
    let [ reply, setReply ] = useState('');
    let { user } = useContext(UserContext);
    let loggedIn = user !== null;
    useEffect(() => {
        fetch('http://localhost:8081/forums/posts/children/'+thread._id)
        .then(res => res.json())
        .then(data => setPosts(data));
        setUserActivity(user, 'Viewing thread: ' + thread.title);
    }, []);
    let providerValue = useMemo(() => ({ reply, setReply }), [ reply, setReply ]);
    return (
        <>
            <EditorContext.Provider value={providerValue}>
                <CollapsibleWidget
                    title={thread.title}
                    minimizable={false}
                >
                    { posts && <PostList posts={posts} /> }
                </CollapsibleWidget>
                { loggedIn && thread.open && 
                    <CollapsibleWidget
                        title="Quick Reply"
                        description={ 
                            <>
                                <span>Add a reply to this thread. Click </span>
                                <a href="" className="link">here</a>
                                <span> for BBCode examples</span>
                            </>
                        }
                    >
                        <RichTextEditor value={reply} setState={setReply}/>
                        <Button className="reply-btn" title="Reply" onClick={async() => await clickedReply(thread, reply, setReply, setPosts)}/>
                        <div style={{clear: 'both' }}/>
                    </CollapsibleWidget> 
                }
            </EditorContext.Provider>
        </>
    )
}
