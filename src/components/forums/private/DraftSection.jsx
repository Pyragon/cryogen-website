import React, { useContext, useState, useEffect } from 'react';

import axios from '../../../utils/axios';

import TableSection from '../../utils/sections/TableSection';
import Pages from '../../utils/Pages';

import SectionContext from '../../../utils/contexts/SectionContext';
import PageContext from '../../../utils/contexts/PageContext';
import MessageContext from '../../../utils/contexts/MessageContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';

const info = [
    'Click the search icon to begin searching through your drafts. Search using filters with filter:value and separate with commas. True/false or yes/no can be used.',
    'Filters: to, subject, body',
    'Examples: to:cody, subject: example'
];

export default function DraftSection() {
    let { setSection } = useContext(SectionContext);
    let { setNewMessageValues } = useContext(MessageContext);
    let { sendNotification, sendErrorNotification, sendConfirmation } = useContext(NotificationContext);
    let { page } = useContext(PageContext);

    let [ messages, setMessages ] = useState([]);
    let [ pageTotal, setPageTotal ] = useState(1);

    let deleteMessage = async(message) => {
        
        let onSuccess = async(close) => {
            try {

                await axios.delete(`/forums/private/drafts/${message._id}`);

                setMessages(messages => messages.filter(m => m._id !== message._id));
                sendNotification({ text: 'Message has been deleted.' });
                close();

            } catch(error) {
                sendErrorNotification(error);
                return;
            }
        };

        sendConfirmation({ text: 'Are you sure you want to delete this message?', onSuccess });
    };

    useEffect(() => {

        let load = async () => {

            try {

                let res = await axios.get(`/forums/private/drafts/${page}`);

                setMessages(res.data.drafts);
                setPageTotal(res.data.pageTotal);
            } catch(error) {
                sendErrorNotification(error);
            }
        };

        load();
    }, []);

    let continueDraft = message => {
        let newMessageValues = {
            recipients: message.recipients.map(recipient => recipient.username).join(', '),
            subject: message.subject,
            body: message.body,
            draft: message,
            setDrafts: setMessages
        };
        setNewMessageValues(newMessageValues);
        setSection('New');
    };

    let rows = messages.map(message => {
        return [
            {
                value: false,
                type: 'notify',
            },
            {
                value: message.recipients.length > 1 ? message.recipients.length : message.recipients.length === 0 ? '' : message.recipients[0],
                type: message.recipients.length === 1 ? 'user' : 'text',
            },
            {
                value: message.subject,
                type: 'text',
            },
            {
                value: message.createdAt,
                type: 'date',
            },
            {
                value: <div style={{textDecoration: 'underline', cursor: 'pointer'}}>Continue</div>,
                type: 'button',
                onClick: () => continueDraft(message),
            },
            {
                value: <div style={{textDecoration: 'underline', cursor: 'pointer'}}>Delete</div>,
                type: 'button',
                onClick: () => deleteMessage(message),
            }
        ];
    });

    return (
        <>
            <TableSection
                info={info}
                actions={[
                    {
                        icon: 'fa-paper-plane',
                        title: 'New Message',
                        onClick: () => setSection('New')
                    }
                ]}
                headers={[ 'Recipient(s)', 'Subject', 'Created', 'Continue', 'Delete' ]}
                rows={rows}
            />
            <Pages
                pageTotal={pageTotal}
                base='/forums/private/drafts'
            />
        </>
    )
}
