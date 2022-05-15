import React, { useContext, useEffect, useRef, useState } from 'react';
import axios from '../../../../utils/axios';
import { validate } from '../../../../utils/validate';

import CreateBBCode from './CreateBBCode';
import TableSection from '../../../utils/sections/TableSection';
import Pages from '../../../utils/Pages';

import NotificationContext from '../../../../utils/contexts/NotificationContext';
import PageContext from '../../../../utils/contexts/PageContext';

const info = [
    'The following page can be used to create bbcodes for the Cryogen Forums',
    'Only add/edit bbcodes if you are 100% sure what you are doing. The HTML from these bbcodes is placed directly on the page. Any mistakes can severely damage the security of the site.'
];

export default function BBCodes() {
    let { page, setPage } = useContext(PageContext);

    let [ pageTotal, setPageTotal ] = useState(1);
    let [ codes, setCodes ] = useState([]);

    let { sendNotification, sendErrorNotification, sendConfirmation, closeModal, openModal } = useContext(NotificationContext);

    let valueRef = useRef({});

    let rows = codes.map(code => {
        return [
            {
                type: 'text',
                value: code.name,
            },
            {
                type: 'text',
                value: code.description,
            },
            {
                type: 'hover',
                value: code.matches,
                shortTitle: true,
            },
            {
                type: 'text',
                value: code.replace,
            },
            {
                type: 'hover',
                value: [
                    code.example,
                    'INTO',
                    {
                        value: code.formatted,
                        dangerous: true
                    }
                ],
                shortTitle: true,
            },
            {
                type: 'date',
                value: code.createdAt,
            },
            {
                type: 'date',
                value: code.updatedAt,
            },
            {
                type: 'button',
                className: 'link',
                value: 'Edit',
                onClick: () => edit(code),
            },
            {
                type: 'button',
                className: 'link',
                value: 'Delete',
                onClick: async() => deleteCode(code),
            }
        ];
    });

    let deleteCode = async (code) => {

        let onSuccess = async () => {

            try {
                 
                await axios.delete(`/forums/bbcodes/${code._id}`);
                
                closeModal();

                sendNotification({ text: 'BBCode successfully deleted.' });

                setCodes(codes.filter(c => c._id !== code._id));

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        sendConfirmation({ text: 'Are you sure you wish to delete this BBCode?', onSuccess });

    };

    let edit = (code) => {
        valueRef.current = code;
        openCreateModal(submitEdit, code);
    };

    let submitEdit = async (code) => {
        let values = valueRef.current;

        values.matches = values.matches.filter(match => match !== '');

        let error = validate(validateOptions, values);
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.put(`/forums/bbcodes/${code._id}`, values);

            closeModal();
            valueRef.current = {};

            sendNotification({ text: 'BBCode successfully edited.' });

            setCodes(code => code.map(c => c._id === code._id ? res.data.bbcode : c));

        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let create = async () => {
        let values = valueRef.current;

        values.matches = values.matches.filter(match => match !== '');

        let error = validate(validateOptions, values);
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.post('/forums/bbcodes', values);

            closeModal();
            valueRef.current = {};

            sendNotification({ text: 'BBCode successfully created.' });

            setCodes(codes => [ ...codes, res.data.bbcode ]);

        } catch(error) {
            sendErrorNotification(error);
        }
    };

    let openCreateModal = (edit, code) => {
        let buttons = [
            {
                title: edit ? 'Edit' : 'Create',
                column: 3,
                className: 'btn-success',
                onClick: edit ? () => edit(code) : create,
            },
            {
                title: 'Cancel',
                column: 4,
                className: 'btn-danger',
                onClick: () => {
                    closeModal();
                    valueRef.current = {};
                }
            }
        ];
        openModal({
            contents: <CreateBBCode ref={valueRef} create={edit ? () => edit(code) : create} />,
            buttons
        })
    };

    useEffect(() => {

        if(!page) setPage(1);

        let load = async () => {

            let res = await axios.get(`/forums/bbcodes/${page}`);

            setCodes(res.data.codes);
            setPageTotal(res.data.pageTotal);

        };

        load();

    }, [ page ]);

    return (
        <>
            <TableSection
                info={info}
                actions={[
                    {
                        title: 'Create New BBCode',
                        icon: 'fa fa-plus-circle',
                        onClick: openCreateModal
                    }
                ]}
                headers={[ 'Name', 'Description', 'Matches', 'Replace', 'Example', 'Created', 'Updated', 'Edit', 'Delete' ]}
                rows={rows}
            />
            <Pages
                pageTotal={pageTotal}
                base={'/forums/bbcodes'}
            />
        </>
    )
}

const validateOptions = {
    name: {
        required: true,
        name: 'Name',
        min: 3,
        max: 50,
    },
    description: {
        required: true,
        name: 'Description',
        min: 3,
        max: 100,
    },
    matches: {
        required: true,
        name: 'Matches',
        type: ['string'],
        min: 3,
        max: 150,
    },
    replace: {
        required: true,
        name: 'Replace',
        min: 3,
        max: 150,
    },
    example: {
        required: true,
        name: 'Example',
        min: 3,
        max: 150,
    },
};
