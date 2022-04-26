import React, { useState, useContext } from 'react';
import { useEffect } from 'react';

import axios from '../../../../utils/axios';
import TableSection from '../../../utils/sections/TableSection';
import SectionContext from '../../../../utils/contexts/SectionContext';
import PageContext from '../../../../utils/contexts/PageContext';
import Pages from '../../../utils/Pages';
import ViewChain from './ViewChain';
import NotificationContext from '../../../../utils/contexts/NotificationContext';
import UserContext from '../../../../utils/contexts/UserContext';

const info = [
    'Click the search icon to begin searching through your inbox. Search using filters with filter:value and separate with commas. True/false or yes/no can be used.',
    'Filters: from, subject, body, sent (date), between (date-date)',
    'Examples: from:cody, subject: example, between: (01/01/2022-01/31/2022)'
];

export default function InboxSection() {
    let { user } = useContext(UserContext);
    let { page } = useContext(PageContext);
    let [ pageTotal, setPageTotal ] = useState(1);
    let [ chains, setChains ] = useState([]);
    let { setSection } = useContext(SectionContext);
    let { sendErrorNotification } = useContext(NotificationContext);

    let [ viewingChain, setViewingChain ] = useState(null);

    let rows = chains.map(chain => {
        return [
            {
                value: chain.notifyUsersWarning.some(notifyUser => notifyUser._id === user._id),
                type: 'notify',
            },
            {
                value: chain.author,
                type: 'user',
            },
            {
                value: chain.recipients,
                type: 'users',
            },
            {
                value: chain.subject,
                type: 'text',
            },
            {
                value: chain.author,
                type: 'user',
            },
            {
                value: chain.createdAt,
                type: 'date',
            }, 
            {
                value: <div style={{textDecoration: 'underline', cursor: 'pointer'}}>Read</div>,
                type: 'button',
                onClick: () => setViewingChain(chain),
            }
        ]
    });
    useEffect(() => {

        let load = async () => {

            try {

                let res = await axios.get(`/forums/private/inbox/${page}`);

                setChains(res.data.chains);
                setPageTotal(res.data.pageTotal);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        load();
    }, [ page ]);
    return (
        <>
            { viewingChain && <ViewChain chain={viewingChain} setViewingChain={setViewingChain} setChains={setChains}/> }
            { !viewingChain && <>
                <TableSection
                    info={info}
                    actions={[
                        {
                            icon: 'fa-paper-plane',
                            title: 'New Message',
                            onClick: () => setSection('New')
                        }
                    ]}
                    headers={[ 'Author', 'Recipient(s)', 'Subject', 'Last message from', 'Created', 'Read' ]}
                    rows={rows}
                />
                <Pages
                    pageTotal={pageTotal}
                    base='/forums/private/inbox'
                />
            </> }
        </>
    );
}
