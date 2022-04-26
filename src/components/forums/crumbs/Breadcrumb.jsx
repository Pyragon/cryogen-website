import React from 'react';

import { Link } from 'react-router-dom';

export default function Breadcrumb({ id, title, link, icon, separator }) {
    return (
        <span key={id} className="breadcrumb">
            { icon && <i className={icon}/> }
            { link ?
                    <Link className="link" to={link}>{title}</Link>
                : 
                    <span style={{color: 'white'}}>{title}</span>
            }
            { separator && <span>{separator}</span> }
        </span>
    )
}
