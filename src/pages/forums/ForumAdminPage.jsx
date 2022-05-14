import React, { useEffect, useState, useMemo } from 'react';
import { useParams } from 'react-router-dom';

import Sections from '../../components/utils/sections/Sections';
import Permissions from '../../components/forums/admin/permissions/Permissions';

import SectionContext from '../../utils/contexts/SectionContext';
import PageContext from '../../utils/contexts/PageContext';
import Usergroups from '../../components/forums/admin/usergroups/Usergroups';

import '../../styles/forums/Admin.css';
import BBCodes from '../../components/forums/admin/bbcodes/BBCodes';

export default function ForumAdminPage() {
    let { section: sectionParam, page: pageParam } = useParams();

    let [ page, setPage ] = useState(pageParam);
    let [section, setSectionState] = useState(null);
    let [sectionTitle, setSectionTitle] = useState(null);
    let [sectionDescription, setSectionDescription] = useState(null);
    let pageProvider = useMemo(() => ({ page, setPage }), [ page, setPage ]);

    let setSection = newSection => {
        newSection = sections.find(s => s.title.toLowerCase() === newSection.toLowerCase());
        if (!newSection)
            newSection = sections[1];

        setSectionState(newSection);
    };

    useEffect(() => {
        if (!section) return;

        let url = '/forums/admin/' + section.title.toLowerCase().replaceAll(' ', '_');
        if (page) url += '/' + page;
        window.history.replaceState(null, '', url);
    }, [section, page]);

    useEffect(() => {

        let active = !sectionParam ? sections[1] : sections.find(section => section.title.toLowerCase().replaceAll(' ', '_') === sectionParam.toLowerCase());
        if (!active)
            active = sections[1];
        setSectionState(active);
    }, []);

    return (
        <SectionContext.Provider value={{ section, setSection, sectionTitle, setSectionTitle, sectionDescription, setSectionDescription }}>
            <PageContext.Provider value={pageProvider}>
                { section && <Sections sections={sections} active={section} /> }
            </PageContext.Provider>
        </SectionContext.Provider>
    )
}

const sections = [
    {
        title: 'Forums',
        description: 'Manage the main forums including subforums.',
    },
    {
        title: 'Permissions',
        content: <Permissions />,
        description: 'Manage the permissions for forums and subforums.',
    },
    {
        title: 'Users',
        description: 'Manage the users and their settings.',
    },
    {
        title: 'Usergroups',
        content: <Usergroups />,
        description: 'Manage the usergroups and their settings.',
    },
    {
        title: 'BBCodes',
        content: <BBCodes />,
        description: 'Manage the BBCodes and their settings.',
    },
    {
        title: 'Misc',
        description: 'Miscellaneous settings for the forums.',
    }
];
