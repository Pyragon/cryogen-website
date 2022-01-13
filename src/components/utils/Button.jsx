import React from 'react'

export default function Button({ title, onClick, className="", defaultClassName="btn ", children}) {
    children = children || title;
    return (
        <button className={defaultClassName+className} onClick={onClick}>{children}</button>
    )
}
