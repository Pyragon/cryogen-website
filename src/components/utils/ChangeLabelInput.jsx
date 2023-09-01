import React, { useState } from 'react'
import Input from './Input';

const ChangeLabelInput = React.forwardRef(({ className="", title, description, defaultText, placeholder="", type="text", onEnter, next, stateChange }, ref) => {
    let [ text, setText ] = useState(defaultText);
    return (
        <div className="input-container" style={{gridTemplateColumns: '1fr'}}>
            <div className='input-title'>
                <p>
                    {title+":"}
                    { description && <span className='input-description'>{' - '+description}</span>}
                </p>
            </div>
            <Input 
                ref={ref} 
                type={type} 
                className={"input "+className} 
                placeholder={placeholder} 
                value={text} 
                setState={(value) => {
                    setText(value);
                    stateChange(value);
                }}
                onEnter={onEnter}
                next={next}
            />
        </div>
    )
});

export default ChangeLabelInput;
