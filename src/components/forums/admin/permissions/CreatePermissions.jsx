import React, { useEffect, useState, useContext } from 'react';
import axios from '../../../../utils/axios';

import NotificationContext from '../../../../utils/contexts/NotificationContext';
import TableSection from '../../../utils/sections/TableSection';

export default function Permissions({ setCreating }) {

    let [ usergroups, setUsergroups ] = useState([]);

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let load = async () => {

            try {

                let res = await axios.get('/forums/usergroups');

                setUsergroups(res.data.usergroups);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        load();

    }, []);

    let rows = Object.keys(permissions).map(key => {
        let specialRef = React.useRef();
        let groupsRef = React.useRef();
        return [
            {
                type: 'string',
                value: permissions[key],
            },
            { //Everyone
                type: 'select',
                value: specialOptions,
                ref: specialRef,
            },
            {
                type: 'multiselect',
                value: usergroups.map(group => {
                    return {
                        value: group._id,
                        label: <span>{group.name}</span>
                    }
                }),
                ref: groupsRef,
            }

        ];
    });

    return (
        <>
            <p className='link m-left-10' onClick={() => setCreating(false)}>Press here to go back</p>
            <TableSection
                info={[]}
                actions={[]}
                headers={[ 'Name', 'Special', 'Usergroups' ]}
                rows={rows}
            />
        </>
    )
};

let specialOptions = {
    '-1': 'Everyone',
    '-2': 'Logged In',
    '-3': 'Only the Author',
    '-4': 'If author is Staff',
};

const permissions = {
    canSee: 'Can See',
    canReply: 'Can Reply',
    canEdit: 'Can Edit',
    canCreateThreads: 'Can Create Threads',
    canModerate: 'Can Moderate',
    canCreatePolls: 'Can Create Polls',
};
