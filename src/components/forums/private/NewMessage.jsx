import React, { useState, useContext } from 'react';

import LabelInput from '../../utils/LabelInput';
import RichTextEditor from '../../utils/editor/RichTextEditor';
import Button from '../../utils/Button';
import axios from '../../../utils/axios';
import SectionContext from '../../../utils/contexts/SectionContext';
import MessageContext from '../../../utils/contexts/MessageContext';
import { useEffect } from 'react';

async function sendMessage(recipients, subject, body, draft, setSection) {
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

    let res = await axios.post('http://localhost:8081/forums/private/message/chain', {
        recipients,
        subject,
        body
    });
    if(res.data.error) {
        console.error(res.data.error);
        return;
    }
    if(draft)
        await axios.delete('/forums/private/drafts/'+draft._id);
    setSection('Messages');
}

async function clear(setRecipients, setSubject, setBody) {
    setRecipients('');
    setSubject('');
    setBody('');
    //TODO - maybe an 'are you sure?' if the body is more than 500 chars?
}

async function saveDraft(recipients, subject, body, draft, setSection) {
    
    if (!subject) {
        console.error('The subject must be filled out!');
        return;
    }
    let res;
    if(!draft)
        res = await axios.post('/forums/private/drafts', {
            recipients,
            subject,
            body
        });
    else
        res = await axios.put('/forums/private/drafts/'+draft._id, {
            recipients,
            subject,
            body
        });
    if(res.data.error) {
        console.error(res.data.error);
        return;
    }
    setSection('Drafts');
}

export default function NewMessage() {
    let { setSection } = useContext(SectionContext);
    let { newMessageValues, setNewMessageValues } = useContext(MessageContext);
    let [ recipients, setRecipients ] = useState(newMessageValues.recipients || '');
    let [ subject, setSubject ] = useState(newMessageValues.subject || '');
    let [ body, setBody ] = useState(newMessageValues.body || '');
    let [ draft ] = useState(newMessageValues.draft);

    useEffect(() => setNewMessageValues({}), []);

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
                onClick={() => sendMessage(recipients, subject, body, draft, setSection)}
            />
            <Button
                title='Save as Draft'
                className='send-message-btn'
                onClick={() => saveDraft(recipients, subject, body, draft, setSection)}
            />
            <Button
                title='Clear'
                className='send-message-btn red'
                onClick={() => clear(setRecipients, setSubject, setBody)}
            />
            <div style={{clear: 'both'}} />
        </div>
    )
}
