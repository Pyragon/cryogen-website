import React, { useState } from 'react'
import Input from './Input';

const LabelInput = React.forwardRef(({ className="", title, description, defaultValue, value, placeholder="", type="text", setState, onEnter, next }, ref) => {
    let text, setText;
    if(!value)
        [ text, setText ] = useState(defaultValue);
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
                value={value || text} 
                setState={newValue => {
                    if(!value) setText(newValue);
                    setState(newValue);
                }}
                onEnter={onEnter}
                next={next}
            />
        </div>
    )
});

export default LabelInput;
