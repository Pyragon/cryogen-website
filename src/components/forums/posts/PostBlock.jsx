import React, { useContext, useState } from 'react';

import { formatDate } from '../../../utils/format';
import axios from '../../../utils/axios';
import Permissions from '../../../utils/permissions';
import Post from './Post';

import EditorContext from '../../../utils/contexts/EditorContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';
import UserContext from '../../../utils/contexts/UserContext';
import DisplayUser from '../../utils/user/DisplayUser';
import EditPost from './EditPost';
import Dropdown from '../../utils/Dropdown';

async function clickedThanks(e, add, post, setThanks) {
    e.preventDefault();
    let link = '/forums/posts/' + post._id + '/thanks';
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

async function clickedQuote(e, post, setReply) {
    e.preventDefault();
    let reply = `[quote="${post._id}"]${post.content}[/quote]`;
    setReply((prev) => prev + (prev ? '\n' : '') + reply);
}

export default function PostBlock({ post, setPosts }) {
    let { user } = useContext(UserContext);
    let { setReply } = useContext(EditorContext);
    let [ editing, setEditing ] = useState(false);
    let [ postState, setPostState ] = useState(post);
    let loggedIn = user !== null, canPost = true;
    let [ thanks, setThanks ] = useState(post.thanks);
    let { sendNotification, sendErrorNotification, sendConfirmation, closeModal } = useContext(NotificationContext);

    let confirmDeletion = () => {
        if(!user) return;
        if(!post.permissions.canModerate(user)) return;
        
        let deletePost = async () => {

            try {

                closeModal();

                await axios.delete(`/forums/posts/${post._id}`);

                setPosts(posts => posts.filter(p => p._id !== post._id));
                sendNotification({ text: 'Post has been deleted.' });

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        sendConfirmation({ text: 'Are you sure you wish to delete this post?', onSuccess: deletePost });
        
    };

    let copyIdToClipboard = () => {
        navigator.clipboard.writeText(postState._id);
        sendNotification({ text: 'Copied post id to clipboard.' });
    };

    let options = [];

    if(post.first !== true) {
        options.push({
            title: 'Delete',
            onClick: confirmDeletion,
            icon: 'fa fa-trash'
        });
    }

    post.permissions = new Permissions(post.thread.subforum.permissions);
    let canEdit = post.permissions.canEdit(user, post);

    let editColumns = postState.edited ? 1 : 0; //last edited
    if(loggedIn) editColumns++; //thanks
    if(canPost) editColumns++; //quote
    if(canEdit) editColumns++; //edit
    if(post.permissions.canModerate(user)) editColumns++; //moderation options
    
    return (
        <div key={postState._id} className="post-content-block">
            <div className="post-date-block">
                <div className="post-date small">{formatDate(postState.createdAt)}</div>
                <div className="post-id small link" onClick={copyIdToClipboard}>{'#'+postState._id}</div>
            </div>
            <div className="post-message-block">
                { editing ? <EditPost post={postState} setEditing={setEditing} setPost={setPostState}/> :
                    <>
                        <Post post={postState} />
                    </>
                }
            </div>
            <div className="edit-options" style={{gridTemplateColumns: `repeat(${editColumns}, auto)`}}>
                { postState.edited && <div className="last-edited edit-option small">{'Lasted Edited: '+formatDate(postState.edited)}</div> }
                { loggedIn && canEdit && <div className="edit-post link edit-option" onClick={() => {setEditing(true)}}>Edit</div> }
                { loggedIn && canPost && <div className="quote-post link edit-option" onClick={(e) => clickedQuote(e, postState, setReply)}>Quote</div> }
                { loggedIn && 
                    <>
                        { thanks.find(thank => thank.user._id === user._id) ? 
                            <div className="link edit-option" onClick={(e) => clickedThanks(e, false, postState, setThanks)}>Remove Thanks</div> : 
                            <div className="link edit-option" onClick={(e) => clickedThanks(e, true, postState, setThanks)}>Thanks</div> 
                        }
                    </>
                }
                { post.permissions.canModerate(user) && options.length > 0 &&
                    <Dropdown
                        title='Moderator Options'
                        className='edit-option'
                        options={options}
                    />
                }
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
