import React, { useState, useContext } from 'react';
import { useEffect } from 'react';

import axios from '../../../utils/axios';
import TableSection from '../../utils/sections/TableSection';
import SectionContext from '../../../utils/contexts/SectionContext';
import { formatDate } from '../../../utils/format';
import Dropdown from '../../utils/Dropdown';
import PageContext from '../../../utils/contexts/PageContext';
import Pages from '../../utils/Pages';
import MessageContext from '../../../utils/contexts/MessageContext';
import InboxMessage from './InboxMessage';
import NotificationContext from '../../../utils/contexts/NotificationContext';

const info = [
    'Click the search icon to begin searching through your inbox. Search using filters with filter:value and separate with commas. True/false or yes/no can be used.',
    'Filters: from, subject, body, sent (date), between (date-date)',
    'Examples: from:cody, subject: example, between: (01/01/2022-01/31/2022)'
];

function markAsReadOrUnread(sendErrorNotification, message, setMessages) {
    axios.post(`/forums/private/inbox/${message._id}/mark`)
        .then(res => setMessages(messages => messages.map(m => m._id === message._id ? res.data.message : m)))
        .catch(sendErrorNotification);
}

function deleteMessage(sendConfirmation, sendNotification, sendErrorNotification, message, setMessages) {
    sendConfirmation({
        text: 'Are you sure you want to delete this message?',
        onSuccess: (close) => {
            axios.post(`/forums/private/inbox/${message._id}/delete`)
                .then(res => {
                    setMessages(messages => messages.filter(m => m._id !== message._id));
                    sendNotification({ text: 'Message has been deleted.' });
                    close();
                })
                .catch(sendErrorNotification);
        }
    })
}

function reply(message, setNewMessageValues, setSection) {
    setNewMessageValues({
        recipients: message.author.displayName,
        subject: `RE: ${message.subject}`,
    });
    setSection('New');
}

function viewMessage(openModal, closeModal, message) {
    openModal({
        contents: <InboxMessage message={message} />,
        buttons: [
            {
                title: 'Close',
                className: 'btn btn-danger',
                column: 4,
                onClick: () => closeModal(),
            },
        ]
    });
}

export default function InboxSection() {
    let { page } = useContext(PageContext);
    let [ pageTotal, setPageTotal ] = useState(1);
    let [ messages, setMessages ] = useState([]);
    let { setSection } = useContext(SectionContext);
    let { setNewMessageValues } = useContext(MessageContext);
    let { sendNotification, sendErrorNotification, sendConfirmation, openModal, closeModal } = useContext(NotificationContext);

    let rows = messages.map(message => {
        return [
            {
                value: message.author,
                type: 'user',
            },
            {
                value: message.subject,
                type: 'text',
            },
            {
                value: message.readAt ? 'Read' : 'Unread',
                type: 'text',
                title: message.readAt ? formatDate(message.readAt) : '',
            },
            {
                value: message.createdAt,
                type: 'date',
            }, {
                value: 
                    <Dropdown 
                        title='Actions'
                        className='large-dropdown'
                        options={[
                            {
                                title: 'View',
                                icon: 'fas fa-eye',
                                onClick: () => viewMessage(openModal, closeModal, message),
                            },
                            {
                                title: 'Reply',
                                icon: 'fas fa-reply',
                                onClick: () => reply(message, setNewMessageValues, setSection)
                            },
                            {
                                title: 'Mark as '+(message.readAt ? 'Unread' : 'Read'),
                                icon: message.readAt ? 'fas fa-envelope-open' : 'fas fa-envelope',
                                onClick: () => markAsReadOrUnread(sendErrorNotification, message, setMessages)
                            },
                            {
                                title: 'Delete',
                                icon: 'fas fa-trash',
                                onClick: () => deleteMessage(sendConfirmation, sendNotification, sendErrorNotification, message, setMessages)
                            }
                        ]}
                    />,
                type: 'jsx'
            }
        ]
    });
    useEffect(() => {
        axios.get('http://localhost:8081/forums/private/inbox/'+page)
            .then(res => {
                setMessages(res.data.messages);
                setPageTotal(res.data.pageTotal);
            })
            .catch(console.error);
    }, [ page ]);
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
                headers={[ 'From', 'Subject', 'Status', 'Received', 'Actions' ]}
                rows={rows}
            />
            <Pages
                pageTotal={pageTotal}
                base='/forums/private/inbox'
            />
        </>
    );
}
