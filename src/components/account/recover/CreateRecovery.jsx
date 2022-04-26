import React, { useContext, useEffect, useState, useRef } from 'react';
import axios from '../../../utils/axios';
import { validate, validateUsername, validateEmail } from '../../../utils/validate';

import CreateRecoveryInfo from './CreateRecoveryInfo';

import Button from '../../utils/Button';
import Widget from '../../utils/Widget';
import LabelInput from '../../utils/LabelInput';
import Input from '../../utils/Input';

import SectionContext from '../../../utils/contexts/SectionContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';

export default function CreateRecovery({ usernameInput }) {

    let [ username, setUsername ] = useState(usernameInput || 'cody');
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

            try {

                let res = await axios.get('/account/recovery/questions');
    
                setRecoveryQuestions(res.data.questions);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        loadQuestions();

    }, []);

    let submit = async () => {

        let validateOptions = {
            username: validateUsername,
            email: validateEmail,
            discord: {
                type: 'string',
                name: 'Discord',
                required: false,
                regexp: /[a-zA-Z0-9_]{3,32}#[0-9]{4}/
            },
            geoLocation: {
                type: 'string',
                name: 'City/Country',
                required: false,
                min: 3,
                max: 100,
            },
            isp: {
                type: 'string',
                name: 'ISP',
                required: false,
                min: 3,
                max: 100,
            },
            additional: {
                type: 'string',
                name: 'Additional Information',
                required: false,
                min: 3,
                max: 500,
            },
            passwords: {
                type: ['string'],
                name: 'Previous Passwords',
                required: false,
                min: 8,
                max: 50,
                duplicates: {
                    allowed: false,
                    error: 'Please do not enter the same previous password twice',
                }
            }
        }
    
        let [validated, error] = validate(validateOptions, {
            username,
            email,
            discord,
            geoLocation,
            isp,
            additional,
            passwords: previousPasswords,
        });

        if(!validated) {
            sendErrorNotification(error);
            return;
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
                passwords: previousPasswords,
                questions,
                answers: recoveryAnswers,
                geoLocation,
                isp,
                additional
            });

        } catch(error) {
            sendErrorNotification(error);
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
