import React, { useState, useEffect } from 'react';

import { useParams } from 'react-router-dom';
import DraftSection from '../../../components/forums/private/DraftSection';
import MessagesSection from '../../../components/forums/private/inbox/MessagesSection';
import NewMessage from '../../../components/forums/private/NewMessage';
import Sections from '../../../components/utils/sections/Sections';
import SectionContext from '../../../utils/contexts/SectionContext';

import '../../../styles/forums/Private.css';
import PageContext from '../../../utils/contexts/PageContext';
import MessageContext from '../../../utils/contexts/MessageContext';

const sections = [
    {
        title: 'New',
        content: <NewMessage />,
        description: 'Create and send a new message to a member of Cryogen.'
    },
    {
        title: 'Messages',
        content: <MessagesSection />,
        description: 'All messages you have sent or received from members of Cryogen.',
    },
    {
        title: 'Drafts',
        content: <DraftSection />,
        description: 'All drafts of messages you have saved.',
    },
];

export default function PrivatePage() {
    let { section: sectionParam, page: pageParam } = useParams();
    let active = sections.find(section => section.title.match(new RegExp(sectionParam, 'i')));
    if(!active)
        active = sections.find(section => section.title === 'Messages');

    let setSection = (newSection) => {
        newSection = sections.find(s => s.title.match(new RegExp(newSection, 'i')));
        if(!newSection)
            newSection = sections.find(s => s.title === 'Messages');
        
        setSectionState(newSection);
    };

    pageParam = pageParam || 1;

    let [ page, setPage ] = useState(pageParam);
    let [ section, setSectionState ] = useState(active);
    let [ sectionTitle, setSectionTitle ] = useState(null);
    let [ sectionDescription, setSectionDescription ] = useState(null);
    let [ sectionSidebar, setSectionSidebar ] = useState(null);
    let [ newMessageValues, setNewMessageValues ] = useState({});

    useEffect(() => {
        let p = '/'+page.toString();
        if(section.title === 'New')
            p = '';
        window.history.replaceState(null, '', '/forums/private/'+section.title.toLowerCase()+p);
    }, [ section, page ]);
    return (
        <SectionContext.Provider value={{section, setSection, sectionTitle, setSectionTitle, sectionDescription, setSectionDescription, sectionSidebar, setSectionSidebar }}>
            <PageContext.Provider value={{page, setPage}}>
                <MessageContext.Provider value={{newMessageValues, setNewMessageValues}}>
                    <Sections 
                        sections={sections}
                        active={section}
                    />
                </MessageContext.Provider>
            </PageContext.Provider>
        </SectionContext.Provider>
    )
}
