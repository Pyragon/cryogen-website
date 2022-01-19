import React, { useState, useEffect, useContext } from 'react';
import axios from '../../../utils/axios';

import { useParams, useNavigate } from 'react-router-dom';

import ForumContainer from '../../../pages/forums/ForumContainer';
import generateBreadcrumbs from '../../../utils/breadcrumbs';
import CollapsibleWidget from '../../utils/CollapsibleWidget';
import LabelInput from '../../utils/LabelInput';
import RichTextEditor from '../../utils/editor/RichTextEditor';
import Button from '../../utils/Button';
import UserContext from '../../../utils/UserContext';

import '../../../styles/forums/Thread.css';

export default function NewThread() {
    let { user } = useContext(UserContext);
    let [ breadcrumbs, setBreadcrumbs ] = useState([]);
    let [ subforum, setSubforum ] = useState(null);
    let [ title, setTitle ] = useState('');
    let [ content, setContent ] = useState('');
    let { forumId } = useParams();
    let navigate = useNavigate();
    async function createThread() {

        if (title.length < 5 || title.length > 50) {
            console.error('Title must be between 5 and 50 characters.');
            return;
        }

        if (content.length < 4 || content.length > 1000) {
            console.error('Content must be between 4 and 1000 characters.');
            return;
        }
        axios.post('/forums/threads/', { title, content, subforum: forumId })
            .then(res => {
                let thread = res.data?.thread;
                if(!thread) {
                    console.error('Error creating thread.');
                    return;
                }
                navigate(`/forums/threads/${thread._id}`);
            }).catch(console.error);
    }
    useEffect(() => {
        axios.get('/forums/subforums/'+forumId)
            .then(res => {
                let forum = res.data;
                setBreadcrumbs(generateBreadcrumbs({ subforum: forum, extend: {
                    title: 'Create new thread'
                } }));
                setSubforum(forum);
            })
            .catch(console.error);
    }, [ forumId ]);
    if(!user) {
        navigate('/forums');
        return;
    }
    return (
        <ForumContainer breadcrumbs={breadcrumbs}>
            <CollapsibleWidget
                title='New Thread'
                description={'Create a new thread in '+(subforum && subforum.name)}
                minimizable={false}
            >
                <LabelInput
                    title='Thread Title'
                    placeholder='Thread Title'
                    value={title}
                    setState={setTitle}
                    className="new-thread-title"
                />
                <div className="input-container" style={{gridTemplateColumns: '1fr'}}>
                    <p>Thread content:</p>
                    <RichTextEditor 
                        style={{ marginLeft: '10px', width: '92%'}} //why 92%?
                        value={content}
                        setState={setContent}
                    />
                </div>
                <Button title='Create Thread' className='create-thread-btn' onClick={createThread}/>
            </CollapsibleWidget>
        </ForumContainer>
    )
}
