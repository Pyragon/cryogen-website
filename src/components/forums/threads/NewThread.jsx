import React, { useState, useEffect, useContext, createRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

import axios from '../../../utils/axios';
import setUserActivity from '../../../utils/user-activity';
import Permissions from '../../../utils/permissions';
import generateBreadcrumbs from '../../../utils/breadcrumbs';
import { validate } from '../../../utils/validate';

import ForumContainer from '../../../pages/forums/ForumContainer';
import CollapsibleWidget from '../../utils/CollapsibleWidget';
import LabelInput from '../../utils/LabelInput';
import RichTextEditor from '../../utils/editor/RichTextEditor';
import Button from '../../utils/Button';
import PollOption from './polls/PollOption'
import PollOptions from './polls/PollOptions';

import UserContext from '../../../utils/contexts/UserContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';

import '../../../styles/forums/Thread.css';

export default function NewThread() {
    let navigate = useNavigate();
    let { user } = useContext(UserContext);
    let [ breadcrumbs, setBreadcrumbs ] = useState([]);
    let [ subforum, setSubforum ] = useState(null);
    let [ title, setTitle ] = useState('');
    let [ content, setContent ] = useState('');
    let { forumId } = useParams();
    let [ question, setQuestion ] = useState('');

    let { sendErrorNotification } = useContext(NotificationContext);

    let answer1Ref = createRef();
    let answer2Ref = createRef();
    let [ options, setOptions ] = useState([
        { ref: answer1Ref, option: <PollOption key={0} index={0} ref={answer1Ref}/>},
        { ref: answer2Ref, option: <PollOption key={1} index={1} ref={answer2Ref}/>}
    ]);

    setUserActivity(user, 'Creating new thread', 'creating');

    let createThread = async() => {

        let validateOptions = {
            title: {
                required: true,
                name: 'Title',
                min: 5,
                max: 50,
            },
            content: {
                required: true,
                name: 'Content',
                min: 4,
                max: 1000,
            }
        };

        let [ validated, error ] = validate(validateOptions, { title, content });
        if(!validated) {
            sendErrorNotification(error);
            return;
        }

        let pollOptions = [];
        if(question) {
            if(question.length < 5 || question.length > 50) {
                sendErrorNotification('Question must be between 5 and 50 characters.');
                return;
            }
            for(let i = 0; i < 6; i++) {
                if(!options[i]) continue;
                let option = options[i].ref.current.value;
                if(pollOptions.includes(option)) {
                    sendErrorNotification('You cannot have duplicate answers.');
                    return;
                }
                if(option.length < 4 || option.length > 25) {
                    sendErrorNotification('Answers must be between 4 and 25 characters.');
                    return;
                }
                pollOptions.push(option);
            }
            if(pollOptions.length < 2) {
                sendErrorNotification('You must have at least two answers.');
                return;
            }
        }

        try {

            let res = await axios.post('/forums/threads', { title, content, subforum: forumId, question, pollOptions });

            navigate(`/forums/threads/${res.data.thread._id}`);

        } catch(error) {
            sendErrorNotification(error);
        }
    }

    useEffect(() => {

        let load = async () => {

            try {

                let res = await axios.get(`/forums/subforums/${forumId}`);

                let forum = res.data.forum;
                if(forum.permissions)
                    forum.permissions = new Permissions(forum.permissions);

                setSubforum(forum);
                setBreadcrumbs(generateBreadcrumbs({ subforum: forum, extend: {
                    title: 'Create new thread',
                    id: 0
                } }));

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        load();

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
