import React, { useContext, useEffect, useState, useRef } from 'react';
import axios from '../../../utils/axios';

import CreateRecoveryInfo from './CreateRecoveryInfo';

import Button from '../../utils/Button';
import Widget from '../../utils/Widget';
import LabelInput from '../../utils/LabelInput';
import Input from '../../utils/Input';

import SectionContext from '../../../utils/contexts/SectionContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';

export default function CreateRecovery({ usernameInput }) {

    let [ username, setUsername ] = useState(usernameInput || '');
    let [ email, setEmail ] = useState('');
    let [ discord, setDiscord ] = useState('');
    let [ previousPasswords, setPreviousPasswords ] = useState([ '', '', '' ]);
    let [ recoveryQuestions, setRecoveryQuestions ] = useState([]);
    let [ recoveryAnswers, setRecoveryAnswers ] = useState([ '', '', '' ]);
    let [ geoLocation, setGeoLocation ] = useState('');
    let [ isp, setIsp ] = useState('');
    let [ additional, setAdditional ] = useState('');
    
    //captcha

    let { sendErrorNotification } = useContext(NotificationContext);
    let { setSection } = useContext(SectionContext);

    let questionRefs = [ useRef(), useRef(), useRef() ];

    useEffect(() => {

        let loadQuestions = async () => {

            let res = await axios.get('/account/recovery/questions');
            if(res.data.error) {
                sendErrorNotification(res.data.error);
                return;
            }

            setRecoveryQuestions(res.data.questions);

        };

        loadQuestions();

    }, []);

    let submit = async () => {
        if(!username) {
            sendErrorNotification('Username is required');
            return;
        }

        if(username.length < 3 || username.length > 12) {
            sendErrorNotification('Username must be between 3 and 12 characters');
            return;
        }

        let emailRegexp = new RegExp('^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)*$');
        let discordRegexp = new RegExp('[a-zA-Z0-9_]{3,32}#[0-9]{4}');

        if(email && !emailRegexp.test(email)) {
            sendErrorNotification('Email is invalid');
            return;
        }

        if(discord && !discordRegexp.test(discord)) {
            sendErrorNotification('Discord name is invalid. Please make sure to include the # and tag.');
            return;
        }

        if(geoLocation && (geoLocation.length < 3 || geoLocation.length > 100)) {
            sendErrorNotification('City/Country must be between 3 and 100 characters');
            return;
        }

        if(isp && (isp.length < 2 || isp.length > 100)) {
            sendErrorNotification('ISP must be between 2 and 100 characters');
            return;
        }

        if(additional && (additional.length < 2 || additional.length > 500)) {
            sendErrorNotification('Additional information must be between 2 and 500 characters');
            return;
        }

        let passwords = [];
        for(let password of previousPasswords) {
            if(!password) continue;
            if(password.length < 8 || password.length > 50) {
                sendErrorNotification('Passwords must be between 8 and 50 characters');
                return;
            }
            if(passwords.includes(password)) {
                sendErrorNotification('You cannot input the same previous password twice.');
                return;
            }
            passwords.push(password);
        }

        let questions = [];
        for(let i = 0; i < 3; i++) {
            let question = questionRefs[i].current.value;
            if(questions.includes(question) && recoveryAnswers[i]) {
                sendErrorNotification('Please use unique recovery questions.');
                return;
            }
            questions.push(question);
            if(recoveryAnswers[i] && (recoveryAnswers[i].length < 3 || recoveryAnswers[i].length > 100)) {
                sendErrorNotification('Recovery answers must be between 3 and 100 characters');
                return;
            }
        }
        
        try {

            let res = await axios.post('/account/recovery', {
                username,
                email,
                discord,
                passwords,
                questions,
                answers: recoveryAnswers,
                geoLocation,
                isp,
                additional
            });

            if(res.data.error) {
                sendErrorNotification(res.data.error);
                return;
            }

        } catch(error) {
            sendErrorNotification(error.response.data.error);
            return;
        }

        // setSection('View Recovery');
        sendErrorNotification('Recovery has been submitted');
        //set section to view recovery, set recovery to view as the one we just submitted
    };
    
    let { setSectionSidebar } = useContext(SectionContext);

    useEffect(() => 
        {
            setSectionSidebar(
                <Widget
                    title="Create Recovery Info"
                    description="Information on creating a recovery form"
                >
                    <CreateRecoveryInfo />
                </Widget>
            );
            return () => setSectionSidebar(null);
        }, []);

    return (
        <>
            <div className='create-recovery-inputs'>
                <div>
                    <LabelInput
                        title='Username'
                        placeholder='Enter username'
                        value={username}
                        setState={setUsername}
                    />
                    <LabelInput
                        title='Email'
                        placeholder='Enter email'
                        value={email}
                        setState={setEmail}
                    />
                    <LabelInput
                        title='Discord'
                        placeholder='Enter discord name (Include name#tag)!'
                        value={discord}
                        setState={setDiscord}
                    />
                    <LabelInput
                        title='Enter City/Country you lived in when the account was created'
                        placeholder='Enter city/country'
                        value={geoLocation}
                        setState={setGeoLocation}
                    />
                    <LabelInput
                        title='Enter the name of your ISP when the account was created'
                        placeholder='Enter ISP'
                        value={isp}
                        setState={setIsp}
                    />
                    <p style={{margin: '10px', color: 'white'}}>Enter any additional information:</p>
                    <textarea
                        className='input'
                        placeholder='Enter any additional information'
                        value={additional}
                        onChange={e => setAdditional(e.target.value)}
                    />
                </div>
                <div>
                    <h3>Previous Passwords:</h3>
                    <LabelInput
                        title='Previous Password 1'
                        placeholder='Enter previous password'
                        value={previousPasswords[0]}
                        setState={(value) => setPreviousPasswords(passwords => passwords.map((_, index) => index === 0 ? value : passwords[index]))}
                    />
                    <LabelInput
                        title='Previous Password 2'
                        placeholder='Enter previous password'
                        value={previousPasswords[1]}
                        setState={(value) => setPreviousPasswords(passwords => passwords.map((_, index) => index === 1 ? value : passwords[index]))}
                    />
                    <LabelInput
                        title='Previous Password 3'
                        placeholder='Enter previous password'
                        value={previousPasswords[2]}
                        setState={(value) => setPreviousPasswords(passwords => passwords.map((_, index) => index === 2 ? value : passwords[index]))}
                    />
                    <h3>Recovery Questions:</h3>
                    { questionRefs.map((ref, index) => {
                        return (
                            <div key={index}>
                                <select className='create-recovery-select input' ref={ref}>
                                    { recoveryQuestions.map((question, index) => <option key={index} value={question._id}>{question.question}</option>) }
                                </select>
                                <Input
                                    value={recoveryAnswers[index]}
                                    setState={(value) => setRecoveryAnswers(answers => answers.map((_, i) => i === index ? value : answers[i]))}
                                    placeholder='Enter your answer'
                                />  
                            </div>
                        )
                    })}
                </div>
            </div>
            <div className='create-recovery-btn-container'>
                <Button
                    title='Create Recovery'
                    onClick={submit}
                />
            </div>
        </>
        
    )
}
