import React from 'react'

export default function Button({ title, onClick}) {
    return (
        <button className="btn" onClick={onClick}>{title}</button>
    )
}
