import React, { useState } from 'react';
import { useEffect } from 'react';

import { useParams } from 'react-router-dom';
import DraftSection from '../../../components/forums/private/DraftSection';
import InboxSection from '../../../components/forums/private/InboxSection';
import NewMessage from '../../../components/forums/private/NewMessage';
import SentSection from '../../../components/forums/private/SentSection';
import Sections from '../../../components/utils/sections/Sections';
import SectionContext from '../../../utils/contexts/SectionContext';

import '../../../styles/forums/Private.css';

const sections = [
    {
        title: 'New',
        content: <NewMessage />,
        description: 'Create and send a new message to a member of Cryogen.'
    },
    {
        title: 'Inbox',
        content: <InboxSection />,
        description: 'All messages you have received from members of Cryogen.',
    },
    {
        title: 'Drafts',
        content: <DraftSection />,
        description: 'All drafts of messages you have saved.',
    },
    {
        title: 'Sent',
        content: <SentSection />,
        description: 'All messages you have sent out.'
    }
];

export default function PrivatePage() {
    let { section: sectionParam } = useParams();
    let active = sections.find(section => section.title.match(new RegExp(sectionParam, 'i')));
    if(!active)
        active = sections.find(section => section.title === 'Inbox');

    let setSection = (newSection) => {
        newSection = sections.find(s => s.title.match(new RegExp(newSection, 'i')));
        if(!newSection)
            newSection = sections.find(s => s.title === 'Inbox');
        
        setSectionState(newSection);
    };

    let [ section, setSectionState ] = useState(active);

    useEffect(() => {
        window.history.replaceState(null, '', '/forums/private/'+section.title.toLowerCase());
    }, [ section ]);
    return (
        <SectionContext.Provider value={{section, setSection}}>
            <Sections 
                sections={sections}
                active={section}
            />
        </SectionContext.Provider>
    )
}
