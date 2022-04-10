import React, { useContext } from 'react';
import LabelInput from '../../../utils/LabelInput';
import PollOption from './PollOption';

import NotificationContext from '../../../../utils/contexts/NotificationContext';

export default function PollOptions({options, setOptions, question, setQuestion}) {
    let { sendNotification } = useContext(NotificationContext);

    let createNewOption = () => {
        if(options.length === 6) {
            console.error('You can only have up to 6 answers.');
            sendNotification('You can only have up to 6 answers.');
            return;
        }
        let ref = React.createRef();
        setOptions(options => [...options, { ref, option: <PollOption key={options.length} index={options.length} ref={ref}/>}]);
    };
    return (
        <>
            <LabelInput 
                title="Poll Question" 
                value={question} 
                setState={setQuestion} 
            />
            <p className="input-title-p">
                Poll Options: 
                <span 
                    className="fa fa-plus-circle" 
                    style={{ marginLeft: '5px', fontSize: '.75rem', cursor: 'pointer' }}
                    title='Add a new answer field' 
                    onClick={createNewOption}
                />
            </p>
            { options.map(option => option.option) }
        </>
    )
}
