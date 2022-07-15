import React, { useContext, useEffect, useState, useRef } from 'react';
import axios from '../../../../utils/axios';
import { validate, validateDiscord, validateEmail, validatePassword, validateUsername } from '../../../../utils/validate';

import TableSection from '../../../utils/sections/TableSection';
import Pages from '../../../utils/Pages';
import DisplayUsergroup, { WithCrowns } from '../../../utils/user/DisplayUsergroup';

import PageContext from '../../../../utils/contexts/PageContext';
import NotificationContext from '../../../../utils/contexts/NotificationContext';
import CreateUser from './CreateUser';

const info = [

];

export default function Users() {
    let { page, setPage } = useContext(PageContext);

    let [ pageTotal, setPageTotal ] = useState(1);
    let [ users, setUsers ] = useState([]);

    let { sendNotification, sendErrorNotification, openModal, closeModal, sendConfirmation } = useContext(NotificationContext);

    let valueRef = useRef({});

    let rows = users.map(user => {
        return [
            {
                type: 'text',
                value: user.username,
            },
            {
                type: 'element',
                value: (
                    <WithCrowns
                        name={user.display.name}
                        group={user.displayGroup}
                    />
                )
            },
            {
                type: 'text',
                value: user.email || 'No email',
            },
            {
                type: 'text',
                value: user.discord || 'No discord',
            },
            {
                type: 'hover',
                value: [
                    <DisplayUsergroup group={user.displayGroup} />,
                    ...user.usergroups.map(group => <DisplayUsergroup group={group}/>)
                ],
                shortTitle: true,
            },
            {
                type: 'text',
                value: user.postCount,
            },
            {
                type: 'text',
                value: user.threadsCreated,
            },
            {
                type: 'date',
                value: user.createdAt,
            },
            {
                type: 'date',
                value: user.updatedAt,
            },
            {
                type: 'button',
                value: 'Edit',
                className: 'link',
                onClick: () => edit(user),
            }
        ]
    });

    let edit = async(user) => {
        valueRef.current = user;
        openCreateModal(submitEdit, user);
    };

    let submitEdit = async(user) => {
        let values = valueRef.current;

        values.username = user.username;
        values.usergroups = values.usergroups.filter(group => group !== '' && group !== 'none');

        let error = validate(validateForEdit, values);
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.put(`/users/${user._id}`, values);

            closeModal();
            valueRef.current = {};

            sendNotification({ text: 'User successfully edited.' });

            setUsers(users.map(user => user._id === res.data.user._id ? res.data.user : user));

        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let create = async () => {
        let values = valueRef.current;

        values.usergroups = values.usergroups.filter(group => group !== '' && group !== 'none');

        let error = validate(validateForCreate, values);
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.post('/users', {
                ...values,
                admin: true
            });

            closeModal();
            valueRef.current = {};

            sendNotification({ text: 'User successfully created.' });

            setUsers([ ...users, res.data.user ]);

        } catch(error) {
            sendErrorNotification(error);
        }
        
    };

    let deleteUser = (user) => {
        try {

            axios.delete(`/users/${user._id}`);

            sendNotification({ text: 'User successfully deleted.' });

            setUsers(users.filter(u => u._id !== user._id));


        } catch(error) {
            sendErrorNotification(error);
        }
        closeModal();
    };

    let openCreateModal = (edit, user) => {
        let buttons = [];
        buttons.push({
            title: edit ? 'Edit' : 'Create',
            column: edit ? 2 : 3,
            className: 'btn-success',
            onClick: edit ? () => edit(user) : create,
        });
        if(edit)
            buttons.push({
                title: 'Delete',
                column: 3,
                className: 'btn-danger',
                onClick: () => sendConfirmation({ text: 'Are you sure you want to delete this user?', onSuccess: () => deleteUser(user) }),
            });
        buttons.push({
            title: 'Cancel',
            column: 4,
            className: 'btn-danger',
            onClick: () => {
                closeModal();
                valueRef.current = {};
            }
        });
        openModal({
            contents: <CreateUser ref={valueRef} create={edit ? () => edit(user) : create } isCreate={!edit}/>,
            buttons,
            modalSize: {
                width: '30rem'
            }
        })
    };

    useEffect(() => {

        if(!page) setPage(1);

        let load = async () => {

            try {

                let res = await axios.get(`/users/admin/${page}`);

                setUsers(res.data.users);

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
                        title: 'Create New User',
                        icon: 'fa fa-plus-circle',
                        onClick: openCreateModal
                    }
                ]}
                headers={[ 'Username', 'Display Name', 'Email', 'Discord', 'Usergroups', 'Posts', 'Threads', 'Created', 'Updated', 'Edit' ]}
                rows={rows}
            />
            <Pages
                pageTotal={pageTotal}
                base={'/forums/users'}
            />
        </>
    )
}

const validateOptions = {
    username: validateUsername,
    displayName: {
        type: 'string',
        name: 'Display Name',
        min: 3,
        max: 12,
        regex: /^[a-zA-Z0-9_ ]+$/,
    },
    email: validateEmail,
    discord: validateDiscord,
    displayGroup: {
        type: 'string',
        name: 'Display Group',
        regex: /^[a-f\d]{24}$/i,
    },
    usergroups: {
        type: ['string'],
        name: 'Usergroups',
        regex: /^[a-f\d]{24}$/i,
    },
};

const validateForCreate = {
    ...validateOptions,
    password: validatePassword
};

const validateForEdit = {
    ...validateOptions,
    password: {
        ...validatePassword,
        required: false,
    }
};
