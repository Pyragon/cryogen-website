import React, { useEffect, useState } from 'react'
import axios from '../../../utils/axios'

import CollapsibleWidget from '../../utils/CollapsibleWidget'
import SubforumBlock from './SubforumBlock';

export default function Categories() {
    let [ subforums, setSubforums ] = useState([]);
    useEffect(() => {
        axios.get('http://localhost:8081/forums/subforums')
            .then(res => setSubforums(res.data));
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