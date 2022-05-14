import React from 'react'
import Input from './Input';

const LabelInput = React.forwardRef(({ className="", title, description, value, placeholder="", type="text", setState, onEnter, next }, ref) => {
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
                value={value} 
                setState={setState}
                onEnter={onEnter}
                next={next}
            />
        </div>
    )
});

export default LabelInput;
