import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';

import SectionContext from '../../../utils/contexts/SectionContext';

import Sections from '../../../components/utils/sections/Sections';
import CreateRecovery from '../../../components/account/recover/CreateRecovery';
import ViewRecovery from '../../../components/account/recover/ViewRecovery';

import '../../../styles/account/Recovery.css';

export default function RecoverPage() {
    let { section: sectionParam, viewKey: viewKeyParam } = useParams();
    let [ searchParams ] = useSearchParams();

    let setSection = (newSection) => {
        newSection = sections.find(s => s.title.toLowerCase() === newSection.toLowerCase());
        if(!newSection)
            newSection = sections[1];
        
        setSectionState(newSection);
    };

    let [ section, setSectionState ] = useState(null);
    let [ sectionTitle, setSectionTitle ] = useState(null);
    let [ sectionDescription, setSectionDescription ] = useState(null);
    let [ sectionSidebar, setSectionSidebar ] = useState(null);
    let [ viewKey, setViewKey ] = useState('');
    let [ created, setCreated ] = useState(true);

    useEffect(() => {
        let username = searchParams.get('username') || '';
        sections[1].content = <CreateRecovery usernameInput={username} />;
        if(!section)
            return;
        window.history.replaceState(null, '', '/recover/'+section.title.toLowerCase().replaceAll(' ', '_'));
    }, [ section ]);

    useEffect(() => {

        let active = !sectionParam ? sections[1] : sections.find(section => section.title.toLowerCase().replaceAll(' ', '_') === sectionParam.toLowerCase());
        if(!active)
            active = sections[1];
        setSectionState(active);
    }, []);

    return (
        <SectionContext.Provider value={{section, setSection, sectionTitle, setSectionTitle, 
            sectionDescription, setSectionDescription, sectionSidebar, setSectionSidebar, 
            viewKey, setViewKey, created, setCreated }}>
                { section && 
                    <Sections 
                        sections={sections}
                        active={section}
                    /> 
                }
        </SectionContext.Provider>
    );
}

const sections = [
    {
        title: 'View Recovery',
        content: <ViewRecovery />,
        description: 'View a recovery you have previously submitted. Make sure you have the secret key!',
    },
    {
        title: 'Create Recovery',
        description: 'Create a new recovery for your account.',
    }
];
