import React, { useState, useContext } from 'react';
import { useEffect } from 'react';

import axios from '../../../utils/axios';
import TableSection from '../../utils/sections/TableSection';
import SectionContext from '../../../utils/contexts/SectionContext';
import { formatDate } from '../../../utils/format';
import Dropdown from '../../utils/Dropdown';
import { sendNotification, sendErrorNotification } from '../../../utils/notifications';
import PageContext from '../../../utils/contexts/PageContext';
import Pages from '../../utils/Pages';

const info = [
    'Click the search icon to begin searching through your inbox. Search using filters with filter:value and separate with commas. True/false or yes/no can be used.',
    'Filters: from, subject, body, sent (date), between (date-date)',
    'Examples: from:cody, subject: example, between: (01/01/2022-01/31/2022)'
];

function markAsReadOrUnread(message, setMessages) {
    axios.post(`/forums/private/inbox/${message._id}/mark`)
        .then(res => setMessages(messages => messages.map(m => m._id === message._id ? res.data.message : m)))
        .catch(sendErrorNotification);
}

function deleteMessage(message, setMessages) {
    sendNotification({
        text: 'Are you sure?',
        layout: 'center',
        buttons: [
            {
                addClass: 'btn btn-success',
                text: 'Yes',
                onClick: (noty) => {
                    axios.post(`/forums/private/inbox/${message._id}/delete`)
                        .then(res => {
                            noty.close();
                            sendNotification({ text: 'Message has been deleted.' });
                            setMessages(messages => messages.filter(m => m._id !== message._id));
                        }).catch(sendErrorNotification);
                },
            },
            {
                addClass: 'btn btn-danger',
                text: 'Cancel',
                onClick: (noty) => {
                    noty.close();
                },
            }
        ],
    });
}

export default function InboxSection() {
    let { page } = useContext(PageContext);
    let [ pageTotal, setPageTotal ] = useState(1);
    let [ messages, setMessages ] = useState([]);
    let { setSection } = useContext(SectionContext);

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
                                onClick: () => console.log('view')
                            },
                            {
                                title: 'Reply',
                                icon: 'fas fa-reply',
                                onClick: () => console.log('reply')
                            },
                            {
                                title: 'Mark as '+(message.readAt ? 'Unread' : 'Read'),
                                icon: 'fas fa-reply',
                                onClick: () => markAsReadOrUnread(message, setMessages)
                            },
                            {
                                title: 'Delete',
                                icon: 'fas fa-trash',
                                onClick: () => deleteMessage(message, setMessages)
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
