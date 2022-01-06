import React from 'react'

export default function LabelInput({ className="", title, value, placeholder="", type="text", setState}) {
    return (
        <div className="input-container" style={{gridTemplateColumns: '1fr'}}>
            <p>{title+":"}</p>
            <input className={"input "+className} value={value} type={type} placeholder={placeholder} onChange={e => setState(e.target.value)}/>
        </div>
    )
}
