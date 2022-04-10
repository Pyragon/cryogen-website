import React from 'react'

export default function Widget({ title, description, descriptions=[], style, children, className="" }) {
    return (
        <div className={"widget "+className} style={style}>
            <div className="header">
                <h4>{title}</h4>
                { description && <p className="small">{description}</p> }
                { descriptions.map(description => <p className="small">{description}</p>)}
            </div>
            <div className="content">
                {children}
            </div>
        </div>
    )
}
