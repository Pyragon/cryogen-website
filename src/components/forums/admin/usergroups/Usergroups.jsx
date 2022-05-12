import React, { useContext, useEffect, useState, useRef } from 'react';
import axios from '../../../../utils/axios';
import { validate } from '../../../../utils/validate';

import TableSection from '../../../utils/sections/TableSection';
import Pages from '../../../utils/Pages';
import CreateUsergroup from './CreateUsergroup';

import PageContext from '../../../../utils/contexts/PageContext';
import NotificationContext from '../../../../utils/contexts/NotificationContext';

const info = [
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum',
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum',
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum'
];

export default function Usergroups() {
    let { page, setPage } = useContext(PageContext);

    let [ pageTotal, setPageTotal ] = useState(1);
    let [ groups, setGroups ] = useState([]);

    let { sendNotification, sendErrorNotification, openModal, closeModal, sendConfirmation } = useContext(NotificationContext);

    let valueRef = useRef();

    let rows = groups.map(group => {
        return [
            {
                type: 'text',
                value: group.name,
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
                        { group.imageBefore && <img src={group.imageBefore} alt='Prefix' /> }
                        { group.imageBefore && group.imageAfter && ' / ' }
                        { group.imageAfter && <img src={group.imageAfter} alt='Suffix' /> }
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

        let [ validated, error ] = validate(validateOptions, values);
        if(!validated) {
            sendErrorNotification(error);
            return;
        }

        try {

            await axios.post('/forums/usergroups', values);

            closeModal();
            valueRef.current = {};

        } catch(error) {
            sendErrorNotification(error);
        }
    }

    let edit = (group) => {
        valueRef.current = group;
        openCreateModal();
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

    let openCreateModal = () => {
        let buttons = [
            {
                title: 'Create',
                column: 3,
                className: 'btn-success',
                onClick: create
            },
            {
                title: 'Cancel',
                column: 4,
                className: 'btn-danger',
                onClick: closeModal,
            }
        ];
        openModal(
            { 
                contents: 
                    <CreateUsergroup ref={valueRef} />, 
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
    imageBefore: {
        required: false,
        type: 'string',
        name: 'Image before',
        min: 1,
        max: 200,
    },
    imageAfter: {
        required: false,
        type: 'string',
        name: 'Image after',
        min: 1,
        max: 200,
    }
};
