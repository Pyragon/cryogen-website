import React, { useState, useContext } from 'react';
import { useEffect } from 'react';

import axios from '../../../utils/axios';
import TableSection from '../../utils/sections/TableSection';
import SectionContext from '../../../utils/contexts/SectionContext';

const info = [
    'Click the search icon to begin searching through your inbox. Search using filters with filter:value and separate with commas. True/false or yes/no can be used.',
    'Filters: from, subject, body, sent (date), between (date-date)',
    'Examples: from:cody, subject: example, between: (01/01/2022-01/31/2022)'
];

export default function InboxSection() {
    let [ messages, setMessages ] = useState([]);
    let { setSection } = useContext(SectionContext);
    //Add a dropdown field for 'Actions'
    let rows = [[
        'Cody',
        'Hello World',
        '01/01/2022',
        <>
        </>
    ]];
    useEffect(() => {
        axios.get('http://localhost:8081/forums/private/inbox')
            .then(res => setMessages(res.data))
            .catch(console.error);
    }, [ messages ]);
    let actions = [
        {
            icon: 'fa-paper-plane',
            title: 'New Message',
            onClick: () => setSection('Inbox')
        }
    ];
    return (
        <TableSection
            info={info}
            actions={actions}
            headers={[ 'From', 'Subject', 'Sent', 'Actions' ]}
            rows={rows}
        />
    );
}
