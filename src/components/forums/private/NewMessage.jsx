import React, { useState, useContext } from 'react';
import { validate } from '../../../utils/validate';

import LabelInput from '../../utils/LabelInput';
import RichTextEditor from '../../utils/editor/RichTextEditor';
import Button from '../../utils/Button';
import axios from '../../../utils/axios';
import SectionContext from '../../../utils/contexts/SectionContext';
import MessageContext from '../../../utils/contexts/MessageContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';
import { useEffect } from 'react';

export default function NewMessage() {
    let { setSection } = useContext(SectionContext);
    let { newMessageValues, setNewMessageValues } = useContext(MessageContext);
    let [ recipients, setRecipients ] = useState(newMessageValues.recipients || '');
    let [ subject, setSubject ] = useState(newMessageValues.subject || '');
    let [ body, setBody ] = useState(newMessageValues.body || '');
    let [ draft ] = useState(newMessageValues.draft);

    let { sendErrorNotification, sendConfirmation } = useContext(NotificationContext);

    useEffect(() => setNewMessageValues({}), []);

    let clear = () => {

        let clearValues = () => {
            setRecipients('');
            setSubject('');
            setBody('');
        }

        if(body.length < 100) {
            clearValues();
            return;
        }

        sendConfirmation({ text: 'Are you sure you want to clear your message?', clearValues });
    };

    let save = async () => {
        
        let error = validate(validateOptionsForDraft, { subject, recipients, body });
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            if(!draft)
                await axios.post('/forums/private/drafts', { recipients, subject, body });
            else
                await axios.put(`/forums/private/drafts/${draft._id}`, { recipients, subject, body });

            setSection('Drafts');
        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let send = async () => {

        let error = validate(validateOptionsForSend, { subject, recipients, body });
        if(error) {
            sendErrorNotification(error);
            return;
        }

        if(recipients.includes(','))
            recipients = recipients.split(', ?');

        try {
            await axios.post('/forums/private/message/chain', { recipients, subject, body });

            if(draft)
                await axios.delete('/forums/private/drafts/'+draft._id);

            setSection('Messages');

        } catch(error) {
            sendErrorNotification(error);
        }
    };

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
                onClick={send}
            />
            <Button
                title='Save as Draft'
                className='send-message-btn'
                onClick={save}
            />
            <Button
                title='Clear'
                className='send-message-btn red'
                onClick={clear}
            />
            <div style={{clear: 'both'}} />
        </div>
    )
}

let validateBody = {
    type: 'string',
    name: 'Message',
    required: true,
    min: 5,
    max: 2000
};

let validateRecipients = {
    type: 'string',
    name: 'Recipients',
    required: true,
    min: 1,
    max: 50,
};

let validateSubject = {
    type: 'string',
    name: 'Subject',
    required: true,
    min: 3,
    max: 100,
};

const validateOptionsForDraft = {
    recipients: {
        ...validateRecipients,
        required: false,
    },
    subject: validateSubject,
    body: {
        ...validateBody,
        required: false,
    }
};

const validateOptionsForSend = {
    recipients: validateRecipients,
    subject: validateSubject,
    body: validateBody,
}
