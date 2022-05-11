import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../../utils/axios';

import CollapsibleWidget from '../../utils/CollapsibleWidget'
import Button from '../../utils/Button';
import SubforumBlock from './SubforumBlock'
import ThreadBlock from '../threads/ThreadBlock';

import UserContext from '../../../utils/contexts/UserContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';
import PageContext from '../../../utils/contexts/PageContext';
import Pages from '../../utils/Pages';

export default function ViewForum({ forum }) {

    let [ subforums, setSubforums ] = useState([]);
    let [ threads, setThreads ] = useState([]);
    let [ pageTotal, setPageTotal ] = useState(1);
    let { user } = useContext(UserContext);
    let { page } = useContext(PageContext);
    let navigate = useNavigate();

    let { sendErrorNotification } = useContext(NotificationContext);

    useEffect(() => {

        let load = async () => {

            try {

                let res = await axios.get(`/forums/subforums/${forum._id}/${page}`);
                setSubforums(res.data.subforum.subforums);
                setThreads(res.data.threads);
                setPageTotal(res.data.pageTotal);

            } catch(error) {
                sendErrorNotification(error);
            }

        };

        load();
        
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
                    <Pages
                        pageTotal={pageTotal}
                        base={`/subforums/${forum._id}`}
                    />
                </>
            }
            
        </>
    )
}
