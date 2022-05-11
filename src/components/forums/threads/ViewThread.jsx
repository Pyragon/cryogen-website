import React, { useState, useEffect, useContext, useMemo, useRef } from 'react';
import axios from '../../../utils/axios';
import { validate, validatePost } from '../../../utils/validate';
import NotificationContext from '../../../utils/contexts/NotificationContext';

import Permissions from '../../../utils/permissions';

import UserContext from '../../../utils/contexts/UserContext';
import setUserActivity from '../../../utils/user-activity';
import CollapsibleWidget from '../../utils/CollapsibleWidget';
import RichTextEditor from '../../utils/editor/RichTextEditor';

import EditorContext from '../../../utils/contexts/EditorContext';
import PageContext from '../../../utils/contexts/PageContext';

import Button from '../../utils/Button';

import PostList from './PostList';
import Pages from '../../utils/Pages';
import Dropdown from '../../utils/Dropdown';
import '../../../styles/forums/Thread.css';

function canReply(user, thread) {
    if(!user) return false;
    if(thread.permissions.canModerate(user)) return true;
    if(!thread.open) return false;
    return thread.permissions.canReply(user, thread);
}

export default function ViewThread({ thread, setThread }) {
    let [ posts, setPosts ] = useState([]);
    let [ reply, setReply ] = useState('');
    let scrollRef = useRef(null);

    let { user } = useContext(UserContext);
    let { page } = useContext(PageContext);
    let { sendErrorNotification, sendConfirmation } = useContext(NotificationContext);

    let pin = async () => {

        try {

            let res = await axios.post(`/forums/threads/${thread._id}/pin`);

            setThread(res.data.thread);

        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let lock = async () => {

        try {

            let res = await axios.post(`/forums/threads/${thread._id}/lock`);

            setThread(res.data.thread);

        } catch(error) {
            sendErrorNotification(error);
        }

    };

    let deleteThread = async () => {
        if(!thread.permissions.canModerate(user)) {
            sendErrorNotification('You do not have permission to delete this thread.');
            return;
        }

        let onSuccess = async () => {

            try {
                let res = await axios.delete(`/forums/threads/${thread._id}`);

                setThread(res.data.thread);

                sendErrorNotification(`Thread has been ${res.data.thread.archived ? 'deleted' : 'restored'}.`);
            } catch(error) {
                sendErrorNotification(error);
            }
        }

        sendConfirmation('Are you sure you wish to delete this thread?', onSuccess);
    };

    let replyToThread = async () => {

        let validateOptions = {
            content: validatePost,
        }; 

        let [ validated, error ] = validate(validateOptions, { content: reply });
        if(!validated) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.post('/forums/posts', { threadId: thread._id, content: reply });

            setReply('');
            setPosts(prev => [...prev, res.data.post]);

        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let [ options ] = useState([
            {
                title: 'Pin Thread',
                icon: 'fas fa-thumbtack',
                onClick: pin,
            },
            {
                title: 'Lock Thread',
                icon: 'fas fa-lock',
                onClick: lock,
            },
            {
                title: 'Delete Thread',
                icon: 'fas fa-trash-alt',
                onClick: deleteThread,
            },
            {
                title: 'Move Thread',
                icon: 'fas fa-arrows-alt',
                onClick: () => {
                }
            },
        ]);
    if(thread.subforum.permissions)
        thread.permissions = new Permissions(thread.subforum.permissions);

    function scrollToTop() {
        scrollRef.current.scrollIntoView({ behavior: 'smooth' });
    }
    useEffect(() => {

        let load = async () => {

            try {

                let res = await axios.get(`/forums/threads/${thread._id}/posts/${page}`);

                let posts = res.data.posts;

                posts = posts.map(post => {
                    if(!post.permissions) return post;
                    post.permissions = new Permissions(post.permissions);
                    return post;
                });

                setPosts(posts);
                setUserActivity(user, 'Viewing thread: ' + thread.title, 'thread', thread._id);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        load();

        options[0].title = thread.pinned ? 'Unpin Thread' : 'Pin Thread';
        options[1].title = thread.open ? 'Lock Thread' : 'Unlock Thread';
        options[1].icon = thread.open ? 'fas fa-lock' : 'fas fa-lock-open';
        options[2].title = thread.archived ? 'Restore Thread' : 'Delete Thread';
        options[2].icon = thread.archived ? 'fas fa-trash-restore' : 'fas fa-trash-alt';

    }, [ user, thread, page ]);
    let providerValue = useMemo(() => ({ reply, setReply }), [ reply, setReply ]);
    return (
        <div>
            { thread.permissions && thread.permissions.canModerate(user) &&
                <>
                    <div className='moderation-btn-container'>
                        <Dropdown
                            title='Moderator Options'
                            className='moderate-thread-btn'
                            options={options}
                            useGrid={true}
                        />
                    </div>
                </>
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
                    pageTotal={thread.pageTotal}
                    base={`/forums/thread/${thread.id}`}
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
                        <RichTextEditor value={reply} setState={setReply} />
                        <Button className="reply-btn" title="Reply" onClick={replyToThread}/>
                        <div style={{clear: 'both' }}/>
                    </CollapsibleWidget> 
                }
            </EditorContext.Provider>
        </div>
    )
}
