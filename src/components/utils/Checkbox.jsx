import React from 'react'

export default function Checkbox({ title, value, setState, className }) {
    return (
        <div className={className}>
            <div>
                <input type="checkbox" checked={value} onChange={e => setState(e.target.checked)}/>
                <div style={{ display: 'inline-block', fontSize: '.85em', height: '19px', verticalAlign: 'middle'}}>{title}</div>
            </div>
        </div>
    )
}
