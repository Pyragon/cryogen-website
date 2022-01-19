import React, { useState, useEffect, useContext, useMemo, useRef } from 'react';
import axios from '../../../utils/axios';

import Permissions from '../../../utils/permissions';

import UserContext from '../../../utils/contexts/UserContext';
import setUserActivity from '../../../utils/user-activity';
import CollapsibleWidget from '../../utils/CollapsibleWidget';
import RichTextEditor from '../../utils/editor/RichTextEditor';

import EditorContext from '../../../utils/contexts/EditorContext';
import PageContext from '../../../utils/contexts/PageContext';

import Button from '../../utils/Button';

import PostList from './PostList';
import Pages from './Pages';

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
        setPosts(prev => [...prev, results]);
    } catch(err) {
        console.error(err.message || err);
    }
}

function canReply(user, thread) {
    if(!user) return false;
    if(thread.permissions.canModerate(user)) return true;
    if(!thread.open) return false;
    return thread.permissions.canReply(user, thread);
}

export default function ViewThread({ thread }) {
    let [ posts, setPosts ] = useState([]);
    let [ reply, setReply ] = useState('');
    let scrollRef = useRef(null);
    let { user } = useContext(UserContext);
    let { page } = useContext(PageContext);
    if(thread.subforum.permissions)
        thread.permissions = new Permissions(thread.subforum.permissions);

    function scrollToTop() {
        scrollRef.current.scrollIntoView({ behavior: 'smooth' });
    }

    useEffect(() => {
        async function fetchData() {
            let response = await axios.get('http://localhost:8081/forums/posts/children/'+thread._id+'/'+page);
            if(!response.data) {
                console.error('No posts found for thread '+thread._id);
                return;
            }
            let posts = response.data.map(post => {
                if(!post.permissions) return post;
                post.permissions = new Permissions(post.permissions);
                return post;
            });

            setPosts(posts);
            setUserActivity(user, 'Viewing thread: ' + thread.title, 'thread', thread._id);
        }
        fetchData();
    }, [ user, thread, page ]);
    let providerValue = useMemo(() => ({ reply, setReply }), [ reply, setReply ]);

    return (
        <div style={{position: 'relative'}}>
            { thread.permissions && thread.permissions.canModerate(user) &&
                <>
                    <Button className="moderate-thread-btn small" onClick={() => console.log('open options')}>
                        <span>Moderation Options </span>
                        <i className="fas fa-chevron-down"></i>
                    </Button>
                    <div style={{clear: 'both' }}/>
                </>
            }
            <EditorContext.Provider value={providerValue}>
                <CollapsibleWidget
                    title={thread.title}
                    minimizable={false}
                    ref={scrollRef}
                >
                    { posts && <PostList posts={posts} /> }
                </CollapsibleWidget>
                <Pages 
                    thread={thread}
                    scroll={scrollToTop}
                />
                { canReply(user, thread) && 
                    <CollapsibleWidget
                        title="Reply"
                        description={ 
                            <>
                                <span>Add a reply to this thread. Click </span>
                                <a href="/forums" className="link">here</a>
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
        </div>
    )
}
