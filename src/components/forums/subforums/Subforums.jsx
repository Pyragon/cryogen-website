import React, { useEffect, useState } from 'react'

import CollapsibleWidget from '../../utils/CollapsibleWidget'
import SubforumBlock from './SubforumBlock';

export default function Categories() {
    let [ subforums, setSubforums ] = useState([]);
    useEffect(() => {
        fetch('http://localhost:8081/forums/subforums')
            .then(res => res.json())
            .then(res => setSubforums(res));
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
