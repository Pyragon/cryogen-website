import React from 'react'

const LabelInput = React.forwardRef(({ className="", title, description, value, placeholder="", type="text", setState}, ref) => {
    return (
        <div className="input-container" style={{gridTemplateColumns: '1fr'}}>
            <div className='input-title'>
                <p>
                    {title+":"}
                    { description && <span className='input-description'>{' - '+description}</span>}
                </p>
            </div>
            <input ref={ref} className={"input "+className} value={value} type={type} placeholder={placeholder} onChange={e => setState(e.target.value)}/>
        </div>
    )
});

export default LabelInput;
