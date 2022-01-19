import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import UserContext from '../../../utils/UserContext';
import axios from '../../../utils/axios';

import CollapsibleWidget from '../../utils/CollapsibleWidget'
import Button from '../../utils/Button';
import SubforumBlock from './SubforumBlock'
import ThreadBlock from '../threads/ThreadBlock';

export default function ViewForum({ forum }) {
    let [ subforums, setSubforums ] = useState([]);
    let [ threads, setThreads ] = useState([]);
    let { user } = useContext(UserContext);
    let navigate = useNavigate();
    useEffect(() => {
        axios.get('http://localhost:8081/forums/subforums/children/' + forum._id)
            .then(res => setSubforums(res.data));
        axios.get('http://localhost:8081/forums/threads/children/' + forum._id)
            .then(res => setThreads(res.data));
    }, [forum]);
    return (
        <>
            { subforums.length > 0 && 
                <CollapsibleWidget 
                    title={'Subforums'}
                    index={0}
                >
                    <SubforumBlock forum={forum} />
                </CollapsibleWidget> 
            }
            { !forum.isCategory && 
                <>
                    { forum.permissions && forum.permissions.canCreateThreads(user) && (
                        <>
                            <Button 
                                title='New Thread' 
                                className='new-thread-btn' 
                                onClick={() => navigate(`/forums/${forum._id}/new`)} 
                            />
                            <div style={{clear: 'both'}} />
                        </>
                    ) }
                    <CollapsibleWidget 
                        title={forum.name}
                        description={forum.description}
                        index={1}
                    >
                        { 
                            threads.length === 0 ?
                                <p className="t-center">No threads have been created yet.</p>
                            :
                                threads.map((thread, index) => (
                                    <ThreadBlock key={index} thread={thread} index={index} />
                                ))
                        }
                    </CollapsibleWidget>
                </>
            }
            
        </>
    )
}
