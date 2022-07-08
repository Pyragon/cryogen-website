import React, { useContext, useEffect, useState, useRef } from 'react';
import axios from '../../../../utils/axios';
import { validate } from '../../../../utils/validate';

import TableSection from '../../../utils/sections/TableSection';
import Pages from '../../../utils/Pages';
import CreateUsergroup from './CreateUsergroup';

import PageContext from '../../../../utils/contexts/PageContext';
import NotificationContext from '../../../../utils/contexts/NotificationContext';
import DisplayUsergroup from '../../../utils/user/DisplayUsergroup';

const info = [
    'The following page can be used to create usergroups for the Cryogen Website and in-game',
    'The rights set here correlate to in-game',
    'Please be very careful editing old usergroups, as it will effect anyone in that usergroup'
];

export default function Usergroups() {
    let { page, setPage } = useContext(PageContext);

    let [ pageTotal, setPageTotal ] = useState(1);
    let [ groups, setGroups ] = useState([]);

    let { sendNotification, sendErrorNotification, openModal, closeModal, sendConfirmation } = useContext(NotificationContext);

    let valueRef = useRef({});

    let rows = groups.map(group => {
        return [
            {
                type: 'element',
                value: <DisplayUsergroup group={group} />,
            },
            {
                type: 'text',
                value: group.rights,
            },
            {
                type: 'text',
                value: group.colour || 'No colour',
                style: !group.colour ? null : { color: group.colour },
            },
            {
                type: 'text',
                value: group.title || 'No title',
            },
            {
                type: 'element',
                value: (
                    <span>
                        {group.htmlBefore && <span>{group.htmlBefore}</span>}
                        {group.htmlBefore && group.htmlAfter && ' / ' }
                        {group.htmlAfter && <span>{group.htmlAfter}</span>}
                    </span>
                )
            },
            {
                type: 'date',
                value: group.createdAt,
            },
            {
                type: 'date',
                value: group.updatedAt,
            },
            {
                type: 'button',
                value: 'Edit',
                className: 'link',
                onClick: () => edit(group),
            },
            {
                type: 'button',
                value: 'Delete',
                className: 'link',
                onClick: async () => await deleteGroup(group),
            }
        ];
    });

    let create = async () => {
        let values = valueRef.current;

        let error = validate(validateOptions, values);
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.post('/forums/usergroups', values);

            closeModal();
            valueRef.current = {};

            sendNotification({ text: 'Usergroup successfully created.' });

            setGroups([ ...groups, res.data.usergroup ]);

        } catch(error) {
            sendErrorNotification(error);
        }
    }

    let edit = (group) => {
        valueRef.current = group;
        openCreateModal(submitEdit, group);
    };

    let submitEdit = async(group) => {
        let values = valueRef.current;
        
        let error = validate(validateOptions, values);
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.put(`/forums/usergroups/${group._id}`, values);

            closeModal();
            valueRef.current = {};

            sendNotification({ text: 'Usergroup successfully edited.' });

            setGroups(groups => groups.map(g => g._id === group._id ? res.data.usergroup : g));

        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let deleteGroup = async (group) => {

        let onSuccess = async () => {

            try {

                await axios.delete(`/forums/usergroups/${group._id}`);

                closeModal();

                sendNotification({ text: 'Usergroup successfully deleted.' });

                setGroups(groups => groups.filter(g => g._id !== group._id));

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        sendConfirmation({ text: 'Are you sure you wish to delete this usergroup?', onSuccess });
    };

    let openCreateModal = (edit, group) => {
        let buttons = [
            {
                title: edit ? 'Edit' : 'Create',
                column: 3,
                className: 'btn-success',
                onClick: edit ? () => edit(group) : create,
            },
            {
                title: 'Cancel',
                column: 4,
                className: 'btn-danger',
                onClick: () => {
                    closeModal();
                    valueRef.current = {};
                },  
            }
        ];
        openModal(
            { 
                contents: <CreateUsergroup ref={valueRef} create={edit ? () => edit(group) : create} />, 
                buttons 
            }
        );
    };

    useEffect(() => {

        if(!page) setPage(1);

        let load = async () => {

            try {

                let res = await axios.get(`/forums/usergroups/${page}`);

                setGroups(res.data.usergroups);
                setPageTotal(res.data.pageTotal);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        load();

    }, [ page ]);

    return (
        <>
            <TableSection
                info={info}
                actions={[
                    {
                        title: 'Create New Usergroup',
                        icon: 'fa fa-plus-circle',
                        onClick: openCreateModal
                    }
                ]}
                headers={[ 'Name', 'Rights', 'Colour', 'Title', 'Images', 'Created', 'Updated', 'Edit', 'Delete' ]}
                rows={rows}
            />
            <Pages
                pageTotal={pageTotal}
                base={'/forums/usergroups'}
            />
        </>
    )
};

let validateOptions = {
    name: {
        required: true,
        type: 'string',
        name: 'Name',
        min: 1,
        max: 50,
    },
    rights: {
        required: true,
        type: 'number',
        name: 'Rights',
    },
    colour: {
        required: false,
        type: 'string',
        name: 'Colour',
        min: 7,
        max: 7,
    },
    title: {
        required: false,
        type: 'string',
        name: 'Title',
        min: 1,
        max: 50,
    },
    htmlBefore: {
        required: false,
        type: 'string',
        name: 'Image before',
        min: 1,
        max: 200,
    },
    htmlAfter: {
        required: false,
        type: 'string',
        name: 'Image after',
        min: 1,
        max: 200,
    }
};
