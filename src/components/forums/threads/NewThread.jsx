import React, { useState, useEffect, useContext, createRef } from 'react';
import axios from '../../../utils/axios';
import setUserActivity from '../../../utils/user-activity';

import Permissions from '../../../utils/permissions';

import { useParams, useNavigate } from 'react-router-dom';

import ForumContainer from '../../../pages/forums/ForumContainer';
import generateBreadcrumbs from '../../../utils/breadcrumbs';
import CollapsibleWidget from '../../utils/CollapsibleWidget';
import LabelInput from '../../utils/LabelInput';
import RichTextEditor from '../../utils/editor/RichTextEditor';
import Button from '../../utils/Button';
import UserContext from '../../../utils/contexts/UserContext';
import PollOption from './polls/PollOption';

import '../../../styles/forums/Thread.css';
import PollOptions from './polls/PollOptions';

export default function NewThread() {
    let { user } = useContext(UserContext);
    let [ breadcrumbs, setBreadcrumbs ] = useState([]);
    let [ subforum, setSubforum ] = useState(null);
    let [ title, setTitle ] = useState('');
    let [ content, setContent ] = useState('');
    let { forumId } = useParams();
    let [ question, setQuestion ] = useState('');
    let answer1Ref = createRef();
    let answer2Ref = createRef();
    let [ options, setOptions ] = useState([
        { ref: answer1Ref, option: <PollOption key={0} index={0} ref={answer1Ref}/>},
        { ref: answer2Ref, option: <PollOption key={1} index={1} ref={answer2Ref}/>}
    ]);
    setUserActivity(user, 'Creating new thread', 'creating');
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
        let pollOptions = [];
        if(question) {
            if(question.length < 5 || question.length > 50) {
                console.error('Question must be between 5 and 50 characters.');
                return;
            }
            for(let i = 0; i < 6; i++) {
                if(!options[i]) continue;
                let option = options[i].ref.current.value;
                if(pollOptions.includes(option)) {
                    console.error('You cannot have duplicate answers.');
                    return;
                }
                if(option.length < 4 || option.length > 25) {
                    console.error('Answers must be between 4 and 25 characters.');
                    return;
                }
                pollOptions.push(option);
            }
            if(pollOptions.length < 2) {
                console.error('You must have at least two answers.');
                return;
            }
        }
        axios.post('/forums/threads/', { title, content, subforum: forumId, question, pollOptions })
            .then(res => {
                let thread = res.data.thread;
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
                    title: 'Create new thread',
                    id: 0
                } }));
                if(forum.permissions)
                    forum.permissions = new Permissions(forum.permissions);
                setSubforum(forum);
            })
            .catch(console.error);
    }, [ forumId ]);
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
                        style={{ marginLeft: '10px', width: '92%' }}
                        value={content}
                        setState={setContent}
                    />
                </div>
                <Button title='Create Thread' className='create-thread-btn' onClick={createThread}/>
            </CollapsibleWidget>
            { subforum && subforum.permissions.canCreatePolls(user) && 
                <CollapsibleWidget
                    title='Poll Options'
                    description='Add a poll to this thread'
                    collapsed={false}
                >
                    <PollOptions options={options} setOptions={setOptions} question={question} setQuestion={setQuestion}/>
                </CollapsibleWidget>
            }
        </ForumContainer>
    )
}
