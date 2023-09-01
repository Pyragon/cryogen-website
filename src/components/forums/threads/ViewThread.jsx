import React, { useState, useEffect, useContext, useMemo, useRef } from 'react';
import axios from '../../../utils/axios';
import { validate, validatePost } from '../../../utils/validate';
import { formatDate } from '../../../utils/format';
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
import LabelInput from '../../utils/LabelInput';
import DisplayUser from '../../utils/user/DisplayUser';

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
    let threadTitle = thread.title;
    
    let scrollRef = useRef(null);

    let { user } = useContext(UserContext);
    let { page } = useContext(PageContext);
    let { sendErrorNotification, sendNotification, sendConfirmation, openModal, closeModal } = useContext(NotificationContext);

    let pin = async () => {

        try {

            let res = await axios.post(`/forums/threads/${thread._id}/pin`);

            setThread(res.data.thread);

            sendNotification({ text: `Thread has been ${res.data.thread.pinned ? 'pinned' : 'unpinned'}.`});

        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let lock = async () => {

        try {

            let res = await axios.post(`/forums/threads/${thread._id}/lock`);

            setThread(res.data.thread);

            sendNotification({ text: `Thread has been ${res.data.thread.open ? 'unlocked' : 'locked'}.`});

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
                let res = await axios.post(`/forums/threads/${thread._id}/archive`);

                setThread(res.data.thread);
                closeModal();

                sendErrorNotification(`Thread has been ${res.data.thread.archived ? 'deleted' : 'restored'}.`);
            } catch(error) {
                sendErrorNotification(error);
            }
        }

        sendConfirmation({ text: 'Are you sure you wish to delete this thread?', onSuccess});
    };

    let replyToThread = async () => {

        let validateOptions = {
            content: validatePost,
        }; 

        let error = validate(validateOptions, { content: reply });
        if(error) {
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

    let renameThread = async () => {

        let success = async () => {

            console.log('renaming');

            let validateOptions = {
                title: {
                    required: true,
                    name: 'Title',
                    min: 5,
                    max: 50,
                }
            };
    
            let error = validate(validateOptions, { title: threadTitle });
            if(error) {
                sendErrorNotification(error);
                return;
            }

            try {
                
                let res = await axios.post('/forums/threads/'+thread._id+'/rename', { title: threadTitle });

                setThread(res.data.thread);
                sendNotification({ text: 'Thread has been successfully renamed.'});

            } catch(error) {
                sendErrorNotification(error);
            }

            closeModal();
        };

        let buttons = [
            {
                title: 'Rename',
                column: 3,
                className: 'btn-success',
                onClick: success,
            },
            {
                title: 'Cancel',
                column: 4,
                className: 'btn-danger',
                onClick: closeModal
            }
        ];

        openModal({ contents: (
            <LabelInput
                className="rename-thread-input"
                title='New Thread Title'
                defaultValue={thread.title}
                value={null}
                setState={text => threadTitle = text}
                placeholder='New Thread Title'
                onEnter={success}
            />
        ), buttons })

    };

    let [ options, setOptions ] = useState([]);

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

        setOptions([
            {
                title: (thread.pinned ? 'Unp' : 'P') + 'in Thread',
                icon: 'fas fa-thumbtack',
                onClick: pin,
            },
            {
                title: (!thread.open ? 'Unl' : 'L') + 'ock Thread',
                icon: 'fas fa-lock' + (!thread.open ? '-open' : ''),
                onClick: lock,
            },
            {
                title: 'Rename Thread',
                icon: 'fas fa-edit',
                onClick: renameThread,
            },
            {
                title: (thread.archived ? 'Restore' : 'Delete') + ' Thread',
                icon: 'fas fa-trash-'+(thread.archived ? 'restore' : 'alt'),
                onClick: deleteThread,
            },
            {
                title: 'Move Thread',
                icon: 'fas fa-arrows-alt',
                onClick: () => {
                }
            },
        ]);

    }, [ user, thread, page ]);

    useEffect(() => {
        return closeModal;
    }, []);
    
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
                    title={
                        <>
                            { thread.pinned ?
                                <>
                                    <i style={{marginRight: '5px', color: 'green'}} className="fas fa-thumbtack" />
                                </>
                            :
                                ''
                            }
                            {
                                !thread.open ?
                                    <>
                                        <i style={{marginRight: '5px', color: 'red'}} className="fas fa-lock" />
                                    </>
                                :
                                    ''
                            }
                            { thread.title }
                        </>
                    }
                    description={
                        !thread.archived ? '' 
                        : (
                            <>
                                <span className='red'>Thread deleted by {<DisplayUser user={thread.archivedBy} />}</span>
                                <span> - {formatDate(thread.archivedStamp)}</span>
                            </>
                        )}
                    minimizable={false}
                    ref={scrollRef}
                >
                    { posts && <PostList posts={posts} setPosts={setPosts} /> }
                </CollapsibleWidget>
                <Pages 
                    pageTotal={thread.pageTotal}
                    base={`/forums/thread/${thread._id}`}
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
