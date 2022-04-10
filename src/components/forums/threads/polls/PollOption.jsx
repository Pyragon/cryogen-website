import React, { useState } from 'react';

import LabelInput from '../../../utils/LabelInput';

const PollOption = React.forwardRef(({index}, ref) => {
    let [ value, setValue ] = useState('');
    return (
        <LabelInput ref={ref} title={'Answer '+(index+1)} value={value} setState={setValue} />
    );
});

export default PollOption;
