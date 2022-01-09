import React, { useState, useEffect, useContext } from 'react';

import UserContext from '../../../utils/UserContext';
import axios from '../../../utils/axios';
import CollapsibleWidget from '../../utils/CollapsibleWidget';
import RichTextEditor from '../../utils/editor/RichTextEditor';

import Button from '../../utils/Button';

import PostList from './PostList';

export default function ViewThread({ thread }) {
    let [ posts, setPosts ] = useState([]);
    let { user } = useContext(UserContext);
    let loggedIn = user !== null;
    useEffect(() => {
        fetch('http://localhost:8081/forums/posts/children/'+thread._id)
        .then(res => res.json())
        .then(data => setPosts(data));
    }, []);
    return (
        <>
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
                    <RichTextEditor />
                    <Button className="reply-btn" title="Reply" onClick={() => console.log('Clicked')}/>
                    <div style={{clear: 'both' }}/>
                </CollapsibleWidget> 
            }
        </>
    )
}
