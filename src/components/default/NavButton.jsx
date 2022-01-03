import React from 'react';

import { Link } from 'react-router-dom';

export default function NavButton({ title, link, onClick}) {
    if(onClick)
        return (<a className="nav-btn" href={link} onClick={onClick}>{title}</a>);
    return (<Link className="nav-btn" to={link}>{title}</Link>);
}
