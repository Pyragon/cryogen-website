import React, { useEffect, useState, useContext } from 'react'
import axios from '../../../utils/axios'

import CollapsibleWidget from '../../utils/CollapsibleWidget'
import SubforumBlock from './SubforumBlock';

import NotificationContext from '../../../utils/contexts/NotificationContext';

export default function Categories() {
    let [ subforums, setSubforums ] = useState([]);

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let load = async () => {

            try {

                let res = await axios.get('/forums/subforums');

                console.log(res.data);

                setSubforums(res.data.subforums);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        load();

    }, []);
    return (
        <>
            { subforums.map((subforum, index) => 
                <CollapsibleWidget 
                    key={subforum._id}
                    title={subforum.name}
                    description={subforum.description}
                    index={index}
                >
                    <SubforumBlock forum={subforum} />
                </CollapsibleWidget>
            )} 
        </> 
    )
}
