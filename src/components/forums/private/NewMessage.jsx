import React, { useState, useContext } from 'react';

import LabelInput from '../../utils/LabelInput';
import RichTextEditor from '../../utils/editor/RichTextEditor';
import Button from '../../utils/Button';
import axios from '../../../utils/axios';
import SectionContext from '../../../utils/contexts/SectionContext';

export default function NewMessage() {
    let {setSection} = useContext(SectionContext);
    let [ recipients, setRecipients ] = useState('');
    let [ subject, setSubject ] = useState('');
    let [ body, setBody ] = useState('');

    async function sendMessage() {
        if(!recipients || !subject || !body) {
            console.error('All fields must be filled out.');
            return;
        }
        if(body.length < 5 || body.length > 2000) {
            console.error('Body must be between 5 and 2000 characters.');
            return;
        }
        if(recipients.includes(','))
            recipients = recipients.split(', ?');
        axios.post('http://localhost:8081/forums/private/message', {
            recipients,
            subject,
            body
        })
        .then(_ => setSection('Inbox'))
        .catch(console.error);
    }

    return (
        <div>
            <LabelInput
                title='Recipient(s)'
                description='Separate recipients with a comma, names will autofill as you type'
                value={recipients}
                setState={setRecipients}
            />
            <LabelInput
                title='Subject'
                value={subject}
                setState={setSubject}
            />
            <div className='input-title' style={{marginBottom: '1rem'}}>
                <p>Content:</p>
                <RichTextEditor 
                    style={{ marginLeft: '10px', width: '92%'}}
                    value={body}
                    setState={setBody}
                />
            </div>
            <Button
                title='Send Message'
                className='send-message-btn'
                onClick={sendMessage}
            />
            <div style={{clear: 'both'}} />
        </div>
    )
}
