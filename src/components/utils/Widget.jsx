import React from 'react'

export default function Widget({ title, children }) {
    return (
        <div className="widget">
            <div className="header">
                <h4>{title}</h4>
            </div>
            <div className="content">
                {children}
            </div>
        </div>
    )
}
