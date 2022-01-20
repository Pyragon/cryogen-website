import React from 'react'

export default function LabelInput({ className="", title, description, value, placeholder="", type="text", setState}) {
    return (
        <div className="input-container" style={{gridTemplateColumns: '1fr'}}>
            <div className='input-title'>
                <p>
                    {title+":"}
                    { description && <span className='input-description'>{' - '+description}</span>}
                </p>
            </div>
            <input className={"input "+className} value={value} type={type} placeholder={placeholder} onChange={e => setState(e.target.value)}/>
        </div>
    )
}
