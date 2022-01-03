import React from 'react'

export default function Button({ title, onClick, className="", defaultClassName="btn "}) {
    return (
        <button className={defaultClassName+className} onClick={onClick}>{title}</button>
    )
}
