import React, { useContext, useEffect, useState } from 'react';
import axios from '../../../../utils/axios';

import TableSection from '../../../utils/sections/TableSection';
import CreatePermissions from './CreatePermissions';
import Pages from '../../../utils/Pages';

import SectionContext from '../../../../utils/contexts/SectionContext';
import PageContext from '../../../../utils/contexts/PageContext';
import NotificationContext from '../../../../utils/contexts/NotificationContext';

const info = [
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum',
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum',
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum'
];

export default function Permissions() {
    let { page, setPage } = useContext(PageContext);
    let { setSectionTitle, setSectionDescription } = useContext(SectionContext);
    
    let [ pageTotal, setPageTotal ] = useState(1);
    let [ permissions, setPermissions ] = useState([]);
    let [ creating, setCreating ] = useState(false);

    let { sendErrorNotification } = useContext(NotificationContext);

    let edit = async () => {

        //open modal

    };

    let rows = permissions.map(permission => {
        return [
            {
                type: 'text',
                value: permission.name,
                groups: permission.usergroups,
            },
            {
                type: 'groups',
                value: permission.canSee,
                groups: permission.usergroups,
            },
            {
                type: 'groups',
                value: permission.canReply,
                groups: permission.usergroups,
            },
            {
                type: 'groups',
                value: permission.canEdit,
                groups: permission.usergroups,
            },
            {
                type: 'groups',
                value: permission.canCreateThreads,
                groups: permission.usergroups,
            },
            {
                type: 'groups',
                value: permission.canModerate,
                groups: permission.usergroups,
            },
            {
                type: 'groups',
                value: permission.canCreatePolls,
                groups: permission.usergroups,
            },
            {
                type: 'button',
                className: 'link',
                value: 'Edit',
                onClick: edit
            },
        ];
    });

    useEffect(() => {

        if(!page) setPage(1);

        let load = async () => {

            try {

                let res = await axios.get(`/forums/permissions/${page}`);

                setPermissions(res.data.permissions);
                setPageTotal(res.data.pageTotal);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        load();

    }, [ page ]);

    if(creating) return <CreatePermissions setCreating={setCreating} />;

    return (
        <>
            <TableSection
                info={info}
                actions={[
                    {
                        title: 'Create New Permission',
                        icon: 'fa fa-plus-circle',
                        onClick: () => {
                            setCreating(true);
                            setSectionTitle('Create New Permissions');
                            setSectionDescription('Create a new permission set for the forums.');
                        }
                    }
                ]}
                headers={[ 'Name', 'Can See', 'Can Reply', 'Can Edit', 'Can Create Threads', 'Can Moderate', 'Can Create Polls', 'Edit' ]}
                rows={rows}
            />
            <Pages
                pageTotal={pageTotal}
                base={'/forums/admin/permissions'}
            />
        </>
    )
}
