import React, { useState, useEffect, useContext, useMemo, useRef } from 'react';
import axios from '../../../utils/axios';
import { sendNotification, sendErrorNotification } from '../../../utils/notifications';

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
import Dropdown from '../../utils/Dropdown';

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
        sendErrorNotification(err);
    }
}

function pinThread(thread, user, setThread) {
    if(!user) return false;
    if(!thread.permissions.canModerate(user)) return true;
    axios.post(`/forums/threads/${thread._id}/pin`)
        .then(res => {
            if(res.data.message) {
                console.error(res.data.message);
                return;
            }
            let thread = res.data.thread;
            sendNotification({
                text: 'Thread has been ' + (!thread.pinned ? 'unpinned' : 'pinned') + '.',
            });
            setThread(thread);
        }).catch(sendErrorNotification);
}

function lockThread(thread, user, setThread) {
    if(!user) return;
    if(!thread.permissions.canModerate(user)) return;
    axios.post(`/forums/threads/${thread._id}/lock`)
        .then(res => {
            if(res.data.message) {
                console.error(res.data.message);
                return;
            }
            let thread = res.data.thread;
            sendNotification({
                text: 'Thread has been ' + (thread.open ? 'unlocked' : 'locked') + '.',
            });
            setThread(thread);
        }).catch(sendErrorNotification);
}

function deleteThread(thread, user, setThread) {
    if(!user) return;
    if(!thread.permissions.canModerate(user)) return;
    sendNotification({
        text: 'Are you sure?',
        layout: 'center',
        buttons: [
            {
                addClass: 'btn btn-success',
                text: 'Yes',
                onClick: (noty) => {
                    axios.post(`/forums/threads/${thread._id}/archive`)
                        .then(res => {
                            if(res.data.message) {
                                console.error(res.data.message);
                                return;
                            }
                            noty.close();
                            sendNotification({ text: 'Thread has been ' + (res.data.thread.archived ? 'deleted' : 'restored') + '.' });
                            setThread(res.data.thread);
                        }).catch(sendErrorNotification);
                },
            },
            {
                addClass: 'btn btn-danger',
                text: 'Cancel',
                onClick: (noty) => {
                    noty.close();
                },
            }
        ],
    });
}

function canReply(user, thread) {
    if(!user) return false;
    if(thread.permissions.canModerate(user)) return true;
    if(!thread.open) return false;
    return thread.permissions.canReply(user, thread);
}

export default function ViewThread({ thread, setThread }) {
    let [ posts, setPosts ] = useState([]);
    let [ reply, setReply ] = useState('');
    let [ options ] = useState([
            {
                title: 'Pin Thread',
                icon: 'fas fa-thumbtack',
                onClick: () => pinThread(thread, user, setThread),
            },
            {
                title: 'Lock Thread',
                icon: 'fas fa-lock',
                onClick: () => lockThread(thread, user, setThread),
            },
            {
                title: 'Delete Thread',
                icon: 'fas fa-trash-alt',
                onClick: () => deleteThread(thread, user, setThread),
            },
            {
                title: 'Move Thread',
                icon: 'fas fa-arrows-alt',
                onClick: () => {
                }
            },
        ]);
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
        options[0].title = thread.pinned ? 'Unpin Thread' : 'Pin Thread';
        options[1].title = thread.open ? 'Lock Thread' : 'Unlock Thread';
        options[1].icon = thread.open ? 'fas fa-lock' : 'fas fa-lock-open';
        options[2].title = thread.archived ? 'Restore Thread' : 'Delete Thread';
        options[2].icon = thread.archived ? 'fas fa-trash-restore' : 'fas fa-trash-alt';
    }, [ user, thread, page ]);
    let providerValue = useMemo(() => ({ reply, setReply }), [ reply, setReply ]);
    return (
        <div style={{position: 'relative'}}>
            { thread.permissions && thread.permissions.canModerate(user) &&
                <Dropdown
                    title='Moderator Options'
                    className='moderate-thread-btn'
                    options={options}
                />
            }
            <EditorContext.Provider value={providerValue}>
                <CollapsibleWidget
                    title={thread.title}
                    minimizable={false}
                    ref={scrollRef}
                >
                    { posts && <PostList posts={posts} setPosts={setPosts} /> }
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
