import React from 'react'

export default function Widget({ title, description, style, children }) {
    return (
        <div className="widget" style={style}>
            <div className="header">
                <h4>{title}</h4>
                { description && <p className="small">{description}</p> }
            </div>
            <div className="content">
                {children}
            </div>
        </div>
    )
}
